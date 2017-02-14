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
package org.n52.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.mgmt.SosMetadataUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadCacheServlet extends HttpServlet {
    
    private static final long serialVersionUID = 8068008200411423941L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadCacheServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	ServletOutputStream out = resp.getOutputStream();
        try {
            if (Boolean.parseBoolean(req.getParameter("reloadCache"))) {
                SosMetadataUpdate.updateSosServices(ConfigurationContext.getServiceMetadatas().keySet());
            }
            out.write(new String("Cache reloaded successfully").getBytes());
        } catch (Exception e) {
            LOGGER.error("Could not reload cache.", e);
            out.write(e.getStackTrace().toString().getBytes());
        } finally {
	        if (out != null) {
	        	out.flush();
		        out.close();
			}
        }
    }

}
