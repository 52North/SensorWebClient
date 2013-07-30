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

package org.n52.server.service.rpc;

import javax.servlet.ServletException;

import org.n52.client.service.SensorMetadataService;
import org.n52.server.service.SensorMetadataServiceImpl;
import org.n52.server.util.Statistics;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
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
	public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(String serviceURL, String procedure) throws Exception {
        Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
		return service.getProcedureDetailsUrl(serviceURL, procedure);
	}

	@Override
	public SOSMetadataResponse getUpdatedSOSMetadata() {
		Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
		return service.getUpdatedSOSMetadata();
	}
}
