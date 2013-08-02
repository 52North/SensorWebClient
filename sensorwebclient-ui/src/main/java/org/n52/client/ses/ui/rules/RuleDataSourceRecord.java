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
package org.n52.client.ses.ui.rules;

import com.smartgwt.client.widgets.grid.ListGridRecord;

public class RuleDataSourceRecord extends ListGridRecord {

	public static final String TYPE = "ruleType";
	public static final String OWNERNAME = "ruleOwnerName";
	public static final String NAME = "ruleName";
	public static final String OWNERID = "ruleOwnerID";
	public static final String DESCRIPTION = "ruleDescription";
	public static final String MEDIUM = "ruleMedium";
	public static final String FORMAT = "ruleFormat";
	public static final String PUBLISHED = "ruleIsPublished";
	public static final String SUBSCRIBED = "ruleIsSubscribed";
	public static final String USERNAME = "ruleUserName";
	public static final String UUID = "ruleUuid";
	
    public RuleDataSourceRecord(String type, String ownerName, String ownerID, String name, String description, String medium, String format, boolean published, boolean subscribed, String uuid) {
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

    public void setType(String type) {
        setAttribute(TYPE, type);
    }

    public String getType() {
        return getAttributeAsString(TYPE);
    }
    
    public void setOwnerName(String ownerName) {
        setAttribute(OWNERNAME, ownerName);
    }

    public String getOwnerName() {
        return getAttributeAsString(OWNERNAME);
    }
    
    public void setOwnerID(String ownerID) {
        setAttribute(OWNERID, ownerID);
    }

    public String getOwnerID() {
        return getAttributeAsString(OWNERID);
    }

    public String getName() {
        return getAttributeAsString(NAME);
    }

    public void setName(String name) {
        setAttribute(NAME, name);
    }

    public void setDescription(String description) {
        setAttribute(DESCRIPTION, description);
    }

    public String getDescription() {
        return getAttributeAsString(DESCRIPTION);
    }

    public void setMedium(String medium) {
        setAttribute(MEDIUM, medium);
    }

    public String getMedium() {
        return getAttributeAsString(MEDIUM);
    }

    public void setFormat(String format) {
        setAttribute(FORMAT, format);
    }

    public String getFormat() {
        return getAttributeAsString(FORMAT);
    }

    public void setPublished(boolean published) {
        setAttribute(PUBLISHED, published);
    }

    public boolean isPublished() {
        return getAttributeAsBoolean(PUBLISHED);
    }
    
    public void setSubscribed(boolean subscribed) {
        setAttribute(SUBSCRIBED, subscribed);
    }

    public boolean isSubscribed() {
        return getAttributeAsBoolean(SUBSCRIBED);
    }

    public String getFieldValue(String field) {
        return getAttributeAsString(field);
    }
    
    public void setUserName(String userName) {
        setAttribute(USERNAME, userName);
    }

    public String getUserName() {
        return getAttributeAsString(USERNAME);
    }
}