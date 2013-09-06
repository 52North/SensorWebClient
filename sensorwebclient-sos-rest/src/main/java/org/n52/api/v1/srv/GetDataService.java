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

package org.n52.api.v1.srv;

import static org.n52.io.v1.data.TimeseriesData.newTimeseriesData;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetFirstValue;
import static org.n52.shared.serializable.pojos.DesignOptions.createOptionsForGetLastValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesMetadata;
import org.n52.io.v1.data.TimeseriesValue;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
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

    private TvpDataCollection performTimeseriesDataRequest(TvpDataCollection timeSeriesResults, DesignOptions options) throws InternalServerException {
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
        catch (Exception e) {
            throw new InternalServerException("Could not get timeseries data for options: " + options, e);
        }
        return timeSeriesResults;
    }

    private TimeseriesMetadata createTimeseriesMetadata(GetDataInfos infos) {
        HashMap<String, ReferenceValue> refValues = infos.getProperties().getRefvalues();
        if (refValues == null || refValues.isEmpty()) {
            return null;
        }
        TimeseriesMetadata timeseriesMetadata = new TimeseriesMetadata();
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
