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
package org.n52.server.service;

import static org.n52.server.mgmt.ConfigurationContext.createSosMetadataHandler;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;

import java.util.Map;

import org.n52.client.service.SensorMetadataService;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.da.MetadataHandler;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMetadataServiceImpl implements SensorMetadataService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SensorMetadataServiceImpl.class);

    @Override
    public SensorMetadataResponse getSensorMetadata(final TimeseriesProperties tsProperties) throws Exception {
        try {
            LOG.debug("Request -> GetSensorMetadata");
            JavaHelper.cleanUpDir(ConfigurationContext.XSL_DIR, ConfigurationContext.FILE_KEEPING_TIME, "xml");
            
            SosTimeseries timeseries = tsProperties.getTimeseries();
            SOSMetadata sosMetadata = getSOSMetadata(timeseries.getServiceUrl());
            MetadataHandler metadataHandler = createSosMetadataHandler(sosMetadata);
            metadataHandler.assembleTimeseriesMetadata(tsProperties);
            
            String procedureId = timeseries.getProcedureId();
            TimeseriesParametersLookup lookup = sosMetadata.getTimeseriesParametersLookup();
            Procedure procedure = lookup.getProcedure(procedureId);
            procedure.addAllRefValues(tsProperties.getRefvalues());
    
            SensorMetadataResponse response = new SensorMetadataResponse(tsProperties);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parsed SensorMetadata: {}", response.toDebugString());
            }
    
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(SosTimeseries timeseries) throws Exception {
        try {
            LOG.debug("Request -> getProcedureDetailsUrl");
            TimeseriesProperties properties = new TimeseriesProperties(timeseries, null, -1, -1);
            SOSMetadata metadata = ConfigurationContext.getSOSMetadata(timeseries.getServiceUrl());
            MetadataHandler metadataHandler = createSosMetadataHandler(metadata);
            metadataHandler.assembleTimeseriesMetadata(properties);
            return new GetProcedureDetailsUrlResponse(properties.getMetadataUrl());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

	@Override
	public SOSMetadataResponse getUpdatedSOSMetadata() {
		Map<String, SOSMetadata> updateSOSMetadata = ConfigurationContext.updateSOSMetadata();
		return new SOSMetadataResponse(updateSOSMetadata);
	}

}
