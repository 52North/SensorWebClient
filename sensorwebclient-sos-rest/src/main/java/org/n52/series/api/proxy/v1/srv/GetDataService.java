/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import static org.n52.io.v1.data.TimeseriesData.newTimeseriesData;
import static org.n52.io.v1.data.UndesignedParameterSet.createForSingleTimeseries;
import static org.n52.server.util.TimeUtil.createIso8601Formatter;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetFirstValue;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetLastValue;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.opengis.gml.x32.FeatureCollectionDocument;
import net.opengis.gml.x32.FeatureCollectionType;
import net.opengis.om.x20.OMObservationPropertyType;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.Interval;
import org.n52.client.service.TimeSeriesDataService;
import org.n52.io.IoParameters;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesDataMetadata;
import org.n52.io.v1.data.TimeseriesValue;
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
import org.n52.server.da.oxf.ResponseExceedsSizeLimitException;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.BadRequestException;
import org.n52.web.InternalServerException;
import org.n52.web.OptionNotSupported;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets data values from an SOS instance. Requested time series are aggregated
 * to a list of {@link TimeseriesProperties} and passed to a configured
 * {@link TimeSeriesDataService}. Data response will be enriched by further
 * metadata from each procedure measuring the requested time series.
 */
public class GetDataService extends DataService implements RawDataService {

	static final Logger LOGGER = LoggerFactory.getLogger(GetDataService.class);

	private TimeSeriesDataService timeSeriesDataService;
	
	private SimpleDateFormat dateFormat = createIso8601Formatter();
	
	private static XmlOptions xmlOptions = initXmlOptions();
	
	/**
	 * @param parameterSet
	 *            containing request parameters.
	 * @return a time series result instance, identified by
	 *         {@link SosTimeseries#getTimeseriesId()}
	 */
	public TvpDataCollection getTimeSeriesFromParameterSet(
			UndesignedParameterSet parameterSet) {
		ArrayList<TimeseriesProperties> tsProperties = new ArrayList<TimeseriesProperties>();
		TvpDataCollection timeseriesCollection = prepareTimeseriesResults(
				parameterSet, tsProperties);
		return performTimeseriesDataRequest(timeseriesCollection,
				createDesignOptions(parameterSet, tsProperties));
	}

	private TvpDataCollection performTimeseriesDataRequest(
			TvpDataCollection timeSeriesResults, DesignOptions options) {
		try {
			TimeSeriesDataRequest tsRequest = new TimeSeriesDataRequest(options);
			TimeSeriesDataResponse timeSeriesData = timeSeriesDataService
					.getTimeSeriesData(tsRequest);
			Map<String, HashMap<Long, Double>> data = timeSeriesData
					.getPayloadData();

			for (String timeseriesId : timeSeriesResults.getAllTimeseries()
					.keySet()) {
				TimeseriesProperties properties = getTimeseriesProperties(
						timeseriesId, options);
				GetDataInfos infos = new GetDataInfos(timeseriesId, properties,
						options);
				HashMap<Long, Double> values = data.get(timeseriesId);
				TimeseriesData timeseriesData = newTimeseriesData(values);
				if (properties.getReferenceValues() != null) {
					timeseriesData.setMetadata(createTimeseriesMetadata(infos));
				}
				timeSeriesResults
						.addNewTimeseries(timeseriesId, timeseriesData);
			}
		} catch (ResponseExceedsSizeLimitException e) {
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			throw new InternalServerException(
					"Could not get timeseries data for options: " + options, e);
		}
		return timeSeriesResults;
	}

	private TimeseriesDataMetadata createTimeseriesMetadata(GetDataInfos infos) {
		HashMap<String, ReferenceValue> refValues = infos.getProperties()
				.getRefvalues();
		if (refValues == null || refValues.isEmpty()) {
			return null;
		}
		TimeseriesDataMetadata timeseriesMetadata = new TimeseriesDataMetadata();
		timeseriesMetadata.setReferenceValues(createReferenceValuesData(
				refValues, infos));
		return timeseriesMetadata;
	}

