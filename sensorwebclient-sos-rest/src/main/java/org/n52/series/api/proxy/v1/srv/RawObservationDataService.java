/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.io.v1.data.UndesignedParameterSet.createForSingleTimeseries;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;
import static org.n52.server.util.TimeUtil.createIso8601Formatter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.Interval;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.sensorweb.v1.spi.RawDataService;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.BadRequestException;
import org.n52.web.InternalServerException;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.gml.x32.FeatureCollectionDocument;
import net.opengis.gml.x32.FeatureCollectionType;
import net.opengis.om.x20.OMObservationPropertyType;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

/**
 * Process raw data query for observations/timeseries.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 1.7.3
 *
 */
public abstract class RawObservationDataService implements RawDataService {

    static final Logger LOGGER = LoggerFactory.getLogger(RawObservationDataService.class);

    private SimpleDateFormat dateFormat = createIso8601Formatter();

    private static XmlOptions xmlOptions = initXmlOptions();

    private static XmlOptions initXmlOptions() {
        XmlOptions xmlOptions = new XmlOptions();
        Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("http://www.opengis.net/gml/3.2", "gml");
        prefixMap.put("http://www.opengis.net/om/2.0", "om");
        prefixMap.put("http://www.opengis.net/waterml/2.0", "wml2");
        prefixMap.put("http://www.opengis.net/waterml-dr/2.0", "wml2dr");
        prefixMap.put("http://www.opengis.net/sampling/2.0", "sf");
        prefixMap.put("http://www.opengis.net/samplingSpatial/2.0", "sams");
        prefixMap.put("http://www.w3.org/1999/xlink", "xlink");
        prefixMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        prefixMap.put("http://www.w3.org/2001/XMLSchema", "xs");
        xmlOptions.setSaveSuggestedPrefixes(prefixMap);
        xmlOptions.setSaveImplicitNamespaces(prefixMap);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSaveNamespacesFirst();
        xmlOptions.setCharacterEncoding("UTF-8");
        return xmlOptions;
    }

    @Override
    public InputStream getRawData(String id, IoParameters query) {
        return getRawData(createForSingleTimeseries(id, query));
    }

    @Override
    public InputStream getRawData(UndesignedParameterSet parameters) {
        checkRawDataFormat(parameters.getRawFormat(), parameters.getTimeseries());
        if (parameters.getTimeseries().length > 1) {
            throw new BadRequestException("Querying raw timeseries data for multiple timeseries is not supported!");
        }
        Map<SOSMetadata, Set<String>> timeseriesMetadataMap = getTimeseriesMetadataMap(parameters);
        if (timeseriesMetadataMap.isEmpty()) {
            throw new ResourceNotFoundException("No data found for timeseries.");
        }
        List<OperationResult> result = queryObservationsForRequestedParameter(timeseriesMetadataMap, parameters);
        if (result.isEmpty()) {
            LOGGER.error("Get no result for GetObservation request");
            return null;
        }
        return getInputStreamFromOperationResult(result.get(0));
    }

