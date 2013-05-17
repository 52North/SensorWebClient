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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.server.oxf.util.ConfigurationContext.SERVER_TIMEOUT;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
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
import net.opengis.waterml.x20.MonitoringPointDocument;
import net.opengis.waterml.x20.MonitoringPointType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.connector.MetadataHandler;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.oxf.util.parser.ConnectorUtils;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class HydroMetadataHandler extends MetadataHandler {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HydroMetadataHandler.class);
    
	@Override
	public SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
		
		SOSMetadata metadata = initMetadata(sosUrl, sosVersion);
		TimeseriesParametersLookup lookup = metadata.getTimeseriesParamtersLookup();
		
		// get a waterml specific responseFormat if set
		String responseFormat = ConnectorUtils.getResponseFormat(getServiceDescriptor(), "waterml");
		if (responseFormat != null) {
			metadata.setOmVersion(responseFormat);
		}

		Collection<SosTimeseries> timeserieses = createTimeserieses();
		
		// execute the GetFeatureOfInterest requests
		Map<SosTimeseries, FutureTask<OperationResult>> futureTasks = new HashMap<SosTimeseries, FutureTask<OperationResult>>();
		for (SosTimeseries timeseries : timeserieses) {
			// create the category for every parameter constellation out of phenomenon and procedure
			String category = getLastPartOf(timeseries.getPhenomenon()) + " (" + getLastPartOf(timeseries.getProcedure()) + ")";
			timeseries.setCategory(category);
			futureTasks.put(timeseries, new FutureTask<OperationResult>(createGetFoiAccess(sosUrl, sosVersion, timeseries)));
		}
		
        int counter = futureTasks.size();
        AReferencingHelper referenceHelper = createReferencingHelper();
		LOGGER.info("Sending " + counter + " GetFeatureOfInterest requests");
		for (SosTimeseries paramConst: futureTasks.keySet()) {
			LOGGER.info("Sending #{} GetFeatureOfInterest request for Offering " + paramConst.getOffering(), counter--);
			AccessorThreadPool.execute(futureTasks.get(paramConst));
			try {
				FutureTask<OperationResult> futureTask = futureTasks.get(paramConst);
				OperationResult opRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest with parameter constellation: " + paramConst + "!");
				}
				GetFeatureOfInterestResponseDocument foiResDoc = getFOIResponseOfOpResult(opRes);
				String id = null;
				String label = null;
				ParsedPoint point = null;
				for (FeaturePropertyType featurePropertyType : foiResDoc.getGetFeatureOfInterestResponse().getFeatureMemberArray()) {
					XmlCursor xmlCursor = featurePropertyType.newCursor();
					if (xmlCursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0", "SF_SpatialSamplingFeature"))){
						SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(xmlCursor.getDomNode());
						SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
						id = sfSamplingFeature.getIdentifier().getStringValue();
						if (sfSamplingFeature.getNameArray().length > 0) {
							label = sfSamplingFeature.getNameArray(0).getStringValue();
						} else {
							label = id;
						}
						point = createParsedPoint(sfSamplingFeature, referenceHelper);
					} else if (xmlCursor.toChild(new QName("http://www.opengis.net/waterml/2.0", "MonitoringPoint"))){
						MonitoringPointDocument monitoringPointDoc = MonitoringPointDocument.Factory.parse(xmlCursor.getDomNode());
						MonitoringPointType monitoringPoint = monitoringPointDoc.getMonitoringPoint();
						id = monitoringPoint.getIdentifier().getStringValue();
						if(monitoringPoint.getNameArray().length > 0) {
							label = monitoringPoint.getNameArray(0).getStringValue();
						} else {
							label = id;
						}
						point = createParsedPoint(monitoringPoint, referenceHelper);
					} else {
						LOGGER.error("Don't find supported feature members in the GetFeatureOfInterest response");
					}
					if (point == null) {
						LOGGER.warn("The foi with ID {} has no valid point", id);
					} else {
						// add feature
						Feature feature = new Feature(id);
						feature.setLabel(label);
	                    lookup.addFeature(feature);
	                    
	                    // create station if not exists
	                    Station station = metadata.getStation(id);
	                    if (station == null) {
	                        double lat = Double.parseDouble(point.getLat());
		                    double lng = Double.parseDouble(point.getLon());
		                    EastingNorthing coords = new EastingNorthing(lng, lat, point.getSrs());
	                        station = new Station(id);
	                        station.setLocation(coords);
	                        metadata.addStation(station);
	                    }
	                    
	                    SosTimeseries tmp = paramConst.clone();
	                    tmp.setFeature(id);
	                    station.addTimeseries(tmp);
					}
				}
			} catch (TimeoutException e) {
				LOGGER.error("Timeout occured.", e);
			}

		}
		
		LOGGER.info("{} stations are created", metadata.getStations().size());
		
		metadata.setHasDonePositionRequest(true);
		return new SOSMetadataResponse(metadata);
	}
	
	private String getLastPartOf(String phenomenonId) {
        return phenomenonId.substring(phenomenonId.lastIndexOf("/") + 1);
    }

	private ParsedPoint createParsedPoint(XmlObject feature, AReferencingHelper referenceHelper) throws XmlException {
		ParsedPoint point = new ParsedPoint();
		XmlCursor cursor = feature.newCursor();
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
                point.setLon(lonLat[1]);
                point.setLat(lonLat[0]);
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

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, SosTimeseries timeseries) throws OXFException {
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("phenomenon", timeseries.getPhenomenon());
        container.addParameterShell("procedure", timeseries.getProcedure());
        Operation operation = new Operation(GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		return new OperationAccessor(getSosAdapter(), operation, container);
	}

}