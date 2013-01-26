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