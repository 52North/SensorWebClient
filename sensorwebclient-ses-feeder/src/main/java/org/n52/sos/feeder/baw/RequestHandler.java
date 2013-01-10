/****************************************************************************
 * Copyright (C) 2010
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
 * 
 * Author: Jan Schulte
 * Created: 17.05.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw;

import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.n52.sos.feeder.baw.task.DescriptionTask;
import org.n52.sos.feeder.baw.utils.DatabaseAccess;
import org.n52.sos.feeder.baw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RequestHandler class handles the incoming request for the used and unused
 * sensors.
 * 
 * @author Jan Schulte
 * 
 */
public class RequestHandler {

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    /**
     * Gets the response.
     *
     * @param msg The incoming SOAPMessage
     * @return The response the to request
     */
    public SOAPMessage getResponse(SOAPMessage msg) {
        SOAPMessage message = null;
        try {
            log.info("Incoming request!");

            // get contents of response
            SOAPBody body = msg.getSOAPBody();
            Iterator<?> childElements = body.getChildElements();

            while (childElements.hasNext()) {
                Object next = childElements.next();
                // check if object is an element
                if (next instanceof SOAPElement) {
                    // check the request
                    SOAPElement elem = (SOAPElement) next;
                    // usedSensors element
                    if (elem.getElementName().getLocalName().equals(
                            Strings.getString("IncomingRequest.usedSensorsRequest"))) { 
                        usedSensors(elem);
                    }
                    // unsedSensors element
                    if (elem.getElementName().getLocalName().equals(
                            Strings.getString("IncomingRequest.unusedSensorsRequest"))) { 
                        unusedSensors(elem);
                    }
                    // addSOS element
                    if (elem.getElementName().getLocalName()
                            .equals(Strings.getString("IncomingRequest.addSOSRequest"))) { 
                        addSOS(elem);
                    }
                }
            }
        } catch (SOAPException e) {
            log.error("Unable to parse SOAPrequest: " + e);
        }
        return message;
    }

    /**
     * Adds the given sos to the database.
     *
     * @param addSOSElem the add sos element
     */
    private void addSOS(SOAPElement addSOSElem) {
        Iterator<?> childElements = addSOSElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sosElement"))) { 
                    log.info("New SOS added: " + elem.getValue()); 
                    // save in database
                    DatabaseAccess.saveNewSOS(elem.getValue());
                    // start an DescriptionTask
                    DescriptionTask task = new DescriptionTask();
                    new Thread(task).start();
                }
            }
        }
    }

    /**
     * Sets the given sensor element to unused.
     *
     * @param unusedSensorsElem the unused sensors element
     */
    private void unusedSensors(SOAPElement unusedSensorsElem) {
        Iterator<?> childElements = unusedSensorsElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sensorElement"))) { 
                    log.info("New Unused Sensor: " + elem.getValue()); 
                    DatabaseAccess.saveSensorUsage(elem.getValue(), false);
                    // FIXME delete lastupdate timestamp
                }
            }
        }
    }

    /**
     * Sets the given sensor element to used.
     *
     * @param usedSensorsElem the used sensors element
     */
    private void usedSensors(SOAPElement usedSensorsElem) {
        Iterator<?> childElements = usedSensorsElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sensorElement"))) { 
                    log.info("New Used Sensor: " + elem.getValue()); 
                    DatabaseAccess.saveSensorUsage(elem.getValue(), true);
                }
            }
        }
    }
}
