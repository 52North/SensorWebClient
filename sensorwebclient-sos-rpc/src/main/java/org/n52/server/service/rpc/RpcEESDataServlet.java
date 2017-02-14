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
package org.n52.server.service.rpc;

import javax.servlet.ServletException;

import org.n52.client.service.EESDataService;
import org.n52.server.service.EESDataServiceImpl;
import org.n52.server.util.Statistics;
import org.n52.shared.requests.EESDataRequest;
import org.n52.shared.responses.EESDataResponse;
import org.n52.shared.service.rpc.RpcEESDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcEESDataServlet extends RemoteServiceServlet implements RpcEESDataService {

    private static final long serialVersionUID = 5367844766876172140L;

    private static final Logger LOG = LoggerFactory.getLogger(RpcEESDataServlet.class);
    
    private EESDataService service;

    @Override
    public void init() throws ServletException {
        LOG.debug("Initialize " + getClass().getName() + " Servlet for SOS Client");
        service = new EESDataServiceImpl();
    }
    
    public EESDataResponse getEESDiagram(EESDataRequest request) throws Exception {
        Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
        return service.getEESDiagram(request);
    }
    
    public EESDataResponse getEESOverview(EESDataRequest request) throws Exception {
        Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
        return service.getEESOverview(request);
    }
}
