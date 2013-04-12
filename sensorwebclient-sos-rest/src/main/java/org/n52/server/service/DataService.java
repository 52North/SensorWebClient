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
import java.util.Map;

import org.joda.time.DateTime;
import org.n52.client.service.SensorMetadataService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterConstellation;
import org.n52.server.service.rest.ParameterSet;
import org.n52.server.service.rest.TimeSeriesdataResult;
import org.n52.server.service.rest.control.InvalidParameterConstallationException;
import org.n52.server.service.rest.control.ResourceNotFoundException;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    private SensorMetadataService sensorMetadataService;

    protected SOSMetadata getServiceMetadata(String instance) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(instance);
        if (metadata == null) {
            LOGGER.warn("Could not find configured SOS instance for itemName '{}'" + instance);
            throw new ResourceNotFoundException();
        }
        return metadata;
    }

    protected void createTimeSeriesRequest(ParameterSet parameterSet, SOSMetadata metadata, ArrayList<TimeSeriesProperties> props, Map<String, TimeSeriesdataResult> timeSeriesResults) {
        for (ParameterConstellation constellation : parameterSet.getParameters()) {
            try {
                Station station = getStationFromParameters(metadata, constellation);
            	TimeSeriesProperties timeSeriesProperties = createTimeSeriesProperties(metadata, station, constellation);
            	timeSeriesProperties.setTsID(constellation.getClientId());
                props.add(decorateProperties(timeSeriesProperties, parameterSet));
                TimeSeriesdataResult result = createTimeSeriesResult(timeSeriesProperties);
                timeSeriesResults.put(constellation.getClientId(), result);
            }
            catch (InvalidParameterConstallationException e) {
                LOGGER.warn("Unable to process request: {}", e.getMessage());
//                timeSeriesResults.put(constellation.getClientId(), null);
            }
            catch (Exception e) {
                LOGGER.error("Could not process time series request.", e);
                throw new InternalServiceException();
            }
        }
    }

    private Station getStationFromParameters(SOSMetadata metadata, ParameterConstellation constellation) throws InvalidParameterConstallationException {
        String offeringId = constellation.getOffering();
        String procedureId = constellation.getProcedure();
        String phenomenonId = constellation.getPhenomenon();
        String foiId = constellation.getFeatureOfInterest();
        Station station = metadata.getStationByParameterConstellation(offeringId, foiId, procedureId, phenomenonId);
        if (station == null) {
            throw new InvalidParameterConstallationException(constellation);
        }
        return station;
    }

    private TimeSeriesProperties createTimeSeriesProperties(SOSMetadata metadata, Station station, ParameterConstellation constellation) {
        String sosUrl = metadata.getServiceUrl();
        Offering offering = metadata.getOffering(constellation.getOffering());
        Procedure procedure = metadata.getProcedure(constellation.getProcedure());
        Phenomenon phenomenon = metadata.getPhenomenon(constellation.getPhenomenon());
        FeatureOfInterest foi = metadata.getFeature(constellation.getFeatureOfInterest());
        return new TimeSeriesProperties(sosUrl, station, offering, foi, procedure, phenomenon, 0, 0, "???", true);
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
    protected TimeSeriesProperties decorateProperties(final TimeSeriesProperties timeSeriesProperties, ParameterSet parameterSet) throws Exception {
        // default is to decorate nothing
        return timeSeriesProperties;
    }

    /**
     * Decorades passed properties with further properties from sensor's metadata (e.g. UOM). These are
     * requested from the {@link SensorMetadataService}.
     */
    protected TimeSeriesProperties decoradeWithSensorMetadataProperties(TimeSeriesProperties timeSeriesProperties) throws Exception {
        return sensorMetadataService.getSensorMetadata(timeSeriesProperties).getProps();
    }

    private TimeSeriesdataResult createTimeSeriesResult(TimeSeriesProperties timeSeriesProperties) {
        TimeSeriesdataResult result = new TimeSeriesdataResult();
        result.setUom(timeSeriesProperties.getUom());
        return result;
    }

    protected DesignOptions createDesignOptions(ParameterSet parameterSet, ArrayList<TimeSeriesProperties> props) {
        return createDesignOptions(parameterSet, props, true);
    }
    
    protected DesignOptions createDesignOptions(ParameterSet parameterSet, ArrayList<TimeSeriesProperties> props, boolean renderGrid) {
        long begin = new DateTime(parameterSet.getBegin()).getMillis();
        long end = new DateTime(parameterSet.getEnd()).getMillis();
        return new DesignOptions(props, begin, end, renderGrid);
    }

    public SensorMetadataService getSensorMetadataService() {
        return sensorMetadataService;
    }

    public void setSensorMetadataService(SensorMetadataService sensorMetadataService) {
        this.sensorMetadataService = sensorMetadataService;
    }

}
