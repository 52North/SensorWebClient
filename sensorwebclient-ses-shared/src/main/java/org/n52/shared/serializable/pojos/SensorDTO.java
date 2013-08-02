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
package org.n52.shared.serializable.pojos;

import java.io.Serializable;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public class SensorDTO implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4217139901327656997L;

    /** The id. */
    private int id;

    /** The sensorID. */
    private String sensorID;

    /** The activated. */
    private boolean activated;

    /** The useCount. */
    private int useCount;

    /**
     * 
     */
    public SensorDTO() {
    }

    /**
     * Instantiates a new sensor.
     * 
     * @param activated
     *            the activated
     * @param inuse
     *            the inuse
     */
    public SensorDTO(String sensorID, boolean activated, int useCount) {
        this.sensorID = sensorID;
        this.activated = activated;
        this.useCount = useCount;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getId() {
        return id;
    }
}