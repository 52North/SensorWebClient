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

import static org.n52.server.oxf.util.ConfigurationContext.getSOSMetadataForItemName;
import static org.n52.server.oxf.util.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.joda.time.Interval;
import org.n52.client.service.SensorMetadataService;
import org.n52.server.api.v0.InternalServiceException;
import org.n52.server.api.v0.ParameterSet;
import org.n52.server.api.v0.ctrl.InvalidSosTimeseriesException;
import org.n52.server.api.v0.ctrl.ResourceNotFoundException;
import org.n52.server.api.v0.output.TimeseriesData;
import org.n52.server.api.v0.output.TimeseriesDataCollection;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
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
            throw new ResourceNotFoundException();
        }
        return metadata;
    }

    protected TimeseriesDataCollection prepareTimeseriesResults(ParameterSet parameterSet,
                                                                ArrayList<TimeseriesProperties> props) {
        TimeseriesDataCollection timeseriesCollection = new TimeseriesDataCollection();
        for (String timeseriesId : parameterSet.getTimeseries()) {
            try {
                TimeseriesRenderingOptions properties = parameterSet.getTimeseriesRenderingOptions(timeseriesId);
                TimeseriesProperties propertiesInstance = createTimeseriesProperties(timeseriesId, properties);
                props.add(decorateProperties(propertiesInstance, parameterSet));
                TimeseriesData timeseriesData = createTimeseriesData(propertiesInstance);
                timeseriesCollection.addNewTimeseries(timeseriesId, timeseriesData);
            }
            catch (InvalidSosTimeseriesException e) {
                LOGGER.warn("Unable to process request: {}", e.getMessage());
                // timeSeriesResults.put(constellation.getClientId(), null);
            }
            catch (Exception e) {
                LOGGER.error("Could not process time series request.", e);
                throw new InternalServiceException();
            }
        }
        return timeseriesCollection;
    }

    private TimeseriesProperties createTimeseriesProperties(String timeseriesId,
                                                            TimeseriesRenderingOptions renderingOptions) throws Exception {
        SOSMetadata metadata = getMetadataForTimeseriesId(timeseriesId);
        Station station = metadata.getStationByTimeSeriesId(timeseriesId);
        SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
        String sosUrl = timeseries.getServiceUrl();
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParamtersLookup();
        Feature foi = lookup.getFeature(timeseries.getFeature());
        Phenomenon phenomenon = lookup.getPhenomenon(timeseries.getPhenomenon());
        Procedure procedure = lookup.getProcedure(timeseries.getProcedure());
        Offering offering = lookup.getOffering(timeseries.getOffering());
        TimeseriesProperties properties = new TimeseriesProperties(sosUrl,
                                                                   station,
                                                                   offering,
                                                                   foi,
                                                                   procedure,
                                                                   phenomenon,
                                                                   0,
                                                                   0,
                                                                   "???",
                                                                   true);
        if (renderingOptions != null) {
            properties.setHexColor(renderingOptions.getHexColor());
        }
        properties.setTsID(timeseriesId);
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
                                                      ParameterSet parameterSet) throws Exception {
        // default is to decorate nothing
        return timeSeriesProperties;
    }

    /**
     * Decorades passed properties with further properties from sensor's metadata (e.g. UOM). These are
     * requested from the {@link SensorMetadataService}.
     */
    protected TimeseriesProperties decoradeWithSensorMetadataProperties(TimeseriesProperties timeSeriesProperties) throws Exception {
        return sensorMetadataService.getSensorMetadata(timeSeriesProperties).getProps();
    }

    private TimeseriesData createTimeseriesData(TimeseriesProperties timeseriesProperties) {
        String uom = timeseriesProperties.getUnitOfMeasure();
        Map<Long, String> dummyMap = Collections.emptyMap();
        return TimeseriesData.newTimeseriesData(dummyMap, uom);
    }

    protected DesignOptions createDesignOptions(ParameterSet parameterSet, ArrayList<TimeseriesProperties> props) {
        return createDesignOptions(parameterSet, props, true);
    }

    protected DesignOptions createDesignOptions(ParameterSet parameterSet,
                                                ArrayList<TimeseriesProperties> props,
                                                boolean renderGrid) {
        Interval timespan = Interval.parse(parameterSet.getTimespan());
        long begin = timespan.getStartMillis();
        long end = timespan.getEndMillis();
        return new DesignOptions(props, begin, end, renderGrid);
    }

    public SensorMetadataService getSensorMetadataService() {
        return sensorMetadataService;
    }

    public void setSensorMetadataService(SensorMetadataService sensorMetadataService) {
        this.sensorMetadataService = sensorMetadataService;
    }

}
