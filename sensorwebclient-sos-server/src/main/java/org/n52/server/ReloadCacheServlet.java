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
package org.n52.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.updates.SosMetadataUpdate;
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
