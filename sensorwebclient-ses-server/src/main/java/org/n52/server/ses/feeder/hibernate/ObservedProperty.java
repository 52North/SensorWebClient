package org.n52.server.ses.feeder.hibernate;

import java.io.Serializable;

/**
 * The Class ObservedProperty.
 */
@SuppressWarnings("serial")
public class ObservedProperty implements Serializable {

    /** The observed property id. */
    private int obsPropId;

    /** The name of the observed property. */
    private String name;
    
    /** The offering. */
    private Offering offering;

    public ObservedProperty() {
        // for hibernate
    }
    
    /**
     * Instantiates a new observed property.
     *
     * @param name the name
     * @param offering the offering
     */
    public ObservedProperty(String name, Offering offering) {
        this.name = name;
        this.offering = offering;
    }
    
    /**
     * Gets the observed property id.
     * 
     * @return the observed Property Id
     */
    public int getObsPropId() {
        return this.obsPropId;
    }

    /**
     * Sets the observed property id.
     * 
     * @param obsPropId
     *            the obsPropId to set
     */
    public void setObsPropId(int obsPropId) {
        this.obsPropId = obsPropId;
    }

    /**
     * Gets the observed property name.
     * 
     * @return the observed property name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the observed property name.
     * 
     * @param name
     *            the observed property name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param offering the offering to set
     */
    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    /**
     * @return the offering
     */
    public Offering getOffering() {
        return this.offering;
    }
}
