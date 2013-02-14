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
package org.n52.client.ses.ui;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class RuleRecord.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>s
 */
public class RuleRecord extends ListGridRecord {

	public static final String TYPE = "type";
	public static final String OWNERNAME = "ownerName";
	public static final String NAME = "name";
	public static final String OWNERID = "ownerID";
	public static final String DESCRIPTION = "description";
	public static final String MEDIUM = "medium";
	public static final String FORMAT = "format";
	public static final String PUBLISHED = "published";
	public static final String SUBSCRIBED = "subscribed";
	public static final String USERNAME = "userName";
	public static final String UUID = "uuid";
	
    /**
     * Instantiates a new rule record.
     */
    public RuleRecord() {
        //document this
    }

    /**
     * Instantiates a new rule record.
     * 
     * @param type
     *            the type
     * @param ownerName 
     * @param ownerID 
     * @param name
     *            the name
     * @param description
     *            the description
     * @param medium
     *            the medium
     * @param format
     *            the format
     * @param published
     *            the published
     * @param subscribed 
     */
    public RuleRecord(String type, String ownerName, String ownerID, String name, String description, String medium, String format, boolean published, boolean subscribed, String uuid) {
        setType(type);
        setOwnerName(ownerName);
        setOwnerID(ownerID);
        setName(name);
        setDescription(description);
        setMedium(medium);
        setFormat(format);
        setPublished(published);
        setSubscribed(subscribed);
        setUuid(uuid);
    }

    private void setUuid(String uuid) {
		setAttribute(UUID, uuid);
	}
    
    public String getUuid() {
		return getAttribute(UUID);
	}

	/**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        setAttribute(TYPE, type);
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return getAttributeAsString(TYPE);
    }
    
    /**
     * 
     * @param ownerName
     */
    public void setOwnerName(String ownerName) {
        setAttribute(OWNERNAME, ownerName);
    }

    /**
     * Gets the owner name.
     * 
     * @return the owner name
     */
    public String getOwnerName() {
        return getAttributeAsString(OWNERNAME);
    }
    
    /**
     * 
     * @param ownerID
     */
    public void setOwnerID(String ownerID) {
        setAttribute(OWNERID, ownerID);
    }

    /**
     * Gets the owner parameterId.
     * 
     * @return the owner parameterId
     */
    public String getOwnerID() {
        return getAttributeAsString(OWNERID);
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return getAttributeAsString(NAME);
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        setAttribute(NAME, name);
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        setAttribute(DESCRIPTION, description);
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return getAttributeAsString(DESCRIPTION);
    }

    /**
     * Sets the medium.
     * 
     * @param medium
     *            the new medium
     */
    public void setMedium(String medium) {
        setAttribute(MEDIUM, medium);
    }

    /**
     * Gets the medium.
     * 
     * @return the medium
     */
    public String getMedium() {
        return getAttributeAsString(MEDIUM);
    }

    /**
     * Sets the format.
     * 
     * @param format
     *            the new format
     */
    public void setFormat(String format) {
        setAttribute(FORMAT, format);
    }

    /**
     * Gets the format.
     * 
     * @return the format
     */
    public String getFormat() {
        return getAttributeAsString(FORMAT);
    }

    /**
     * Sets the published.
     * 
     * @param published
     *            the new published
     */
    public void setPublished(boolean published) {
        setAttribute(PUBLISHED, published);
    }

    /**
     * Gets the published.
     * 
     * @return the published
     */
    public boolean getPublished() {
        return getAttributeAsBoolean(PUBLISHED);
    }
    
    /**
     * Sets the subscribed.
     * @param subscribed 
     */
    public void setSubscribed(boolean subscribed) {
        setAttribute(SUBSCRIBED, subscribed);
    }

    /**
     * Gets the subscribed.
     * 
     * @return the subscribed
     */
    public boolean getSubscribed() {
        return getAttributeAsBoolean(SUBSCRIBED);
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
    
    /**
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        setAttribute(USERNAME, userName);
    }

    /**
     * 
     * @return the user name
     */
    public String getUserName() {
        return getAttributeAsString(USERNAME);
    }
    
}