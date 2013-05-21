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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterSet;
import org.n52.server.service.rest.model.TimeSeriesDataResult;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets data values from an SOS instance. Requested time series are aggregated to a list of
 * {@link TimeSeriesProperties} and passed to a configured {@link TimeSeriesDataService}. Data response will
 * be enriched by further metadata from each procedure measuring the requested time series.
 */
public class GetDataService extends DataService {

    static final Logger LOGGER = LoggerFactory.getLogger(GetDataService.class);

    private TimeSeriesDataService timeSeriesDataService;

    /**
     * @param parameterSet containing request parameters.
     * @param instance the configured data instance
     * @return a time series result instance, identified by {@link SosTimeseries#getClientId()}
     */
    public Map<String, TimeSeriesDataResult> getTimeSeriesFromParameterSet(ParameterSet parameterSet, String instance) {
        SOSMetadata metadata = getServiceMetadata(instance);
        ArrayList<TimeSeriesProperties> tsProperties = new ArrayList<TimeSeriesProperties>();
        Map<String, TimeSeriesDataResult> timeSeriesResults = createTimeSeriesRequest(parameterSet, metadata, tsProperties);
        performTimeSeriesDataRequest(timeSeriesResults, createDesignOptions(parameterSet, tsProperties));
        return timeSeriesResults;
    }

    @Override
    protected TimeSeriesProperties decorateProperties(TimeSeriesProperties timeSeriesProperties, ParameterSet parameterSet) throws Exception {
        return decoradeWithSensorMetadataProperties(timeSeriesProperties);
    }
    
    private void performTimeSeriesDataRequest(Map<String, TimeSeriesDataResult> timeSeriesResults, DesignOptions options) throws InternalServiceException {
        try {
            TimeSeriesDataRequest tsRequest = new TimeSeriesDataRequest(options);
            TimeSeriesDataResponse timeSeriesData = timeSeriesDataService.getTimeSeriesData(tsRequest);
            Map<String, HashMap<Long, String>> data = timeSeriesData.getPayloadData();
            for (String clientId : timeSeriesResults.keySet()) {
                TimeSeriesDataResult result = timeSeriesResults.get(clientId);
                result.setValues(createCsvValues(data.get(clientId)));
            }
        }
        catch (Exception e) {
            LOGGER.error("Could not get timeseries data for options: " + options);
            throw new InternalServiceException();
        }
    }

    private String createCsvValues(HashMap<Long, String> timeSeries) {
        StringBuilder sb = new StringBuilder();
        for (Long timestamp : timeSeries.keySet()) {
            sb.append(timestamp.toString()).append(",");
            sb.append(timeSeries.get(timestamp)).append(";");
        }
        return sb.toString();
    }

    public TimeSeriesDataService getTimeSeriesDataService() {
        return timeSeriesDataService;
    }

    public void setTimeSeriesDataService(TimeSeriesDataService timeSeriesDataService) {
        this.timeSeriesDataService = timeSeriesDataService;
    }

}
