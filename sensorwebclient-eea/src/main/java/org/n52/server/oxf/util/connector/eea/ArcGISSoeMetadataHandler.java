/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.server.oxf.util.connector.eea;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.namespace.QName;

import net.opengis.gml.x32.AbstractGeometryType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.impl.PointTypeImpl;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.connector.MetadataHandler;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.oxf.util.parser.ConnectorUtils;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class ArcGISSoeMetadataHandler extends MetadataHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISSoeMetadataHandler.class);

    private static final String FOI_WILDCARD = "FOI_WILDCARD";
    
	@Override
	public SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
        SOSAdapter adapter = new SOSAdapterByGET(sosVersion);
        ServiceDescriptor serviceDesc = ConnectorUtils.getServiceDescriptor(sosUrl, adapter);
        String sosTitle = serviceDesc.getServiceIdentification().getTitle();
        
        // TODO test with OperationsMetadata (missing in the Capabilities of the EEA-SOS
		// String omFormat = ConnectorUtils.getOMFormat(serviceDesc);
		// String smlVersion = ConnectorUtils.getSMLVersion(serviceDesc, sosVersion);
		String omFormat = "http://www.opengis.net/om/2.0";
		//String smlVersion = "http://www.opengis.net/sensorML/2.0"; // http://www.opengis.net/sensorML/2.0
		String smlVersion = "http://www.opengis.net/sensorML/1.0.1"; // http://www.opengis.net/sensorML/1.0.1

		ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle, sosVersion, omFormat, smlVersion);
        
        SOSMetadata metadata = (SOSMetadata) ConfigurationContext.getServiceMetadata(sosUrl);
        
        IBoundingBox sosBbox = null;
		Map<Station, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<Station, FutureTask<OperationResult>>();
		Contents contents = serviceDesc.getContents();
        for (String dataIdent : contents.getDataIdentificationIDArray()) {
			ObservationOffering Observationoffering = (ObservationOffering) contents.getDataIdentification(dataIdent);
			String offeringId = Observationoffering.getIdentifier();

			sosBbox = ConnectorUtils.createBbox(sosBbox, Observationoffering);

			String[] phenArray = Observationoffering.getObservedProperties();
			String[] procArray = Observationoffering.getProcedures();
			
			// add offering
			Offering offering = new Offering(offeringId);
			offering.setLabel(Observationoffering.getTitle());
            metadata.addOffering(offering);
			
			// add phenomenons
			for (String phenomenonId : phenArray) {
				Phenomenon phenomenon = new Phenomenon(phenomenonId);
				phenomenon.setLabel(phenomenonId.substring(phenomenonId.indexOf("#") + 1));
                metadata.addPhenomenon(phenomenon);
			}
			
			// add procedures
			for (String procedureId : procArray) {
				metadata.addProcedure(new Procedure(procedureId));
			}

			ArrayList<String> fois = new ArrayList<String>();
			fois.add(FOI_WILDCARD);

	        AReferencingHelper referenceHelper = createReferencingHelper(metadata); 
			String bboxString = createBboxString(ConnectorUtils.createBbox(null, Observationoffering), referenceHelper);
			// add station
			for (String procedure : procArray) {
				for (String phenomenon : phenArray) {
					for (String foi : fois) {
						Station station = new Station();
						station.setPhenomenon(phenomenon);
						station.setProcedure(procedure);
						station.setOffering(offeringId);
						station.setFeature(foi);
						metadata.addStation(station);
						futureTasks.put(station, new FutureTask<OperationResult>(createGetFoiAccess(sosUrl, sosVersion, bboxString, station)));
					}
				}
			}
        }

        // add srs
        try {
            if (!sosBbox.getCRS().startsWith("EPSG")) {
                String[] crsParts = sosBbox.getCRS().split(":");
                String epsgCode = "EPSG:" + crsParts[crsParts.length - 1];
                metadata.setSrs(epsgCode);
            } else {
                metadata.setSrs(sosBbox.getCRS());
            }
        } catch (Exception e) {
            LOGGER.error("Could not insert spatial metadata", e);
        }
        
        // TODO send DescribeSensor for every procedure to get the UOM, when the EEA-SOS deliver the uom


        AReferencingHelper referenceHelper = createReferencingHelper(metadata); 
        
		// execute the GetFeatureOfInterest requests
		LOGGER.debug("Sending " + futureTasks.size() + " GetFeatureOfInterest requests");
		for (Station station : futureTasks.keySet()) {
		    LOGGER.debug("Sending request for " + station);
			AccessorThreadPool.execute(futureTasks.get(station));
			try {
				FutureTask<OperationResult> futureTask = futureTasks.get(station);
				OperationResult opRes = futureTask.get(ConfigurationContext.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
				metadata.removeStation(station);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest " + station + "!");
				}
				XmlObject xmlObject = XmlObject.Factory.parse(opRes.getIncomingResultAsStream());
				if (xmlObject instanceof GetFeatureOfInterestResponseDocument) {
					GetFeatureOfInterestResponseDocument getFoiRespDoc = (GetFeatureOfInterestResponseDocument) xmlObject;
					GetFeatureOfInterestResponseType getFoiResp = getFoiRespDoc.getGetFeatureOfInterestResponse();
					FeaturePropertyType[] featureMemberArray = getFoiResp.getFeatureMemberArray();
					for (FeaturePropertyType featurePropType : featureMemberArray) {
						SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(featurePropType.xmlText());
						SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
						String id = sfSamplingFeature.getId();
						String label;
						if (sfSamplingFeature.getNameArray().length > 0) {
							label = sfSamplingFeature.getNameArray(0).getStringValue();
						} else {
							label = id;
						}
						FeatureOfInterest feature = new FeatureOfInterest(id);
						feature.setLabel(label);
                        metadata.addFeature(feature);
						Station clone = station.clone();
						ParsedPoint point = getPointOfSamplingFeatureType(sfSamplingFeature, referenceHelper);
	                    double lat = Double.parseDouble(point.getLat());
	                    double lng = Double.parseDouble(point.getLon());
	                    EastingNorthing coords = new EastingNorthing(lng, lat);
						clone.setLocation(coords, point.getSrs());
                        clone.setFeature(id);
						metadata.addStation(clone);
					}
				}
			} catch (TimeoutException e) {
				LOGGER.error("Timeout occured.", e);
			} finally {
				futureTasks.remove(station);
			}
		} 
		
		LOGGER.info("Retrieved #{} stations from SOS '{}'", metadata.getStations().size(), sosUrl);
		metadata.setHasDonePositionRequest(true);
		return new SOSMetadataResponse(metadata);

	}

	ParsedPoint getPointOfSamplingFeatureType(SFSamplingFeatureType sfSamplingFeature, AReferencingHelper referenceHelper) throws XmlException {
		ParsedPoint point = new ParsedPoint();
		XmlCursor cursor = sfSamplingFeature.newCursor();
		if (cursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0", "shape"))) {
			ShapeDocument shapeDoc = ShapeDocument.Factory.parse(cursor.getDomNode());
			AbstractGeometryType abstractGeometry = shapeDoc.getShape().getAbstractGeometry();
			if (abstractGeometry instanceof PointTypeImpl) {
				PointTypeImpl pointDoc = (PointTypeImpl) abstractGeometry;
				DirectPositionType pos = pointDoc.getPos();
				String srsName = pos.getSrsName(); 
				String[] lonLat = pos.getStringValue().split(" ");
				
		        String wgs84 = "EPSG:4326";
                point.setLon(lonLat[0]);
                point.setLat(lonLat[1]);
                point.setSrs(wgs84);
		        try {
					String srs = referenceHelper.extractSRSCode(srsName);
					GeometryFactory geometryFactory = referenceHelper.createGeometryFactory(srs);

	                Double x = Double.parseDouble(lonLat[0]);
	                Double y = Double.parseDouble(lonLat[1]);
					Coordinate coord = referenceHelper.createCoordinate(srs, x, y, null);
					
					Point createdPoint = geometryFactory.createPoint(coord);
					createdPoint = referenceHelper.transformToWgs84(createdPoint, srs);
					
					point = new ParsedPoint(createdPoint.getX() + "", createdPoint.getY() + "", wgs84);
				} catch (Exception e) {
					LOGGER.debug("Could not transform! Keeping old SRS: " + wgs84, e);
				}
			}
		}
		return point;
	}

	String createBboxString(IBoundingBox bbox, AReferencingHelper referenceHelper) {
		StringBuffer sb = new StringBuffer();
		sb.append("om:featureOfInterest/*/sams:shape,");
		sb.append(bbox.getLowerCorner()[0]).append(",");
		sb.append(bbox.getLowerCorner()[1]).append(",");
		sb.append(bbox.getUpperCorner()[0]).append(",");
		sb.append(bbox.getUpperCorner()[1]).append(",");
		int code = referenceHelper.getSrsIdFrom(bbox.getCRS());
		sb.append("urn:ogc:def:crs:EPSG::").append(code);
		return sb.toString();
	}

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String bboxString, Station station) throws OXFException {
		SOSAdapter adapter = new SOSAdapterByGET(sosVersion, null);
		Operation operation = new Operation(SOSAdapter.GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("phenomenon", station.getPhenomenon());
        container.addParameterShell("procedure", station.getProcedure());
        container.addParameterShell("bbox", bboxString);
		return new OperationAccessor(adapter, operation, container);
	}

}
