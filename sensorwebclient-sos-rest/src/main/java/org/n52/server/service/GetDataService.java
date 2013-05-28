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

package org.n52.server.service;

import static org.n52.server.service.rest.model.TimeseriesData.newTimeseriesData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterSet;
import org.n52.server.service.rest.model.TimeseriesData;
import org.n52.server.service.rest.model.TimeseriesDataCollection;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
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
    public TimeseriesDataCollection getTimeSeriesFromParameterSet(ParameterSet parameterSet) {
        ArrayList<TimeseriesProperties> tsProperties = new ArrayList<TimeseriesProperties>();
        TimeseriesDataCollection timeseriesCollection = prepareTimeseriesResults(parameterSet, tsProperties);
        return performTimeseriesDataRequest(timeseriesCollection, createDesignOptions(parameterSet, tsProperties));
    }

    @Override
    protected TimeseriesProperties decorateProperties(TimeseriesProperties timeSeriesProperties, ParameterSet parameterSet) throws Exception {
        return decoradeWithSensorMetadataProperties(timeSeriesProperties);
    }
    
    private TimeseriesDataCollection performTimeseriesDataRequest(TimeseriesDataCollection timeSeriesResults, DesignOptions options) throws InternalServiceException {
        try {
            TimeSeriesDataRequest tsRequest = new TimeSeriesDataRequest(options);
            TimeSeriesDataResponse timeSeriesData = timeSeriesDataService.getTimeSeriesData(tsRequest);
            Map<String, HashMap<Long, String>> data = timeSeriesData.getPayloadData();
            
            for (String timeseriesId : timeSeriesResults.getAllTimeseries().keySet()) {
                TimeseriesData result = timeSeriesResults.getTimeseries(timeseriesId);
                HashMap<Long, String> values = data.get(timeseriesId);
                TimeseriesData timeseriesData = newTimeseriesData(values, result.getUom());
                timeSeriesResults.addNewTimeseries(timeseriesId, timeseriesData);
            }
        }
        catch (Exception e) {
            LOGGER.error("Could not get timeseries data for options: " + options);
            throw new InternalServiceException();
        }
        return timeSeriesResults;
    }

    public TimeSeriesDataService getTimeSeriesDataService() {
        return timeSeriesDataService;
    }

    public void setTimeSeriesDataService(TimeSeriesDataService timeSeriesDataService) {
        this.timeSeriesDataService = timeSeriesDataService;
    }

}
