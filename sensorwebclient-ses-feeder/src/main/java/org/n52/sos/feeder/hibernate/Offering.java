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
 * Created: 04.01.2011
 *****************************************************************************/
package org.n52.sos.feeder.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * The Class Offering.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class Offering implements Serializable {

    /** The offering id. */
    private int offeringID;
    
    /** The name. */
    private String name;

    /** The sensor. */
    private Sensor sensor;
    
    /** The observed properties. */
    private Set<ObservedProperty> observedProperties;
    
    /**
     * @return the offeringID
     */
    public int getOfferingID() {
        return this.offeringID;
    }

    /**
     * @param offeringID the offeringID to set
     */
    public void setOfferingID(int offeringID) {
        this.offeringID = offeringID;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the observedProperties
     */
    public Set<ObservedProperty> getObservedProperties() {
        return this.observedProperties;
    }

    /**
     * @param observedProperties the observedProperties to set
     */
    public void setObservedProperties(Set<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * @return the sensor
     */
    public Sensor getSensor() {
        return this.sensor;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public String[] getObsPropAsStringArray() {
        String[] obsPropsStrings = new String[this.observedProperties.size()];
        int i = 0;
        for (Iterator<ObservedProperty> iterator = this.observedProperties.iterator(); iterator.hasNext();) {
            obsPropsStrings[i++] = iterator.next().getName();
        }
        return obsPropsStrings;
    }
    
}