    /**
     * @param timeseriesId
     *            the timeseries id to find the SOS metadata for.
     * @return the SOS metadata associated to the given timeseries or
     *         <code>null</code> if timeseries id is unknown.
     */
    private SOSMetadata getMetadataForTimeseriesId(String timeseriesId) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.containsStationWithTimeseriesId(timeseriesId)) {
                return metadata;
            }
        }
        return null;
    }

    private void checkRawDataFormat(String rawFormat, String id) {
        if (rawFormat != null && !rawFormat.isEmpty()) {
            SOSMetadata metadata = getMetadataForTimeseriesId(id);
            boolean valid = false;
            for (String format : metadata.getObservationFormats()) {
                if (format.equals(rawFormat)) {
                    valid = true;
                }
            }
            if (!valid) {
                throw new BadRequestException(
                        String.format("Requested rawFormat '%s' is not supported by timeseries '%s'!", rawFormat, id));
            }
        } else {
            throw new BadRequestException("The parameter 'rawFormat' is not set or empty!");
        }
    }

    private void checkRawDataFormat(String rawFormat, String[] timeseries) {
        for (String id : timeseries) {
            checkRawDataFormat(rawFormat, id);
        }
    }

    private List<OperationResult> queryObservationsForRequestedParameter(Map<SOSMetadata, Set<String>> map,
            UndesignedParameterSet parameters) {
        List<OperationResult> list = new ArrayList<OperationResult>(map.size());
        for (Entry<SOSMetadata, Set<String>> entry : map.entrySet()) {
            list.add(queryObservationForTimeseries(entry.getKey(), entry.getValue(), parameters));
        }
        return list;
    }

    private OperationResult queryObservationForTimeseries(SOSMetadata metadata, Set<String> timeseriesIds,
            UndesignedParameterSet parameters) {
        try {
            return executeGetObservationRequest(createGetObservationRequest(metadata, timeseriesIds, parameters), metadata);
        } catch (Exception e) {
            throw new InternalServerException(
                    String.format("Could not get raw observation data for timeseries '%s' and rawFormat '%s'!",
                            createStringFromSet(timeseriesIds), parameters.getRawFormat()),
                    e);
        }
    }

    private OperationResult executeGetObservationRequest(Callable<OperationResult> callable, SOSMetadata metadata)
            throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask<OperationResult> futureTask = new FutureTask<OperationResult>(callable);
        AccessorThreadPool.execute(futureTask);
        return futureTask.get(metadata.getTimeout(), TimeUnit.MILLISECONDS);
    }

    private Map<SOSMetadata, Set<String>> getTimeseriesMetadataMap(UndesignedParameterSet parameters) {
        Map<SOSMetadata, Set<String>> map = new HashMap<SOSMetadata, Set<String>>();

        for (String timeseriesId : parameters.getTimeseries()) {
            SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
                if (map.containsKey(metadata)) {
                    map.get(metadata).add(timeseriesId);
                } else {
                    Set<String> set = new HashSet<String>();
                    set.add(timeseriesId);
                    map.put(metadata, set);
                }
        }
        return map;
    }

    private SosTimeseries getTimeseries(SOSMetadata metadata, String timeseriesId) {
        Station station = metadata.getStationByTimeSeriesId(timeseriesId);
        return station.getTimeseriesById(timeseriesId);
    }

    private Callable<OperationResult> createGetObservationRequest(SOSMetadata metadata, Set<String> timeseriesIds,
            UndesignedParameterSet parameters) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        Map<String, String[]> map = getQueryParameter(metadata, timeseriesIds);
        addProcedure(container, map.get("proc"));
        addObservedProperty(container, map.get("obsProp"));
        addFeatureOfInterest(container, map.get("foi"));
        if (SosUtil.isVersion100(metadata.getVersion()) && map.get("off").length > 1) {
            // TODO split for SOS 1.0.0 and offering
            throw new BadRequestException(
                    "The requested service is of type SOS 1.0.0 and offering size is greater than 1!");
        }
        addOffering(container, map.get("off"));
        return createGetObservationRequest(metadata, container, parameters);
    }

    private Callable<OperationResult> createGetObservationRequest(SOSMetadata metadata, ParameterContainer container,
            UndesignedParameterSet parameters) throws OXFException {
        // set responseFormat
        container.addParameterShell("responseFormat", parameters.getRawFormat());
        // set temporal filter
        addTemporalFilter(container, parameters, metadata);
        container.addParameterShell("version", metadata.getVersion());
        container.addParameterShell("service", "SOS");
        Operation operation = new Operation("GetObservation", metadata.getServiceUrl(), metadata.getServiceUrl());
        return new OperationAccessor(SosAdapterFactory.createSosAdapter(metadata), operation, container);
    }

    private Map<String, String[]> getQueryParameter(SOSMetadata metadata, Set<String> timeseriesIds) {
        Set<String> procedures = new HashSet<String>();
        Set<String> phenomena = new HashSet<String>();
        Set<String> featureOfInterest = new HashSet<String>();
        Set<String> offering = new HashSet<String>();
        for (String timeseriesId : timeseriesIds) {
            SosTimeseries timeseries = getTimeseries(metadata, timeseriesId);
            procedures.add(timeseries.getProcedure().getProcedureId());
            phenomena.add(timeseries.getPhenomenon().getPhenomenonId());
            featureOfInterest.add(timeseries.getFeature().getFeatureId());
            offering.add(timeseries.getOffering().getOfferingId());
        }
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("proc", procedures.toArray(new String[0]));
        map.put("obsProp", phenomena.toArray(new String[0]));
        map.put("foi", featureOfInterest.toArray(new String[0]));
        map.put("off", offering.toArray(new String[0]));
        return map;
    }

    private void addProcedure(ParameterContainer container, String... procedures) throws OXFException {
        if (checkArray(procedures)) {
            container.addParameterShell("procedure", procedures);
        }
    }

    private void addOffering(ParameterContainer container, String... offerings) throws OXFException {
        if (checkArray(offerings)) {
            container.addParameterShell("offering", offerings);
        }
    }

    private void addObservedProperty(ParameterContainer container, String... observedProperties) throws OXFException {
        if (checkArray(observedProperties)) {
            container.addParameterShell("observedProperty", observedProperties);
        }
    }

    private void addFeatureOfInterest(ParameterContainer container, String... featureOfInterests) throws OXFException {
        if (checkArray(featureOfInterests)) {
            container.addParameterShell("featureOfInterest", featureOfInterests);
        }
    }

    private void addTemporalFilter(ParameterContainer container, UndesignedParameterSet parameters,
            SOSMetadata metadata) throws OXFException {
        if (parameters.getTimespan() != null && !parameters.getTimespan().isEmpty()) {
            String parameterName = null;
            if (SosUtil.isVersion100(metadata.getVersion())) {
                parameterName = "eventTime";
            } else if (SosUtil.isVersion200(metadata.getVersion())) {
                parameterName = "temporalFilter";
            }
            container.addParameterShell(parameterName, getTimeFrom(parameters));
        }
    }

    private boolean checkArray(String[] array) {
        return array != null && array.length > 0;
    }

    protected ITime getTimeFrom(UndesignedParameterSet parameters) {
        Interval timespan = Interval.parse(parameters.getTimespan());
        Calendar beginPos = Calendar.getInstance();
        beginPos.setTimeInMillis(timespan.getStartMillis());
        Calendar endPos = Calendar.getInstance();
        endPos.setTimeInMillis(timespan.getEndMillis());
        String begin = dateFormat.format(beginPos.getTime());
        String end = dateFormat.format(endPos.getTime());
        return TimeFactory.createTime(begin + "/" + end);
    }

    private InputStream getInputStreamFromOperationResult(OperationResult result) {
        try {
            XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsAutoCloseStream());
            if (result_xb instanceof GetObservationResponseDocument) {
                FeatureCollectionDocument fcd = FeatureCollectionDocument.Factory.newInstance(xmlOptions);
                FeatureCollectionType fct = fcd.addNewFeatureCollection();
                fct.setId(UUID.randomUUID().toString());
                GetObservationResponseDocument gord = (GetObservationResponseDocument) result_xb;
                if (gord.getGetObservationResponse() != null
                        && gord.getGetObservationResponse().getObservationDataArray() != null)
                    for (ObservationData od : gord.getGetObservationResponse().getObservationDataArray()) {
                        OMObservationPropertyType omopt = OMObservationPropertyType.Factory.newInstance(xmlOptions);
                        omopt.setOMObservation(od.getOMObservation());
                        fct.addNewFeatureMember().set(omopt);
                    }
                return new AutoCloseInputStream(
                        XmlObject.Factory.parse(fcd.xmlText(xmlOptions)).newInputStream(xmlOptions));
            }
        } catch (Exception e) {
            LOGGER.debug("Returned response is not XML formatted!");
        }
        return result.getIncomingResultAsAutoCloseStream();
    }

    private String createStringFromSet(Set<String> timeseriesIds) {
        StringBuilder builder = new StringBuilder();
        for (String string : timeseriesIds) {
            builder.append(string).append(", ");
        }
        return builder.substring(0, builder.lastIndexOf(","));
    }
}
