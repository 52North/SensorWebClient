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

import org.n52.client.service.EESDataService;
import org.n52.server.oxf.util.logging.Statistics;
import org.n52.server.service.EESDataServiceImpl;
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
