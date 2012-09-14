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

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class SensorRecord.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>s
 */
public class SensorRecord extends ListGridRecord implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1903693116549224476L;

    /**
     * 
     */

    /**
     * Instantiates a new sensor record.
     */
    public SensorRecord() {
        // empty constructor
    }

    /**
     * Instantiates a new sensor record.
     * 
     * @param name
     *            the name
     * @param status
     *            the status
     * @param inUse 
     */
    public SensorRecord(String name, String status, String inUse) {
        setName(name);
        setStatus(status);
        setInUse(inUse);
    }
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return getAttributeAsString("name");
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        setAttribute("name", name);
    }

    /**
     * Sets the status.
     * 
     * @param status
     *            the new status
     */
    public void setStatus(String status) {
        setAttribute("status", status);
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public String getStatus() {
        return getAttributeAsString("status");
    }
    
    /**
     * Sets the inUse.
     * 
     * @param inUse
     *            the new inUse
     */
    public void setInUse(String inUse) {
        setAttribute("inUse", inUse);
    }

    /**
     * Gets the inUse.
     * 
     * @return the inUse
     */
    public String getInUse() {
        return getAttributeAsString("inUse");
    }

    /**
     * Gets the field value.
     * 
     * @param field
     *            the field
     * @return the field value
     */
    public String getFieldValue(String field) {
        return getAttributeAsString(field);
    }
}