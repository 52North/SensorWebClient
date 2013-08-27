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
import java.util.Map;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesValue;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
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
     * @param parameterSet containing request parameters.
     * @return a time series result instance, identified by {@link SosTimeseries#getTimeseriesId()}
     */
    public TimeseriesDataCollection getTimeSeriesFromParameterSet(UndesignedParameterSet parameterSet) {
        ArrayList<TimeseriesProperties> tsProperties = new ArrayList<TimeseriesProperties>();
        TimeseriesDataCollection timeseriesCollection = prepareTimeseriesResults(parameterSet, tsProperties);
        return performTimeseriesDataRequest(timeseriesCollection, createDesignOptions(parameterSet, tsProperties));
    }

    private TimeseriesDataCollection performTimeseriesDataRequest(TimeseriesDataCollection timeSeriesResults, DesignOptions options) throws InternalServerException {
        try {
            TimeSeriesDataRequest tsRequest = new TimeSeriesDataRequest(options);
            TimeSeriesDataResponse timeSeriesData = timeSeriesDataService.getTimeSeriesData(tsRequest);
            Map<String, HashMap<Long, Double>> data = timeSeriesData.getPayloadData();
            
            for (String timeseriesId : timeSeriesResults.getAllTimeseries().keySet()) {
                HashMap<Long, Double> values = data.get(timeseriesId);
                TimeseriesData timeseriesData = newTimeseriesData(values);
                timeSeriesResults.addNewTimeseries(timeseriesId, timeseriesData);
            }
        }
        catch (Exception e) {
            throw new InternalServerException("Could not get timeseries data for options: " + options, e);
        }
        return timeSeriesResults;
    }
    
    public TimeseriesValue getFirstValue(SosTimeseries timeseries) {
        TimeseriesProperties properties = createTimeseriesProperties(timeseries.getTimeseriesId());
        DesignOptions designOptions = createOptionsForGetFirstValue(properties);
        return performFirstOrLastValueRequest(properties, designOptions);
    }
    
    public TimeseriesValue getLastValue(SosTimeseries timeseries) {
        TimeseriesProperties properties = createTimeseriesProperties(timeseries.getTimeseriesId());
        DesignOptions designOptions = createOptionsForGetLastValue(properties);
        return performFirstOrLastValueRequest(properties, designOptions);
    }

    private TimeseriesValue performFirstOrLastValueRequest(TimeseriesProperties properties, DesignOptions designOptions) {
       try {
           TimeseriesDataCollection dataCollection = prepareTimeseriesResults(properties);
           dataCollection = performTimeseriesDataRequest(dataCollection, designOptions);
           TimeseriesValue[] data = dataCollection.getTimeseries(properties.getTimeseriesId()).getValues();
           if (data.length == 0) {
               LOGGER.error("Server did not return the first/last value for timeseries '{}'.", properties.getTimeseriesId());
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

    public void setTimeSeriesDataService(TimeSeriesDataService timeSeriesDataService) {
        this.timeSeriesDataService = timeSeriesDataService;
    }

}
