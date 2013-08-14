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

package org.n52.server.sos.connector.eea;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
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
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.MetadataHandler;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.parser.ConnectorUtils;
import org.n52.server.parser.utils.ParsedPoint;
import org.n52.io.crs.CRSUtils;
import org.n52.io.crs.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class ArcGISSoeMetadataHandler extends MetadataHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISSoeMetadataHandler.class);

	@Override
	public SOSMetadata performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
		SOSMetadata metadata = initMetadata(sosUrl, sosVersion);
		TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
		
        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries();
        
        // TODO send DescribeSensor for every procedure to get the UOM, when the EEA-SOS deliver the uom

        CRSUtils referenceHelper = createReferencingHelper();
        Map<String, String> offeringBBoxMap = getOfferingBBoxMap();
        Map<SosTimeseries, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<SosTimeseries, FutureTask<OperationResult>>();
        for (SosTimeseries timeseries : observingTimeseries) {
        	String bboxString = offeringBBoxMap.get(timeseries.getOfferingId());
        	futureTasks.put(timeseries,	new FutureTask<OperationResult>(createGetFoiAccess(sosUrl, sosVersion, bboxString, timeseries)));
		}
		// execute the GetFeatureOfInterest requests
		LOGGER.debug("Sending " + futureTasks.size() + " GetFeatureOfInterest requests");
		for (SosTimeseries timeseries : futureTasks.keySet()) {
		    LOGGER.debug("Sending request for " + timeseries);
			AccessorThreadPool.execute(futureTasks.get(timeseries));
			try {
				FutureTask<OperationResult> futureTask = futureTasks.get(timeseries);
				OperationResult opRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest " + timeseries + "!");
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
						// create station if not exists
						Station station = metadata.getStation(id);
						if (station == null) {
							ParsedPoint point = getPointOfSamplingFeatureType(sfSamplingFeature, referenceHelper);
	                        double lat = Double.parseDouble(point.getLat());
		                    double lng = Double.parseDouble(point.getLon());
		                    EastingNorthing coords = new EastingNorthing(lat, lng, point.getSrs());
	                        station = new Station(id);
	                        station.setLocation(coords);
	                        metadata.addStation(station);
						}
                        // add feature
						String label;
						if (sfSamplingFeature.getNameArray().length > 0) {
							label = sfSamplingFeature.getNameArray(0).getStringValue();
						} else {
							label = id;
						}
						Feature feature = new Feature(id);
						feature.setLabel(label);
                        lookup.addFeature(feature);
                        
                        SosTimeseries tmp = timeseries.clone();
                        tmp.setFeature(new Feature(id));
                        station.addTimeseries(tmp);
					}
				}
			} catch (TimeoutException e) {
				LOGGER.error("Timeout occured.", e);
			} finally {
				futureTasks.remove(timeseries);
			}
		} 

        infoLogServiceSummary(metadata);
		metadata.setHasDonePositionRequest(true);
		return metadata;
	}

	public ParsedPoint getPointOfSamplingFeatureType(SFSamplingFeatureType sfSamplingFeature, CRSUtils referenceHelper) throws XmlException {
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
	
	private Map<String, String> getOfferingBBoxMap() throws OXFException {
		Map<String, String> offeringBBox = new HashMap<String, String>();
		Contents contents = getServiceDescriptorContent();
		for (String dataIdent : contents.getDataIdentificationIDArray()) {
			ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(dataIdent);
			String key = offering.getIdentifier(); 
			String bboxString = createBboxString(ConnectorUtils.createBbox(null, offering), createReferencingHelper());
			offeringBBox.put(key, bboxString);
		}
		return offeringBBox;
	}

	public String createBboxString(IBoundingBox bbox, CRSUtils referenceHelper) {
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

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String bboxString, SosTimeseries timeseries) throws OXFException {
		SOSAdapter adapter = new SOSAdapterByGET(sosVersion);
		Operation operation = new Operation(SOSAdapter.GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("phenomenon", timeseries.getPhenomenonId());
        container.addParameterShell("procedure", timeseries.getProcedureId());
        container.addParameterShell("bbox", bboxString);
		return new OperationAccessor(adapter, operation, container);
	}

	@Override
	public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
		throw new UnsupportedOperationException();
	}

}
