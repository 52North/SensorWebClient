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

import org.n52.client.service.SesSensorService;
import org.n52.server.ses.service.SesSensorServiceImpl;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.service.rpc.RpcSesSensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcSesSensorServlet extends RemoteServiceServlet implements RpcSesSensorService {

    private static final long serialVersionUID = 2842775817858111586L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesSensorServlet.class);

    private SesSensorService service;

    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
        service = new SesSensorServiceImpl();
    }    
    
    // returns all activated sensors which are stored in the DB of the client
    public synchronized SesClientResponse getStations() throws Exception {
        return service.getStations();
    }

    public synchronized SesClientResponse getAllSensors() throws Exception {
        return service.getAllSensors();
    }
    
    // returns all phenomena of given stationID and the unit of measurements
    public synchronized SesClientResponse getPhenomena(String station) throws Exception {
        return service.getPhenomena(station);
    }

    // activate or deactivate sensors. This function is for admins only
    public synchronized void updateSensor(String sensorID, boolean newStatus) throws Exception {
        service.updateSensor(sensorID, newStatus);
    }
    
    // delete sensor from DB
    public synchronized SesClientResponse deleteSensor(String sensorID) throws Exception {
        return service.deleteSensor(sensorID);
    }
}