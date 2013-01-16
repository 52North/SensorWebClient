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
package org.n52.server.ses.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.n52.client.service.SesSensorService;
import org.n52.server.ses.Config;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.mail.MailSender;
import org.n52.server.ses.util.SesParser;
import org.n52.server.ses.util.SesUtil;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.Sensor;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SesSensorServiceImpl implements SesSensorService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SesSensorServiceImpl.class);

    private static SesParser parser;

    /**
     * This method adds new sensors to the data base of the client.
     * Source is the SES with registered sensors. 
     */
    public static synchronized void addSensorsToDB(){
        
        // XXX refactor
        
        ArrayList<String> sensors = getParser().getRegisteredSensors();
        for (int i = 0; i < sensors.size(); i++) {
            if (!HibernateUtil.existsSensor(sensors.get(i))) {
                Sensor sensor = new Sensor(sensors.get(i), true, 0);
                if (sensor != null) {
                    LOGGER.debug("Persist sensor to to feed: {}", sensor.getSensorID());
                    HibernateUtil.addSensor(sensor);
                }
            }
        }
    }

    private static SesParser getParser(){
        if (SesSensorServiceImpl.parser == null) {
            return new SesParser(Config.serviceVersion, Config.sesEndpoint);
        }
        return parser;
    }

    @Override
    public SesClientResponse getAllSensors() throws Exception {
        try {
            LOGGER.debug("get registered sensors from DB");
            List<Sensor> sensors = HibernateUtil.getSensors();
            return new SesClientResponse(SesClientResponse.types.REGISTERED_SENSORS, sensors);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public void updateSensor(String sensorID, boolean newStatus) throws Exception {
        try {
            LOGGER.debug("updateSensor: " + sensorID + " . New status: activated = " + newStatus);
            if (!HibernateUtil.updateSensor(sensorID, newStatus)) {
                LOGGER.error("Update sensor failed!");
                throw new Exception("Update sensor failed!");
            }
            
            if (!newStatus) {
                // sensor was deactivated
                // inform all subscriber
                ArrayList<User> userList = SesUtil.getUserBySensorID(sensorID);
                
                // iterate over the user
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    // inform user
                    MailSender.sendSensorDeactivatedMail(user.geteMail(), sensorID);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getStations() throws Exception {
        try {
            LOGGER.debug("getStations");
            ArrayList<String> finalList = new ArrayList<String>();
            HashSet<String> h = new HashSet<String>();
            
            // DB request
            List<Sensor> sensors = HibernateUtil.getActiveSensors();
            for (int i = 0; i < sensors.size(); i++) {
                // HashSet is used to avoid duplicates
                h.add(sensors.get(i).getSensorID());
            }
            
            finalList.addAll(h);
            
            // sort list
            Collections.sort(finalList);
    
            return new SesClientResponse(SesClientResponse.types.STATIONS, finalList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getPhenomena(String station) throws Exception {
        try {
            LOGGER.debug("getPhenomena for station: " + station);
            ArrayList<String> finalList = new ArrayList<String>();
            ArrayList<String> unit = new ArrayList<String>();
            
            // get the sensor from DB
            Sensor sensor = HibernateUtil.getSensorByID(station);
            if (sensor != null) {
                // get phenomena
                ArrayList<String> phenomena = getParser().getPhenomena(sensor.getSensorID());
                // get the unit of measurement
                unit.add(getParser().getUnit(sensor.getSensorID()));
                for (int i = 0; i < phenomena.size(); i++) {
                    LOGGER.debug(phenomena.get(i));
                    finalList.add(phenomena.get(i)); 
                }
            }
            
            Collections.sort(finalList);
            return new SesClientResponse(SesClientResponse.types.PHENOMENA, finalList, unit);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteSensor(String sensorID) throws Exception {
        try {
            LOGGER.debug("delete sensor: " + sensorID);
            if (HibernateUtil.deleteSensorByID(sensorID)) {
                return new SesClientResponse(SesClientResponse.types.DELETE_SENSOR_OK);
            }
            throw new Exception("delete sensor" + ": " + sensorID + " " + "failed");
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
