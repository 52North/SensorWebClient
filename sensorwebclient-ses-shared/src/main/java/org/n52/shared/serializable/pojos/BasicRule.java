/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public class BasicRule implements Serializable {

    private static final long serialVersionUID = -5628735312144877770L;

    private int id;

    private String uuid;
    
    private String name;

    private String ruleType;

    private String type;

    private String description;

    private boolean published;

    private int ownerID;

    private String eml;

    private boolean subscribed;
    
    private String medium;
    
    private String format;
    
    private TimeseriesMetadata timeseriesMetadata;
    
    public BasicRule(BasicRuleDTO basicRuleDTO) {
        this.id = basicRuleDTO.getId();
        this.name = basicRuleDTO.getName();
        this.ruleType = basicRuleDTO.getRuleType();
        this.setType(basicRuleDTO.getType());
        this.description = basicRuleDTO.getDescription();
        this.published = basicRuleDTO.isRelease();
        this.ownerID = basicRuleDTO.getOwnerID();
        this.eml = basicRuleDTO.getEml();
        this.subscribed = basicRuleDTO.isSubscribed();
        this.medium = basicRuleDTO.getMedium();
        this.format = basicRuleDTO.getFormat();
        this.setUuid(basicRuleDTO.getUuid());
        
        // XXX check for inconsistence regarding to missing FOI+offering+serviceUrl
        this.timeseriesMetadata = basicRuleDTO.getTimeseriesMetadata();
        
    }

    public BasicRule(String name, String ruleType, String type, String description, boolean release, int ownerID, String eml, boolean subscribed) {
        this.name = name;
        this.ruleType = ruleType;
        this.type = type;
        this.description = description;
        this.published = release;
        this.ownerID = ownerID;
        this.eml = eml;
        this.subscribed = subscribed;
    }

    public BasicRule() {
        // for serialization
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return this.published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getOwnerID() {
        return this.ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public String getEml() {
        return this.eml;
    }

    public void setEml(String eml) {
        this.eml = eml;
    }

    public boolean isSubscribed() {
        return this.subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String getMedium() {
        return this.medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public TimeseriesMetadata getTimeseriesMetadata() {
        return timeseriesMetadata;
    }

    public void setTimeseriesMetadata(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}