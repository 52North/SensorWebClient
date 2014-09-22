/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
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