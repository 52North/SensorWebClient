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
package org.n52.server.oxf.util.connector.hydro;

import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
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
import com.vividsolutions.jts.geom.PrecisionModel;

public class HydroMetadataHandler extends MetadataHandler {
	
	private static final String FOI_WILDCARD = "FOI_WILDCARD";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HydroMetadataHandler.class);
    
    private SOSAdapter adapter;
	
	@Override
	public SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
		this.adapter = new SOSwithSoapAdapter(sosVersion, new SoapSOSRequestBuilder_200());
		ServiceDescriptor serviceDesc = ConnectorUtils.getServiceDescriptor(sosUrl, this.adapter);

        String sosTitle = serviceDesc.getServiceIdentification().getTitle();
//		String omFormat = ConnectorUtils.getOMFormat(serviceDesc);
		String omFormat = "http://www.opengis.net/om/2.0";
//		String smlVersion = ConnectorUtils.getSMLVersion(serviceDesc, sosVersion);
		String smlVersion = "http://www.opengis.net/sensorML/1.0.1";
		ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle, sosVersion, omFormat, smlVersion);
		
		SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);

        Map<Station, FutureTask<OperationResult>> futureTasks = new HashMap<Station, FutureTask<OperationResult>>();
		
		Contents contents = serviceDesc.getContents();
		for (String dataIdent : contents.getDataIdentificationIDArray()) {
			ObservationOffering observationOffering = (ObservationOffering) contents.getDataIdentification(dataIdent);
			String offeringID = observationOffering.getIdentifier();
			
			if (metadata.getSrs() == null) {
                IBoundingBox bbox = ConnectorUtils.createBbox(observationOffering);
                metadata.setSrs(getSpatialReferenceSystem(bbox));
            }
            
			String[] phenomenons = observationOffering.getObservedProperties();
			String[] procedures = observationOffering.getProcedures();

			// add offering
			Offering offering = new Offering(offeringID);
			offering.setLabel(observationOffering.getTitle());
            metadata.addOffering(offering);
			
			// add phenomenons
			for (String phenomenonId : phenomenons) {
				for (String procedure : procedures) {
					String id = phenomenonId;
					Phenomenon phenomenon = new Phenomenon(id);
					String label = getLastPartOf(phenomenonId) + " by " + getLastPartOf(procedure); 
					phenomenon.setLabel(label);
	                metadata.addPhenomenon(phenomenon);
				}
			}
			
			// add procedures
			for (String procedure : procedures) {
				metadata.addProcedure(new Procedure(procedure));
			}

            // remove related features
			ArrayList<String> fois = new ArrayList<String>();
			for (String foi : observationOffering.getFeatureOfInterest()) {
				if (!foi.contains("related")) {
					fois.add(foi);
				}
			}
			// set foi wildcard to add them later
			if (fois.isEmpty()) {
				fois.add(FOI_WILDCARD);
			}
			
			// add station
			for (String procedure : procedures) {
				for (String phenomenon : phenomenons) {
					for (String foi : fois) {
						Station station = new Station();
						station.setPhenomenon(phenomenon);
						station.setProcedure(procedure);
						station.setOffering(offeringID);
						station.setFeature(foi);
                        String filter = getLastPartOf(phenomenon) + " (" + getLastPartOf(procedure) + ")";
                        station.setStationCategory(filter);
						metadata.addStation(station);
						if (fois.contains(FOI_WILDCARD)) {
							futureTasks.put(station, new FutureTask<OperationResult>(createGetFoiAccess(sosUrl, sosVersion, station)));
						}
					}
				}
			}
		}
        
		// execute the GetFeatureOfInterest requests
        int counter = futureTasks.size();
        AReferencingHelper referenceHelper = createReferencingHelper(metadata);
		LOGGER.info("Sending " + counter + " GetFeatureOfInterest requests");
		for (Station station: futureTasks.keySet()) {
			LOGGER.info("Sending #{} GetFeatureOfInterest request for Offering " + station.getOffering(), counter--);
			AccessorThreadPool.execute(futureTasks.get(station));
			try {
				FutureTask<OperationResult> futureTask = futureTasks.get(station);
				OperationResult opRes = futureTask.get(ConfigurationContext.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
				metadata.removeStation(station);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest " + station + "!");
				}
				GetFeatureOfInterestResponseDocument foiResDoc = getFOIResponseOfOpResult(opRes);
				for (FeaturePropertyType featurePropertyType : foiResDoc.getGetFeatureOfInterestResponse().getFeatureMemberArray()) {
					SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(featurePropertyType.xmlText());
					SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
					String id = sfSamplingFeature.getIdentifier().getStringValue();
					String label;
					if (sfSamplingFeature.getNameArray().length > 0) {
						label = sfSamplingFeature.getNameArray(0).getStringValue();
					} else {
						label = id;
					}
					ParsedPoint point = getPointOfSamplingFeatureType(sfSamplingFeature, referenceHelper);
					if (point == null) {
						LOGGER.warn("The foi with ID {} has no valid point", id);
					} else {
						FeatureOfInterest feature = new FeatureOfInterest(id);
						feature.setLabel(label);
	                    metadata.addFeature(feature);
						Station clone = station.clone();
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
			}

		}
		
		metadata.setHasDonePositionRequest(true);
		return new SOSMetadataResponse(metadata);
	}

    private String getSpatialReferenceSystem(IBoundingBox bbox) {
        try {
            if (!bbox.getCRS().startsWith("EPSG")) {
                return "EPSG:" + bbox.getCRS().split(":")[bbox.getCRS().split(":").length - 1];
            } else {
                return bbox.getCRS();
            }
        } catch (Exception e) {
            LOGGER.error("Could not insert spatial metadata", e);
            return "NA";
        }
    }

    private String getLastPartOf(String phenomenonId) {
        return phenomenonId.substring(phenomenonId.lastIndexOf("/") + 1);
    }

	private ParsedPoint getPointOfSamplingFeatureType(SFSamplingFeatureType sfSamplingFeature, AReferencingHelper referenceHelper) throws XmlException {
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
				if (lonLat[0].isEmpty()) {
					return null;
				}
				Double lon = Double.parseDouble(lonLat[1]);
				Double lat = Double.parseDouble(lonLat[0]);
		        String wgs84 = "EPSG:4326";
                point.setLon(lonLat[0]);
                point.setLat(lonLat[1]);
                point.setSrs(wgs84);
		        try {
					String srs = referenceHelper.extractSRSCode(srsName); 
					int srsID = referenceHelper.getSrsIdFromEPSG(srs);
					PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING);
					GeometryFactory geometryFactory = new GeometryFactory(pm, srsID);
					Coordinate coord = referenceHelper.createCoordinate(srs, lon, lat, null);
					Point createdPoint = geometryFactory.createPoint(coord);
					createdPoint = referenceHelper.transform(createdPoint, srs, wgs84);
					point = new ParsedPoint(createdPoint.getY() + "", createdPoint.getX() + "", wgs84); 
				} catch (Exception e) {
					LOGGER.debug("Could not transform! Keeping old SRS: " + wgs84, e);
				}
			}
		}
		return point;
	}
	
	private GetFeatureOfInterestResponseDocument getFOIResponseOfOpResult(OperationResult opRes)
			throws XmlException, IOException, OXFException {
		XmlObject foiResponse = XmlObject.Factory.parse(opRes.getIncomingResultAsStream());
		if (foiResponse instanceof GetFeatureOfInterestResponseDocument) {
			return (GetFeatureOfInterestResponseDocument) foiResponse;
		} else {
			throw new OXFException("No valid GetFeatureOfInterestREsponse");
		}
	}

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, Station station) throws OXFException {
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("phenomenon", station.getPhenomenon());
        container.addParameterShell("procedure", station.getProcedure());
        Operation operation = new Operation(GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		return new OperationAccessor(adapter, operation, container);
	}

}