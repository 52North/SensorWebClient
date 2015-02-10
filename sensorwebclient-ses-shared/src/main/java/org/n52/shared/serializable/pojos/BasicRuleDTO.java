/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

/**
 * The Class BasicRuleDTO.
 * 
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 */
public class BasicRuleDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5688964593672148306L;

    /** The id. */
    private int id;

    /** The name. */
    private String name;

    /** The rule type. */
    private String ruleType;

    /** The type of a basic rule */
    private String type;

    /** The description. */
    private String description;

    /** The release. */
    private boolean release;

    /** The owner. */
    private int ownerID;

    /** The eml. */
    private String eml;

    /** The is subscribed. */
    private boolean subscribed;
    
    /** The medium. */
    private String medium;
    
    /** The format. */
    private String format;
    
    /** The ownerName */
    private String ownerName;
    
    private String uuid;
    
    private TimeseriesMetadata timeseriesMetadata;

    /**
     * Instantiates a new basic rule dto.
     */
    public BasicRuleDTO() {
        //
    }

    /**
     * Instantiates a new basic rule dto.
     * 
     * @param id
     *            the id
     */
    public BasicRuleDTO(int id) {
        this.id = id;
    }

    /**
     * Instantiates a new basic rule dto.
     * 
     * @param id
     *            the id
     * @param name
     *            the name
     * @param ruleType
     *            the rule type
     * @param type
     *            the type of the basic rule
     * @param description
     *            the description
     * @param release
     *            the release
     * @param ownerID
     *            the ownerID
     * @param eml
     *            the eml
     * @param subscribed
     * @param medium 
     * @param format 
     */
    public BasicRuleDTO(int id, String name, String ruleType, String type, String description, boolean release,
            int ownerID, String eml, boolean subscribed, String medium, String format, String uuid, TimeseriesMetadata timeseriesMetadata) {
        this.id = id;
        this.name = name;
        this.ruleType = ruleType;
        this.setType(type);
        this.description = description;
        this.release = release;
        this.ownerID = ownerID;
        this.eml = eml;
        this.subscribed = subscribed;
        this.medium = medium;
        this.format = format;
        this.uuid = uuid;
        this.timeseriesMetadata = timeseriesMetadata;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the rule type.
     * 
     * @return the rule type
     */
    public String getRuleType() {
        return this.ruleType;
    }

    /**
     * Sets the rule type.
     * 
     * @param ruleType
     *            the new rule type
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if is release.
     * 
     * @return true, if is release
     */
    public boolean isRelease() {
        return this.release;
    }

    /**
     * Sets the release.
     * 
     * @param release
     *            the new release
     */
    public void setRelease(boolean release) {
        this.release = release;
    }

    /**
     * Gets the owner.
     * 
     * @return the ownerID
     */
    public int getOwnerID() {
        return this.ownerID;
    }

    /**
     * Sets the owner.
     * 
     * @param ownerID
     *            the new owner
     */
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * @return the eml
     */
    public String getEml() {
        return this.eml;
    }

    /**
     * 
     * @param eml
     */
    public void setEml(String eml) {
        this.eml = eml;
    }

    /**
     * @return {@link Boolean}
     */
    public boolean isSubscribed() {
        return this.subscribed;
    }

    /**
     * @param subscribed
     */
    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * 
     * @return medium
     */
    public String getMedium() {
        return this.medium;
    }

    /**
     * @param medium
     */
    public void setMedium(String medium) {
        this.medium = medium;
    }

    /**
     * @return format
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * @param format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return owner name
     */
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * 
     * @param ownerName
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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