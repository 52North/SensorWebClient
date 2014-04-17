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
package org.n52.api.v1.srv;

import static org.n52.io.v1.data.TimeseriesData.newTimeseriesData;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetFirstValue;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetLastValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesDataMetadata;
import org.n52.io.v1.data.TimeseriesValue;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.server.da.oxf.ResponseExceedsSizeLimitException;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.web.BadRequestException;
import org.n52.web.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets data values from an SOS instance. Requested time series are aggregated to a list of
 * {@link TimeseriesProperties} and passed to a configured {@link TimeSeriesDataService}. Data response will
 * be enriched by further metadata from each procedure measuring the requested time series.
 */
public class GetDataService extends DataService {

    static final Logger LOGGER = LoggerFactory.getLogger(GetDataService.class);

    private TimeSeriesDataService timeSeriesDataService;

    /**
     * @param parameterSet
     *        containing request parameters.
     * @return a time series result instance, identified by {@link SosTimeseries#getTimeseriesId()}
     */
    public TvpDataCollection getTimeSeriesFromParameterSet(UndesignedParameterSet parameterSet) {
        ArrayList<TimeseriesProperties> tsProperties = new ArrayList<TimeseriesProperties>();
        TvpDataCollection timeseriesCollection = prepareTimeseriesResults(parameterSet, tsProperties);
        return performTimeseriesDataRequest(timeseriesCollection, createDesignOptions(parameterSet, tsProperties));
    }

    private TvpDataCollection performTimeseriesDataRequest(TvpDataCollection timeSeriesResults, DesignOptions options) {
        try {
            TimeSeriesDataRequest tsRequest = new TimeSeriesDataRequest(options);
            TimeSeriesDataResponse timeSeriesData = timeSeriesDataService.getTimeSeriesData(tsRequest);
            Map<String, HashMap<Long, Double>> data = timeSeriesData.getPayloadData();

            for (String timeseriesId : timeSeriesResults.getAllTimeseries().keySet()) {

                TimeseriesProperties properties = getTimeseriesProperties(timeseriesId, options);
                GetDataInfos infos = new GetDataInfos(timeseriesId, properties, options);
                HashMap<Long, Double> values = data.get(timeseriesId);
                TimeseriesData timeseriesData = newTimeseriesData(values);
                if (properties.getReferenceValues() != null) {
                    timeseriesData.setMetadata(createTimeseriesMetadata(infos));
                }
                timeSeriesResults.addNewTimeseries(timeseriesId, timeseriesData);
            }
        }
        catch (ResponseExceedsSizeLimitException e) {
            throw new BadRequestException(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalServerException("Could not get timeseries data for options: " + options, e);
        }
        return timeSeriesResults;
    }

    private TimeseriesDataMetadata createTimeseriesMetadata(GetDataInfos infos) {
        HashMap<String, ReferenceValue> refValues = infos.getProperties().getRefvalues();
        if (refValues == null || refValues.isEmpty()) {
            return null;
        }
        TimeseriesDataMetadata timeseriesMetadata = new TimeseriesDataMetadata();
        timeseriesMetadata.setReferenceValues(createReferenceValuesData(refValues, infos));
        return timeseriesMetadata;
    }

    private Map<String, TimeseriesData> createReferenceValuesData(HashMap<String, ReferenceValue> refValues,
                                                                  GetDataInfos infos) {
        Map<String, TimeseriesData> refValuesDataCollection = new HashMap<String, TimeseriesData>();
        for (String referenceValueId : refValues.keySet()) {
            ReferenceValue referenceValue = refValues.get(referenceValueId);
            TimeseriesValue[] referenceValues = referenceValue.getValues().length == 1
                ? fitReferenceValuesForInterval(referenceValue, infos)
                : referenceValue.getValues();
            TimeseriesData timeseriesData = newTimeseriesData(referenceValues);
            refValuesDataCollection.put(referenceValue.getGeneratedGlobalId(infos.getTimeseriesId()), timeseriesData);
        }
        return !refValuesDataCollection.isEmpty()
            ? refValuesDataCollection
            : null;
    }

    private TimeseriesValue[] fitReferenceValuesForInterval(ReferenceValue referenceValue, GetDataInfos infos) {
        DesignOptions options = infos.getOptions();
        long begin = options.getBegin();
        long end = options.getEnd();

        /*
         * We create artificial interval bounds for "one value" references to match the requested timeframe.
         * This is needed to render the particular reference value in a chart.
         */

        TimeseriesValue lastValue = referenceValue.getLastValue();
        TimeseriesValue from = new TimeseriesValue(begin, lastValue.getValue());
        TimeseriesValue to = new TimeseriesValue(end, lastValue.getValue());
        return new TimeseriesValue[] {from, to};
    }

    public TimeseriesValue getFirstValue(SosTimeseries timeseries) {
        TimeseriesProperties properties = createCondensedTimeseriesProperties(timeseries.getTimeseriesId());
        DesignOptions designOptions = createOptionsForGetFirstValue(properties);
        return performFirstOrLastValueRequest(properties, designOptions);
    }

    public TimeseriesValue getLastValue(SosTimeseries timeseries) {
        TimeseriesProperties properties = createCondensedTimeseriesProperties(timeseries.getTimeseriesId());
        DesignOptions designOptions = createOptionsForGetLastValue(properties);
        return performFirstOrLastValueRequest(properties, designOptions);
    }

    private TimeseriesValue performFirstOrLastValueRequest(TimeseriesProperties properties, DesignOptions designOptions) {
        try {
            TvpDataCollection dataCollection = prepareTimeseriesResults(properties);
            dataCollection = performTimeseriesDataRequest(dataCollection, designOptions);
            TimeseriesValue[] data = dataCollection.getTimeseries(properties.getTimeseriesId()).getValues();
            if (data.length == 0) {
                LOGGER.error("Server did not return the first/last value for timeseries '{}'.",
                             properties.getTimeseriesId());
                return null;
            }
            return data[0];
        }
        catch (Exception e) {
            LOGGER.debug("Could not retrieve first or last value request. Probably not supported.");
            return null;
        }
    }

    public TimeSeriesDataService getTimeSeriesDataService() {
        return timeSeriesDataService;
    }

    public void setTimeSeriesDataService(TimeSeriesDataService timeSeriesDataService) {
        this.timeSeriesDataService = timeSeriesDataService;
    }

}
