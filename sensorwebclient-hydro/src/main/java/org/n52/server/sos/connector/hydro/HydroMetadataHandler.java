/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.server.sos.connector.hydro;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;
import static org.n52.server.sos.connector.hydro.SOSwithSoapAdapter.GET_DATA_AVAILABILITY;

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
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.MetadataHandler;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.parser.ConnectorUtils;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosService;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public class HydroMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HydroMetadataHandler.class);

    public HydroMetadataHandler(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public void assembleTimeseriesMetadata(TimeseriesProperties properties) throws Exception {

        // TODO use different request strategy to obtain metadata/uom when SOS supports HydroProfile
        // (HyProfile must request an Observation (without timestamp we get the last value))
        // ==> move metadata obtaining strategy to MetadataHandler class: a different strategy can
        // be used by overriding the default (metadata via SensorML)
        
    }

    @Override
    public SOSMetadata performMetadataCompletion() throws Exception {
        SOSMetadata metadata = initMetadata();
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
        initMetadata();
        collectTimeseries(newMetadata);
        return newMetadata;
    }

    private void collectTimeseries(SOSMetadata metadata) throws OXFException,
            InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {

        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(metadata.getServiceUrl());

        Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks = new HashMap<SosTimeseries, FutureTask<OperationResult>>();
        Map<String, FutureTask<OperationResult>> getFoiAccessTasks = new HashMap<String, FutureTask<OperationResult>>();

        // create tasks by iteration over procedures
        for (SosTimeseries timeserie : observingTimeseries) {
            String procedureID = timeserie.getProcedureId();
            getFoiAccessTasks.put(procedureID,
                                  new FutureTask<OperationResult>(createGetFoiAccess(metadata.getServiceUrl(),
                                                                                     metadata.getVersion(),
                                                                                     procedureID)));
            getDataAvailabilityTasks.put(timeserie,
                                         new FutureTask<OperationResult>(createGDAAccess(metadata.getServiceUrl(),
                                                                                         metadata.getVersion(),
                                                                                         timeserie)));
        }

        // create list of timeseries of GDA requests
        Collection<SosTimeseries> timeseries = executeGDATasks(getDataAvailabilityTasks, metadata);

        // iterate over tasks of getFOI and add them to metadata
        executeFoiTasks(getFoiAccessTasks, metadata);

        // iterate over timeseries and add them to station with according feature id
        for (SosTimeseries timeserie : timeseries) {
            String feature = timeserie.getFeatureId();
            Station station = metadata.getStation(feature);
            if (station != null) {
                station.addTimeseries(timeserie);
            }
            else {
                LOGGER.warn("{} not added! No station for feature '{}'.", timeserie, feature);
            }
        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
    }

    private Collection<SosTimeseries> executeGDATasks(Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks,
                                                      SOSMetadata metadata) throws InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {
        int counter = getDataAvailabilityTasks.size();
        LOGGER.debug("Sending " + counter + " GetDataAvailability requests");
        Collection<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        for (SosTimeseries timeserie : getDataAvailabilityTasks.keySet()) {
            LOGGER.debug("Sending #{} GetDataAvailability request for procedure " + timeserie.getProcedureId(),
                         counter--);
            FutureTask<OperationResult> futureTask = getDataAvailabilityTasks.get(timeserie);
            AccessorThreadPool.execute(futureTask);
            OperationResult result = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
            if (result == null) {
                LOGGER.error("Get no result for GetDataAvailability with parameter constellation: " + timeserie + "!");
            }
            XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
            timeseries.addAll(getAvailableTimeseries(result_xb, timeserie, metadata));
        }
        return timeseries;
    }

    private void executeFoiTasks(Map<String, FutureTask<OperationResult>> getFoiAccessTasks, SOSMetadata metadata) throws InterruptedException,
            ExecutionException,
            XmlException,
            IOException,
            OXFException {
        int counter;
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        counter = getFoiAccessTasks.size();
        CRSUtils referenceHelper = createReferencingHelper();
        LOGGER.debug("Sending {} GetFeatureOfInterest requests", counter);
        for (String procedureID : getFoiAccessTasks.keySet()) {
            LOGGER.debug("Sending #{} GetFeatureOfInterest request for procedure '{}'", counter--, procedureID);
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
                for (FeaturePropertyType featurePropertyType : foiResDoc.getGetFeatureOfInterestResponse().getFeatureMemberArray()) {
                    Point point = null;
                    XmlCursor xmlCursor = featurePropertyType.newCursor();
                    if (xmlCursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0",
                                                    "SF_SpatialSamplingFeature"))) {
                        SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(xmlCursor.getDomNode());
                        SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
                        id = sfSamplingFeature.getIdentifier().getStringValue();
                        if (sfSamplingFeature.getNameArray().length > 0) {
                            label = sfSamplingFeature.getNameArray(0).getStringValue();
                        }
                        else {
                            label = id;
                        }
                        point = createParsedPoint(sfSamplingFeature, referenceHelper);
                    }
                    else if (xmlCursor.toChild(new QName("http://www.opengis.net/waterml/2.0", "MonitoringPoint"))) {
                        MonitoringPointDocument monitoringPointDoc = MonitoringPointDocument.Factory.parse(xmlCursor.getDomNode());
                        MonitoringPointType monitoringPoint = monitoringPointDoc.getMonitoringPoint();
                        id = monitoringPoint.getIdentifier().getStringValue();
                        if (monitoringPoint.getNameArray().length > 0) {
                            label = monitoringPoint.getNameArray(0).getStringValue();
                        }
                        else {
                            label = id;
                        }
                        point = createParsedPoint(monitoringPoint, referenceHelper);
                    }
                    else {
                        LOGGER.error("Don't find supported feature members in the GetFeatureOfInterest response");
                    }
                    if (point == null) {
                        LOGGER.warn("The foi with ID {} has no valid point", id);
                    }
                    else {
                        // if (metadata.getStations().size() > 10) {
                        // break;
                        // }
                        // add feature
                        Feature feature = new Feature(id, metadata.getServiceUrl());
                        feature.setLabel(label);
                        lookup.addFeature(feature);

                        // create station if not exists
                        Station station = metadata.getStation(id);
                        if (station == null) {
                            station = new Station(id, metadata.getServiceUrl());
                            station.setLocation(point);
                            metadata.addStation(station);
                        }
                    }
                }
            }
            catch (TimeoutException e) {
                LOGGER.error("Timeout occured.", e);
            }
        }
    }

    private Collection<SosTimeseries> getAvailableTimeseries(XmlObject result_xb,
                                                             SosTimeseries timeserie,
                                                             SOSMetadata metadata) throws XmlException, IOException {
        ArrayList<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        String queryExpression = "declare namespace sos='http://www.opengis.net/sos/2.0'; $this/sos:GetDataAvailabilityResponse/sos:dataAvailabilityMember";
        XmlObject[] response = result_xb.selectPath(queryExpression);
        for (XmlObject xmlObject : response) {
            SosTimeseries addedtimeserie = new SosTimeseries();
            String feature = getAttributeOfChildren(xmlObject, "featureOfInterest", "href");
            String phenomenon = getAttributeOfChildren(xmlObject, "observedProperty", "href");
            String procedure = getAttributeOfChildren(xmlObject, "procedure", "href");
            addedtimeserie.setFeature(new Feature(feature, metadata.getServiceUrl()));
            addedtimeserie.setPhenomenon(new Phenomenon(phenomenon, metadata.getServiceUrl()));
            addedtimeserie.setProcedure(new Procedure(procedure, metadata.getServiceUrl()));
            // create the category for every parameter constellation out of phenomenon and procedure
            String category = getLastPartOf(phenomenon) + " (" + getLastPartOf(procedure) + ")";
            addedtimeserie.setCategory(new Category(category, metadata.getServiceUrl()));
            addedtimeserie.setOffering(new Offering(timeserie.getOfferingId(), metadata.getServiceUrl()));
            addedtimeserie.setSosService(new SosService(timeserie.getServiceUrl(), metadata.getVersion()));
            addedtimeserie.getSosService().setLabel(metadata.getTitle());
            timeseries.add(addedtimeserie);
        }
        return timeseries;
    }

    private String getAttributeOfChildren(XmlObject xmlObject, String child, String attribute) {
        SimpleValue childObject = ((org.apache.xmlbeans.SimpleValue) xmlObject.selectChildren("http://www.opengis.net/om/2.0",
                                                                                              child)[0].selectAttribute("http://www.w3.org/1999/xlink",
                                                                                                                        attribute));
        return childObject.getStringValue();
    }

    private String getLastPartOf(String phenomenonId) {
        return phenomenonId.substring(phenomenonId.lastIndexOf("/") + 1);
    }

    private Point createParsedPoint(XmlObject feature, CRSUtils referenceHelper) throws XmlException {
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

                Double lon = Double.parseDouble(lonLat[0]);
                Double lat = Double.parseDouble(lonLat[1]);
                Double alt = Double.NaN;
                if (lonLat.length == 3) {
                    alt = Double.parseDouble(lonLat[2]);
                }
                try {
                    String srs = referenceHelper.extractSRSCode(srsName);
                    Point point = referenceHelper.createPoint(lon, lat, alt, srs);
                    return referenceHelper.transformOuterToInner(point, srs);
                }
                catch (FactoryException e) {
                    LOGGER.warn("Could not create intern CRS.", e);
                }
                catch (TransformException e) {
                    LOGGER.warn("Could not transform to intern CRS.", e);
                }
            }
        }
        return null;
    }

    private GetFeatureOfInterestResponseDocument getFOIResponseOfOpResult(OperationResult opRes) throws XmlException,
            IOException,
            OXFException {
        XmlObject foiResponse = XmlObject.Factory.parse(opRes.getIncomingResultAsStream());
        if (foiResponse instanceof GetFeatureOfInterestResponseDocument) {
            return (GetFeatureOfInterestResponseDocument) foiResponse;
        }
        else {
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

    private Callable<OperationResult> createGDAAccess(String sosUrl, String version, SosTimeseries timeserie) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("procedure", timeserie.getProcedureId());
        container.addParameterShell("version", version);
        Operation operation = new Operation(GET_DATA_AVAILABILITY, sosUrl, sosUrl);
        return new OperationAccessor(getSosAdapter(), operation, container);
    }

}