	private Map<String, TimeseriesData> createReferenceValuesData(
			HashMap<String, ReferenceValue> refValues, GetDataInfos infos) {
		Map<String, TimeseriesData> refValuesDataCollection = new HashMap<String, TimeseriesData>();
		for (String referenceValueId : refValues.keySet()) {
			ReferenceValue referenceValue = refValues.get(referenceValueId);
			TimeseriesValue[] referenceValues = referenceValue.getValues().length == 1 ? fitReferenceValuesForInterval(
					referenceValue, infos) : referenceValue.getValues();
			TimeseriesData timeseriesData = newTimeseriesData(referenceValues);
			refValuesDataCollection.put(referenceValue
					.getGeneratedGlobalId(infos.getTimeseriesId()),
					timeseriesData);
		}
		return !refValuesDataCollection.isEmpty() ? refValuesDataCollection
				: null;
	}

	private TimeseriesValue[] fitReferenceValuesForInterval(
			ReferenceValue referenceValue, GetDataInfos infos) {
		DesignOptions options = infos.getOptions();
		long begin = options.getBegin();
		long end = options.getEnd();

		/*
		 * We create artificial interval bounds for "one value" references to
		 * match the requested timeframe. This is needed to render the
		 * particular reference value in a chart.
		 */

		TimeseriesValue lastValue = referenceValue.getLastValue();
		TimeseriesValue from = new TimeseriesValue(begin, lastValue.getValue());
		TimeseriesValue to = new TimeseriesValue(end, lastValue.getValue());
		return new TimeseriesValue[] { from, to };
	}

	public TimeseriesValue getFirstValue(SosTimeseries timeseries) {
		TimeseriesProperties properties = createCondensedTimeseriesProperties(timeseries
				.getTimeseriesId());
		DesignOptions designOptions = createOptionsForGetFirstValue(properties);
		return performFirstOrLastValueRequest(properties, designOptions);
	}

	public TimeseriesValue getLastValue(SosTimeseries timeseries) {
		TimeseriesProperties properties = createCondensedTimeseriesProperties(timeseries
				.getTimeseriesId());
		DesignOptions designOptions = createOptionsForGetLastValue(properties);
		return performFirstOrLastValueRequest(properties, designOptions);
	}

	private TimeseriesValue performFirstOrLastValueRequest(
			TimeseriesProperties properties, DesignOptions designOptions) {
		try {
			TvpDataCollection dataCollection = prepareTimeseriesResults(properties);
			dataCollection = performTimeseriesDataRequest(dataCollection,
					designOptions);
			TimeseriesValue[] data = dataCollection.getTimeseries(
					properties.getTimeseriesId()).getValues();
			if (data.length == 0) {
				LOGGER.error(
						"Server did not return the first/last value for timeseries '{}'.",
						properties.getTimeseriesId());
				return null;
			}
			return data[0];
		} catch (Exception e) {
			LOGGER.debug("Could not retrieve first or last value request. Probably not supported.");
			return null;
		}
	}

	public TimeSeriesDataService getTimeSeriesDataService() {
		return timeSeriesDataService;
	}

	public void setTimeSeriesDataService(
			TimeSeriesDataService timeSeriesDataService) {
		this.timeSeriesDataService = timeSeriesDataService;
	}

	@Override
	public InputStream getRawData(String id, IoParameters query) {
		return getRawData(createForSingleTimeseries(id, query));
	}

	@Override
	public InputStream getRawData(UndesignedParameterSet parameters) {
		checkRawDataFormat(parameters.getRawFormat(),
				parameters.getTimeseries());
		Map<SOSMetadata, Set<String>> timeseriesMetadataMap = getTimeseriesMetadataMap(parameters);
		if (timeseriesMetadataMap.isEmpty()) {
			throw new ResourceNotFoundException("Found no data for timeseries and parameter.");
		} else if (timeseriesMetadataMap.size() > 1) {
			throw new OptionNotSupported(
					"Querying raw timeseries data from several services is not yet supported");
		}
		List<OperationResult> result = getOperationResult(timeseriesMetadataMap, parameters);
		if (result.isEmpty()) {
			LOGGER.error("Get no result for GetObservation request");
			return null;
		}
		// TODO implement support for multiple results
		return getInputStreamFromOperationResult(result.get(0));
	}

