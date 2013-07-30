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

import org.n52.client.service.QueryService;
import org.n52.server.service.QueryServiceImpl;
import org.n52.server.util.Statistics;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.service.rpc.RpcQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcQueryServlet extends RemoteServiceServlet implements RpcQueryService {

    private static final long serialVersionUID = -4732808888038989869L;

    private static final Logger LOG = LoggerFactory.getLogger(RpcQueryServlet.class);
    
    private QueryService service;

    @Override
    public void init() throws ServletException {
        LOG.debug("Initialize " + getClass().getName() +" Servlet for SOS Client");
        service = new QueryServiceImpl();
    }
    
	@Override
	public QueryResponse doQuery(QueryRequest request)
			throws Exception {
		Statistics.saveHostRequest(this.getThreadLocalRequest().getRemoteHost());
		return service.doQuery(request);
	}

}
