/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import org.n52.client.service.SesTimeseriesFeedService;
import org.n52.server.ses.service.SesTimeseriesFeedServiceImpl;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.service.rpc.RpcSesTimeseriesToFeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcSesTimeseriesToFeedServlet extends RemoteServiceServlet implements RpcSesTimeseriesToFeedService {

    private static final long serialVersionUID = 2842775817858111586L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesTimeseriesToFeedServlet.class);

    private SesTimeseriesFeedService service;

    
    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
        service = new SesTimeseriesFeedServiceImpl();
    }

    @Override
    public SesClientResponse getTimeseriesFeeds() throws Exception {
        return service.getTimeseriesFeeds();
    }    
    @Override
    public void updateTimeseriesFeed(String timeseriesFeedId, boolean newStatus) throws Exception {
        service.updateTimeseriesFeed(timeseriesFeedId, newStatus);
    }

    @Override
    public SesClientResponse getStations() throws Exception {
        return service.getStations();
    }

    @Override
    public SesClientResponse getPhenomena(String sensor) throws Exception {
        return service.getPhenomena(sensor);
    }

    @Override
    public SesClientResponse deleteTimeseriesFeed(String sensorID) throws Exception {
        return service.deleteTimeseriesFeed(sensorID);
    }

}