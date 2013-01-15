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
