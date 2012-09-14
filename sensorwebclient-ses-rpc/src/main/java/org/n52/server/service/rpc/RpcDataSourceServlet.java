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

import java.util.List;

import javax.servlet.ServletException;

import org.n52.client.service.SesDataSourceService;
import org.n52.server.ses.service.SesDataSourceServiceImpl;
import org.n52.shared.serializable.pojos.TestRecord;
import org.n52.shared.service.rpc.RpcSesDataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Loads sensors within small data packages.
 */
public class RpcDataSourceServlet extends RemoteServiceServlet implements RpcSesDataSourceService {

    private static final long serialVersionUID = -8967310202939131413L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcDataSourceServlet.class);

    private SesDataSourceService service;

    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() + " Servlet for SES Client");
        service = new SesDataSourceServiceImpl();
    }

    public synchronized List<TestRecord> fetch() throws Exception {
        return service.fetch();
    }

    public synchronized TestRecord add(TestRecord record) throws Exception {
        return service.add(record);
    }

    public synchronized TestRecord update(TestRecord record) throws Exception {
        return service.update(record);
    }

    public synchronized void remove(TestRecord record) throws Exception {
        service.remove(record);
    }
}
