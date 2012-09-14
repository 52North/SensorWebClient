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
    
    private String sensor;
    
    private String phenomenon;

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
            int ownerID, String eml, boolean subscribed, String medium, String format, String sensor, String phenomenon) {
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
        this.sensor = sensor;
        this.phenomenon = phenomenon;
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

    /**
     * @return sensor
     */
    public String getSensor() {
        return this.sensor;
    }

    /**
     * @param sensor
     */
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    /**
     * @return phenomenon
     */
    public String getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * @param phenomenon
     */
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
}