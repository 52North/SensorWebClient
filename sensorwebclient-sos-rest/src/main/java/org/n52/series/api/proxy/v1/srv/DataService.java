/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadataForItemName;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.Instant;

import org.joda.time.Interval;
import org.n52.client.service.SensorMetadataService;
import org.n52.client.service.TimeSeriesDataService;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.InternalServerException;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    private SensorMetadataService sensorMetadataService;

    /**
     * @param timeseriesId
     *        the timeseries id to find the SOS metadata for.
     * @return the SOS metadata associated to the given timeseries or <code>null</code> if timeseries id is
     *         unknown.
     */
    protected SOSMetadata getMetadataForTimeseriesId(String timeseriesId) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.containsStationWithTimeseriesId(timeseriesId)) {
                return metadata;
            }
        }
        return null;
    }

    /**
     * @param instance
     *        the SOS instance to find metadata for.
     * @return the SOS metadata.
     * @throws ResourceNotFoundException
     *         if SOS instance could not be found.
     */
    protected SOSMetadata getMetadataForInstanceName(String instance) {
        SOSMetadata metadata = getSOSMetadataForItemName(instance);
        if (metadata == null) {
            LOGGER.warn("Could not find configured SOS instance for itemName '{}'" + instance);
            throw new ResourceNotFoundException("No SOS instance for name '" + instance + "'.");
        }
        return metadata;
    }

    /**
     * @param parameterSet
     *        a set of timeseries parameters.
     * @param props
     *        an empty list to add prepared {@link TimeseriesProperties} to.
     * @return a prepared {@link TvpDataCollection} to be filled with data coming from a
     *         {@link TimeSeriesDataService}.
     */
    protected TvpDataCollection prepareTimeseriesResults(UndesignedParameterSet parameterSet,
                                                         List<TimeseriesProperties> props) {
        TvpDataCollection timeseriesCollection = new TvpDataCollection();
        for (String timeseriesId : parameterSet.getTimeseries()) {
            try {
                TimeseriesProperties propertiesInstance = parameterSet.isExpanded()
                    ? createExpandedTimeseriesProperties(timeseriesId) // e.g. with refValues
                    : createCondensedTimeseriesProperties(timeseriesId);
                TimeseriesData timeseriesData = createTimeseriesData(propertiesInstance);
                timeseriesCollection.addNewTimeseries(timeseriesId, timeseriesData);
                props.add(propertiesInstance);
            }
            catch (Exception e) {
                LOGGER.error("Could not process time series request.", e);
                throw new InternalServerException("Could not process time series request.", e);
            }
        }
        return timeseriesCollection;
    }

    protected TvpDataCollection prepareTimeseriesResults(TimeseriesProperties props) {
        TvpDataCollection timeseriesCollection = new TvpDataCollection();
        try {
            String timeseriesId = props.getTimeseriesId();
            TimeseriesProperties propertiesInstance = createCondensedTimeseriesProperties(timeseriesId);
            TimeseriesData timeseriesData = createTimeseriesData(propertiesInstance);
            timeseriesCollection.addNewTimeseries(timeseriesId, timeseriesData);
        }
        catch (Exception e) {
            LOGGER.error("Could not process time series request.", e);
            throw new InternalServerException("Could not process time series request.", e);
        }
        return timeseriesCollection;
    }
    
    protected TimeseriesProperties createExpandedTimeseriesProperties(String timeseriesId) {
        SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        Station station = metadata.getStationByTimeSeriesId(timeseriesId);
        SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
        TimeseriesProperties condensedTimeseriesProperties = createCondensedTimeseriesProperties(timeseriesId);
        Procedure procedure = lookup.getProcedure(timeseries.getProcedureId());
        condensedTimeseriesProperties.addAllRefValues(procedure.getReferenceValues());
        return condensedTimeseriesProperties;
    }

    protected HashMap<String,ReferenceValue> getReferenceValuesFor(String timeseriesId) {
        SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
        Station station = metadata.getStationByTimeSeriesId(timeseriesId);
        SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
        
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        Procedure procedure = lookup.getProcedure(timeseries.getProcedureId());
        return procedure.getReferenceValues();
    }

    protected TimeseriesProperties getTimeseriesProperties(String timeseriesId, DesignOptions options) {
        for (TimeseriesProperties timeseriesProperties : options.getProperties()) {
            if (timeseriesProperties.getTimeseriesId().equals(timeseriesId)) {
                return timeseriesProperties;
            }
        }
        return null;
    }

    protected TimeseriesProperties createCondensedTimeseriesProperties(String timeseriesId) {
        SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
        Station station = metadata.getStationByTimeSeriesId(timeseriesId);
        SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        TimeseriesProperties properties = new TimeseriesProperties(timeseries, station, 0, 0, "???", true);
        return properties;
    }

    /**
     * Override if passed properties have to be extended/complemented/expanded. If not overridden the
     * properties remain as passed.
     * 
     * @param timeSeriesProperties
     *        the properties to decorate.
     * @param parameterSet
     *        the request parameters.
     * @return the decorated properties
     * @throws Exception
     *         if decoration fails.
     */
    protected TimeseriesProperties decorateProperties(final TimeseriesProperties timeSeriesProperties,
                                                      UndesignedParameterSet parameterSet) throws Exception {
        // default is to decorate nothing
        return timeSeriesProperties;
    }

    private TimeseriesData createTimeseriesData(TimeseriesProperties timeseriesProperties) {
        Map<Long, Double> dummyMap = Collections.emptyMap();
        return TimeseriesData.newTimeseriesData(dummyMap);
    }

    protected DesignOptions createDesignOptions(UndesignedParameterSet parameterSet,
                                                ArrayList<TimeseriesProperties> props) {
        return createDesignOptions(parameterSet, props, true);
    }

    protected DesignOptions createDesignOptions(UndesignedParameterSet parameterSet,
                                                ArrayList<TimeseriesProperties> props,
                                                boolean renderGrid) {
        Interval timespan = Interval.parse(parameterSet.getTimespan());
        long begin = timespan.getStartMillis();
        long end = timespan.getEndMillis();
        DesignOptions designOptions = new DesignOptions(props, begin, end, renderGrid);
        if (parameterSet.getResultTime() != null) {
            Instant resultTime = Instant.parse(parameterSet.getResultTime());
            designOptions.setResultTime(resultTime.getMillis());
        }
        return designOptions;
    }

    public SensorMetadataService getSensorMetadataService() {
        return sensorMetadataService;
    }

    public void setSensorMetadataService(SensorMetadataService sensorMetadataService) {
        this.sensorMetadataService = sensorMetadataService;
    }

}
