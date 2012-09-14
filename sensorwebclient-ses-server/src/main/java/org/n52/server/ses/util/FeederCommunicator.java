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
package org.n52.server.ses.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.n52.server.ses.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 * 
 */
public class FeederCommunicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeederCommunicator.class);
    
    /**
     * Sends a usedSensor request to the SosSesFeeder to activate a sensor
     * 
     * @param sensorID
     *            sensorID of the sensor
     */
    public static void addUsedSensor(String sensorID) {
        // create SOAPMessage
        try {
            // create Message
            SOAPMessage message = MessageFactory.newInstance().createMessage();
            SOAPHeader header = message.getSOAPHeader();
            SOAPBody sb = message.getSOAPBody();
            header.detachNode();

            // add bodyName
            QName bodyName = new QName("http://sossesfeeder.52north.org/", "usedSensors", "feeder");
            SOAPBodyElement bodyElement = sb.addBodyElement(bodyName);

            // add sensorElement
            QName qn = new QName("sensor");
            SOAPElement sensorElem = bodyElement.addChildElement(qn);
            sensorElem.addTextNode(sensorID);

            // send message
            sendMessage(message);
            LOGGER.info("Send usedSensor message for " + sensorID + " to Feeder");
            // FIXME add handling of failed communication
        } catch (Exception e) {
            LOGGER.error("Error while sync'ing with sos-ses-feeder.", e);
        } 
    }

    /**
     * Sends a unusedSensor request to the SosSesFeeder to deactivate a sensor
     * 
     * @param sensorID
     *            sensorID ot the sensor
     */
    public static void removeUsedSensor(String sensorID) {
        // create SOAPMessage
        try {
            // create Message
            SOAPMessage message = MessageFactory.newInstance().createMessage();
            SOAPHeader header = message.getSOAPHeader();
            SOAPBody sb = message.getSOAPBody();
            header.detachNode();
            LOGGER.debug("Message: " + message);

            // add bodyName
            QName bodyName = new QName("http://sossesfeeder.52north.org/", "unusedSensors", "feeder");
            SOAPBodyElement bodyElement = sb.addBodyElement(bodyName);

            // add sensorElement
            QName qn = new QName("sensor");
            SOAPElement sensorElem = bodyElement.addChildElement(qn);
            sensorElem.addTextNode(sensorID);

            LOGGER.debug("Message: " + message);
            
            // send message
            sendMessage(message);
            LOGGER.info("Send unusedSensor message for " + sensorID + " to Feeder");
        } catch (Exception e) {
            LOGGER.error("Error while sync'ing with sos-ses-feeder.", e);
        } 
    }

    private static void sendMessage(SOAPMessage message) throws MalformedURLException, UnsupportedOperationException, SOAPException {
        URL endpoint = new URL(Config.feeder);
        LOGGER.debug("Endpoint: " + endpoint);
        SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
        connection.call(message, endpoint);
    }
}
