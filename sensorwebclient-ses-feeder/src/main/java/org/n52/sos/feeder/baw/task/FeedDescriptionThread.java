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
 * Created: 01.07.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;

import org.n52.sos.feeder.baw.connector.SESConnector;
import org.n52.sos.feeder.baw.connector.SOSConnector;
import org.n52.sos.feeder.baw.hibernate.ObservedProperty;
import org.n52.sos.feeder.baw.hibernate.Offering;
import org.n52.sos.feeder.baw.hibernate.SOS;
import org.n52.sos.feeder.baw.hibernate.Sensor;
import org.n52.sos.feeder.baw.utils.DatabaseAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread manages the collection of the sensorML document for one
 * procedure. It requests the document from the SOS and sends it to the SES by a
 * registerPublisher request.
 * 
 * @author Jan Schulte
 * 
 */
public class FeedDescriptionThread extends Thread {

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(FeedDescriptionThread.class);

    /** The sensor. */
    private Sensor sensor;

    /** The sos. */
    private SOS sos;

    /** The sos connector. */
    private SOSConnector sosCon;

    /** The ses connector. */
    private SESConnector sesCon;

    private boolean running = true;

    /**
     * Instantiates a new feed description thread.
     * 
     * @param sensor
     *            the sensor
     * @param sos
     *            The sos to which the sensor belong
     */
    public FeedDescriptionThread(Sensor sensor, SOS sos) {
        super("Description_" + sensor.getProcedure());
        this.sensor = sensor;
        this.sosCon = new SOSConnector(sos.getUrl());
        this.sesCon = new SESConnector();
        this.sos = sos;
    }

    /**
     * Starts the thread and collects the sensorML document an send it to the
     * SES.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        if (isRunning()) {
            // check if sensor procedure and related sos exists in
            // log.debug("FeedDescriptionThread#run()");
            boolean exists = DatabaseAccess.existsProcedureSOS(this.sensor.getProcedure(), this.sos.getUrl());

            if (exists) {
                // send Describe Sensor for procedure in the SOS
                log.debug("Run Thread for procedure: " + this.sensor.getProcedure());
                SensorMLDocument sensorML = null;
                sensorML = this.sosCon.getSensorML(this.sensor.getProcedure());
                if (sensorML == null) {
                    log.error("Get no SensorML-Document for procedure " + this.sensor.getProcedure());
                } else {
                    log.debug("SensorML: " + sensorML);
                    // get observedProperty
                    ArrayList<String> obsPropsSensorML = getObservedProperties(sensorML);
                    // check if the observedProperties of the sensor contains in
                    // the
                    // list of observedPropertys of the sensorML-Document
                    for (Offering offering : this.sensor.getOfferings()) {
                        Set<ObservedProperty> obsPropsTemp = new HashSet<ObservedProperty>();
                        for (ObservedProperty obsProp : offering.getObservedProperties()) {
                            if (obsPropsSensorML.contains(obsProp.getName())) {
                                obsPropsTemp.add(obsProp);
                            }
                        }
                        offering.setObservedProperties(obsPropsTemp);
                    }
                    String sesId = null;
                    try {
                        sesId = this.sesCon.registerPublisher(sensorML);
                        if (sesId == null) {
                            log.error("Get no resource ID for procedure " + this.sensor.getProcedure() + " from SES");
                        } else {
                            log.info("Sensor '" + this.sensor.getProcedure() + "' add to SES with ID: " + sesId);
                            this.sensor.setSesId(sesId);
                            DatabaseAccess.saveState(this.sos, this.sensor);
                        }
                    } catch (Exception e) {
                        log.error("Error while sending Sensor '" + this.sensor.getProcedure() + "' to SES");
                    }
                }
            } else {
                log.debug("Sensor " + this.sensor.getProcedure() + " in SOS " + this.sos.getUrl()
                        + " already exists in Database!");
            }
        }
    }

    /**
     * Gets the observed property.
     * 
     * @param sensorML
     *            the sensor ml
     * @return the observed property
     */
    private ArrayList<String> getObservedProperties(SensorMLDocument sensorML) {
        ArrayList<String> observedProperties = new ArrayList<String>();
        Member[] members = sensorML.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = null;
            try {
                systemType = (SystemType) member.getProcess();
            } catch (Exception e) {
                log.warn("Member is no SystemType.");
                return null;
            }
            Outputs outputs = systemType.getOutputs();
            OutputList outputList = outputs.getOutputList();
            IoComponentPropertyType[] outputArray = outputList.getOutputArray();
            for (IoComponentPropertyType output : outputArray) {
                if (output.isSetObservableProperty()) {
                    observedProperties.add(output.getObservableProperty().getDefinition());
                }
                if (output.isSetQuantity()) {
                    observedProperties.add(output.getQuantity().getDefinition());
                }
            }
        }
        return observedProperties;
    }

    /**
     * @param running
     *            the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

}
