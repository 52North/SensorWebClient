/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.service.rpc;

import javax.servlet.ServletException;

import org.n52.client.service.SensorMetadataService;
import org.n52.server.service.SensorMetadataServiceImpl;
import org.n52.server.util.Statistics;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.service.rpc.RpcSensorMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcSensorMetadataServlet extends RemoteServiceServlet implements RpcSensorMetadataService {

    private static final long serialVersionUID = -4181305877424535105L;

    private static final Logger LOG = LoggerFactory.getLogger(RpcSensorMetadataServlet.class);
    
    private SensorMetadataService service;

    @Override
    public void init() throws ServletException {
        LOG.debug("Initialize " + getClass().getName() +" Servlet for SOS Client");
        service = new SensorMetadataServiceImpl();
    }
    
    @Override
    public SensorMetadataResponse getSensorMetadata(TimeseriesProperties tsProperties) throws Exception {
        Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
        return service.getSensorMetadata(tsProperties);
    }

	@Override
	public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(SosTimeseries timeseries) throws Exception {
        Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
		return service.getProcedureDetailsUrl(timeseries);
	}

	@Override
	public SOSMetadataResponse getUpdatedSOSMetadata() {
		Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
		return service.getUpdatedSOSMetadata();
	}
}
