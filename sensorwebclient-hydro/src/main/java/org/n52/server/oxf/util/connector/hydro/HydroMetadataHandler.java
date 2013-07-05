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
import static org.n52.server.oxf.util.connector.hydro.SOSwithSoapAdapter.GET_DATA_AVAILABILITY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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

import org.apache.xmlbeans.SimpleValue;
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
import org.n52.shared.serializable.pojos.EastingNorthing;
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
import com.vividsolutions.jts.geom.PrecisionModel;

public class HydroMetadataHandler extends MetadataHandler {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HydroMetadataHandler.class);
    
	@Override
	public SOSMetadata performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
		SOSMetadata metadata = initMetadata(sosUrl, sosVersion);
		// get a waterml specific responseFormat if set
		String responseFormat = ConnectorUtils.getResponseFormat(getServiceDescriptor(), "waterml");
		if (responseFormat != null) {
			metadata.setOmVersion(responseFormat);
		}
		collectTimeseries(metadata);
		return metadata;
	}

	@Override
	public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
		SOSMetadata newMetadata = metadata.clone();
		initServiceDescription(newMetadata);
		collectTimeseries(newMetadata);
		return newMetadata;
	}

	private void collectTimeseries(SOSMetadata metadata) throws OXFException, InterruptedException,
			ExecutionException, TimeoutException, XmlException, IOException {

		Collection<SosTimeseries> observingTimeseries = createObservingTimeseries();
		
		Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks = new HashMap<SosTimeseries, FutureTask<OperationResult>>();
		Map<String, FutureTask<OperationResult>> getFoiAccessTasks = new HashMap<String, FutureTask<OperationResult>>();
		
		// create tasks by iteration over procedures
		for (SosTimeseries timeserie : observingTimeseries) {
			String procedureID = timeserie.getProcedure();
			getFoiAccessTasks.put(procedureID, new FutureTask<OperationResult>(createGetFoiAccess(metadata.getServiceUrl(), metadata.getVersion(), procedureID)));
			getDataAvailabilityTasks.put(timeserie, new FutureTask<OperationResult>(createGDAAccess(metadata.getServiceUrl(), timeserie)));
		}
		
		// create list of timeseries of GDA requests
		Collection<SosTimeseries> timeseries = executeGDATasks(getDataAvailabilityTasks);
		LOGGER.info("{} timeseries constellations are created", timeseries.size());
		
		// iterate over tasks of getFOI and add them to metadata
        executeFoiTasks(getFoiAccessTasks, metadata);
		
		// iterate over timeseries and add them to station with according feature id
        for (SosTimeseries timeserie : timeseries) {
			Station station = metadata.getStation(timeserie.getFeature());
			if (station != null) {
				station.addTimeseries(timeserie);	
			} else {
				LOGGER.error("Station with id {} doesn't exist", timeserie.getFeature());
			}
		}
		
		LOGGER.info("{} stations are created", metadata.getStations().size());
		
		metadata.setHasDonePositionRequest(true);
	}

	private Collection<SosTimeseries> executeGDATasks(
			Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks)
			throws InterruptedException, ExecutionException, TimeoutException, XmlException, IOException {
		int counter = getDataAvailabilityTasks.size();
		LOGGER.info("Sending " + counter + " GetDataAvailability requests");
		Collection<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
		for (SosTimeseries timeserie : getDataAvailabilityTasks.keySet()) {
			LOGGER.info("Sending #{} GetDataAvailability request for procedure " + timeserie.getProcedure(), counter--);
			FutureTask<OperationResult> futureTask = getDataAvailabilityTasks.get(timeserie);
			AccessorThreadPool.execute(futureTask);
			OperationResult result = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
			if (result == null) {
				LOGGER.error("Get no result for GetDataAvailability with parameter constellation: " + timeserie + "!");
			}
			XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
			timeseries.addAll(getAvailableTimeseries(result_xb, timeserie));
		}
		return timeseries;
	}

	private void executeFoiTasks(
			Map<String, FutureTask<OperationResult>> getFoiAccessTasks,
			SOSMetadata metadata)
			throws InterruptedException, ExecutionException, XmlException,
			IOException, OXFException {
		int counter;
		TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
		counter = getFoiAccessTasks.size();
        AReferencingHelper referenceHelper = createReferencingHelper();
		LOGGER.info("Sending " + counter + " GetFeatureOfInterest requests");
		for (String procedureID : getFoiAccessTasks.keySet()) {
			LOGGER.info("Sending #{} GetFeatureOfInterest request for procedure " + procedureID, counter--);
			FutureTask<OperationResult> futureTask = getFoiAccessTasks.get(procedureID);
			AccessorThreadPool.execute(futureTask);
			try {
				OperationResult opRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest with procedure: " + procedureID + "!");
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
//						if (metadata.getStations().size() > 10) {
//							break;
//						}
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
					}
				}
			} catch (TimeoutException e) {
				LOGGER.error("Timeout occured.", e);
			}
		}
	}
	
	private Collection<SosTimeseries> getAvailableTimeseries(XmlObject result_xb, SosTimeseries timeserie) throws XmlException, IOException {
		ArrayList<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
		String queryExpression = "declare namespace sos='http://www.opengis.net/sos/2.0'; $this/sos:GetDataAvailabilityResponse/sos:dataAvailabilityMember";
		XmlObject[] response = result_xb.selectPath(queryExpression);
		for (XmlObject xmlObject : response) {
			SosTimeseries addedtimeserie = new SosTimeseries();
			String feature = getAttributeOfChildren(xmlObject, "featureOfInterest", "href");
			String phenomenon = getAttributeOfChildren(xmlObject, "observedProperty", "href");
			String procedure = getAttributeOfChildren(xmlObject, "procedure", "href");
			addedtimeserie.setFeature(feature);
			addedtimeserie.setPhenomenon(phenomenon);
			addedtimeserie.setProcedure(procedure);
			// create the category for every parameter constellation out of phenomenon and procedure
			String category = getLastPartOf(phenomenon) + " (" + getLastPartOf(procedure) + ")";
			addedtimeserie.setCategory(category);
			addedtimeserie.setOffering(timeserie.getOffering());
			addedtimeserie.setServiceUrl(timeserie.getServiceUrl());
			timeseries.add(addedtimeserie);
		}
		return timeseries;
	}

	private String getAttributeOfChildren(XmlObject xmlObject, String child,
			String attribute) {
		SimpleValue childObject = ((org.apache.xmlbeans.SimpleValue) xmlObject.selectChildren("http://www.opengis.net/om/2.0",child)[0].selectAttribute("http://www.w3.org/1999/xlink",attribute));
		return childObject.getStringValue();
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

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String procedureID) throws OXFException {
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("procedure", procedureID);
        Operation operation = new Operation(GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		return new OperationAccessor(getSosAdapter(), operation, container);
	}

	private Callable<OperationResult> createGDAAccess(String sosUrl, SosTimeseries timeserie) throws OXFException {
		ParameterContainer container = new ParameterContainer();
        container.addParameterShell("procedure", timeserie.getProcedure());
        Operation operation = new Operation(GET_DATA_AVAILABILITY, sosUrl, sosUrl);
		return new OperationAccessor(getSosAdapter(), operation, container);
	}

}