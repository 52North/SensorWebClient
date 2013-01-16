package org.n52.server.ses.feeder.hibernate;

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