	@Override
	public boolean supportsRawData() {
		return true;
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
				throw new OptionNotSupported(
						String.format(
								"Requested rawFormat '%s' is not supported by timeseries '%s'!",
								rawFormat, id));
			}
		} else {
			throw new BadRequestException(
					"The parameter 'rawFormat' is not set or empty!");
		}
	}

	private void checkRawDataFormat(String rawFormat, String[] timeseries) {
		for (String id : timeseries) {
			checkRawDataFormat(rawFormat, id);
		}
	}

	private List<OperationResult> getOperationResult(
			Map<SOSMetadata, Set<String>> map, UndesignedParameterSet parameters) {
		List<OperationResult> list = new ArrayList<OperationResult>(map.size());
		for (Entry<SOSMetadata, Set<String>> entry : map.entrySet()) {
			list.add(getOperationResult(entry.getKey(), entry.getValue(), parameters));
		}
		return list;
	}

	private OperationResult getOperationResult(SOSMetadata metadata,
			Set<String> timeseriesIds, UndesignedParameterSet parameters) {
		try {
			return getOperationResult(createGetObservationRequest(metadata, timeseriesIds, parameters), metadata);
		} catch (Exception e) {
			throw new InternalServerException(
					String.format(
							"Could not get raw observation data for timeseries '%s' and rawFormat '%s'!",
							setToString(timeseriesIds),
							parameters.getRawFormat()), e);
		}
	}

	private OperationResult getOperationResult(
			Callable<OperationResult> callable, SOSMetadata metadata)
			throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<OperationResult> futureTask = new FutureTask<OperationResult>(callable);
		AccessorThreadPool.execute(futureTask);
		return futureTask.get(metadata.getTimeout(), TimeUnit.MILLISECONDS);
	}

	private Map<SOSMetadata, Set<String>> getTimeseriesMetadataMap(UndesignedParameterSet parameters) {
		Map<SOSMetadata, Set<String>> map = new HashMap<SOSMetadata, Set<String>>();

		for (String timeseriesId : parameters.getTimeseries()) {
			SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
			if (checkQueryParameters(metadata, timeseriesId, parameters)) {
				if (map.containsKey(metadata)) {
					map.get(metadata).add(timeseriesId);
				} else {
					Set<String> set = new HashSet<String>();
					set.add(timeseriesId);
					map.put(metadata, set);
				}
			}
		}
		return map;
	}

	private boolean checkQueryParameters(SOSMetadata metadata, String timeseriesId, UndesignedParameterSet parameters) {
		SosTimeseries timeseries = getTimeseries(metadata, timeseriesId);
		return checkService(metadata, parameters) && checkCategory(timeseries, parameters) 
				&& checkPhenomenon(timeseries, parameters) && checkStation(metadata, timeseriesId, parameters) 
				&& checkFeature(timeseries, parameters) && checkProcedure(timeseries, parameters)
				&& checkOffering(timeseries, parameters);
	}
	
	private boolean checkOffering(SosTimeseries timeseries, UndesignedParameterSet parameters) {
		return checkForParameter(timeseries.getOffering().getGlobalId(), parameters, "offering");
	}
	
	private boolean checkProcedure(SosTimeseries timeseries, UndesignedParameterSet parameters) {
		return checkForParameter(timeseries.getProcedure().getGlobalId(), parameters, "procedure");
	}
	
	private boolean checkFeature(SosTimeseries timeseries, UndesignedParameterSet parameters) {
		return checkForParameter(timeseries.getFeature().getGlobalId(), parameters, "feature");
	}
	
	private boolean checkStation(SOSMetadata metadata, String timeseriesId, UndesignedParameterSet parameters) {
		Station station = metadata.getStationByTimeSeriesId(timeseriesId);
		return checkForParameter(station.getGlobalId(), parameters, "station");
	}
	
	private boolean checkPhenomenon(SosTimeseries timeseries, UndesignedParameterSet parameters) {
		return checkForParameter(timeseries.getPhenomenon().getGlobalId(), parameters, "phenomenon");
	}

	private boolean checkCategory(SosTimeseries timeseries, UndesignedParameterSet parameters) {
		return checkForParameter(timeseries.getCategory().getGlobalId(), parameters, "category");
	}

	private boolean checkService(SOSMetadata metadata, UndesignedParameterSet parameters) {
		return checkForParameter(metadata.getGlobalId(), parameters, "service");
	}
	
	private boolean checkForParameter(String toCheck, UndesignedParameterSet parameters, String parameter) {
		if (parameters.containsParameter(parameter.toLowerCase())) {
			return parameters.getAsString(parameter.toLowerCase()).equals(toCheck);
		}
		return true;
	}

	private SosTimeseries getTimeseries(SOSMetadata metadata, String timeseriesId) {
		Station station = metadata.getStationByTimeSeriesId(timeseriesId);
		return station.getTimeseriesById(timeseriesId);
	}

	private Callable<OperationResult> createGetObservationRequest(
			SOSMetadata metadata, Set<String> timeseriesIds,
			UndesignedParameterSet parameters) throws OXFException {
		ParameterContainer container = new ParameterContainer();
		Map<String, String[]> map = getQueryParameter(metadata, timeseriesIds);
		addProcedure(container, map.get("proc"));
		addObservedProperty(container, map.get("obsProp"));
		addFeatureOfInterest(container, map.get("foi"));
		addOffering(container, map.get("off"));
		return createGetObservationRequest(metadata, container, parameters);
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

	private Callable<OperationResult> createGetObservationRequest(
			SOSMetadata metadata, ParameterContainer container, UndesignedParameterSet parameters)
			throws OXFException {
		// set responseFormat
		container.addParameterShell("responseFormat", parameters.getRawFormat());
		// set temporal filter
		addTemporalFilter(container, parameters, metadata);
		container.addParameterShell("version", metadata.getVersion());
		container.addParameterShell("service", "SOS");
		Operation operation = new Operation("GetObservation", metadata.getServiceUrl(), metadata.getServiceUrl());
		return new OperationAccessor( SosAdapterFactory.createSosAdapter(metadata), operation, container);
	}

	private void addProcedure(ParameterContainer container, String...procedures) throws OXFException {
		if (check(procedures)) {
			container.addParameterShell("procedure", procedures);
		}
	}
	
	private void addOffering(ParameterContainer container, String...offerings) throws OXFException {
		if (check(offerings)) {
			container.addParameterShell("offering", offerings);
		}
	}
	
	private void addObservedProperty(ParameterContainer container, String...observedProperties) throws OXFException {
		if (check(observedProperties)) {
			container.addParameterShell("observedProperty", observedProperties);
		}
	}
	
	private void addFeatureOfInterest(ParameterContainer container, String...featureOfInterests) throws OXFException {
		if (check(featureOfInterests)) {
			container.addParameterShell("featureOfInterest", featureOfInterests);
		}
	}

	private void addTemporalFilter(ParameterContainer container,
			UndesignedParameterSet parameters, SOSMetadata metadata) throws OXFException {
		if (parameters.getTimespan() != null
				&& !parameters.getTimespan().isEmpty()) {
			String parameterName = null;
			if (SosUtil.isVersion100(metadata.getVersion())) {
				parameterName = "eventTime";
			} else if (SosUtil.isVersion200(metadata.getVersion())) {
				parameterName = "temporalFilter";
			}
			container.addParameterShell(parameterName, getTimeFrom(parameters));
		}
	}
	
	private boolean check(String[] array) {
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
				if (gord.getGetObservationResponse() != null && gord.getGetObservationResponse().getObservationDataArray() != null)
			    for (ObservationData od : gord.getGetObservationResponse().getObservationDataArray()) {
			    	OMObservationPropertyType omopt = OMObservationPropertyType.Factory.newInstance(xmlOptions);
			    	omopt.setOMObservation(od.getOMObservation());
					fct.addNewFeatureMember().set(omopt);
				}
			    return new AutoCloseInputStream(XmlObject.Factory.parse(fcd.xmlText(xmlOptions)).newInputStream(xmlOptions));
			}
		} catch (Exception e) {
			LOGGER.debug("Returned response is not XML formatted!");
		}
		return result.getIncomingResultAsAutoCloseStream();
	}

	private Object setToString(Set<String> timeseriesIds) {
		StringBuilder builder = new StringBuilder();
		for (String string : timeseriesIds) {
			builder.append(string).append(", ");
		}
		return builder.substring(0, builder.lastIndexOf(","));
	}
	
	private static XmlOptions initXmlOptions() {
		XmlOptions xmlOptions = new XmlOptions();
		Map<String, String> prefixMap = new HashMap<String, String>();
		prefixMap.put("http://www.opengis.net/gml/3.2" , "gml");
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
	
}
