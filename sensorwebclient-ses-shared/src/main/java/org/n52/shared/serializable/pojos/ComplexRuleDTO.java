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
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 * 
 */
public class ComplexRuleDTO implements Serializable {

    /** The Constant serialVersionUID */
    private static final long serialVersionUID = 4893019968669577779L;

    /**
     * The id.
     */
    private int id;

    /**
     * The name.
     */
    private String name;

    /**
     * The ruelType.
     */
    private String ruleType;

    /**
     * The Description
     */
    private String description;

    /**
     * The release
     */
    private boolean release;

    /**
     * The ownerID
     */
    private int ownerID;
    
    /** The eml */
    private String eml;
    
    /** The subscribed */
    private boolean subscribed;
    
    /** The medium. */
    private String medium;
    
    /** The format. */
    private String format;
    
    /** The tree */
    private String tree;
    
    /** The ownerName */
    private String ownerName;
    
    private String sensor;
    
    private String phenomenon;

    /**
     * @param id
     *            the id
     * @param name
     *            the name
     * @param ruleType
     *            the ruleType
     * @param description
     *            the description
     * @param release
     *            the release
     * @param ownerID
     *            the ownerID
     * @param eml 
     * @param subscribed 
     * @param medium 
     * @param format 
     * @param tree 
     * @param sensor 
     * @param phenomenon 
     * 
     */
    public ComplexRuleDTO(int id, String name, String ruleType, String description, boolean release, int ownerID, 
            String eml, boolean subscribed, String medium, String format, String tree, String sensor, String phenomenon) {
        this.id = id;
        this.name = name;
        this.ruleType = ruleType;
        this.description = description;
        this.release = release;
        this.ownerID = ownerID;
        this.eml = eml;
        this.subscribed = subscribed;
        this.medium = medium;
        this.format = format;
        this.tree = tree;
        this.sensor = sensor;
        this.phenomenon = phenomenon;
    }
    
    /**
     * empty contrsuctor
     */
    public ComplexRuleDTO() {
        //
    }

    /**
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ruleType
     */
    public String getRuleType() {
        return this.ruleType;
    }

    /**
     * @param ruleType
     *            the ruleType to set
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the release
     */
    public boolean isRelease() {
        return this.release;
    }

    /**
     * @param release
     *            the release to set
     */
    public void setRelease(boolean release) {
        this.release = release;
    }

    /**
     * @return the ownerID
     */
    public int getOwnerID() {
        return this.ownerID;
    }

    /**
     * @param ownerID
     *            the ownerID to set
     */
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * @return eml
     */
    public String getEml() {
        return this.eml;
    }

    /**
     * @param eml
     */
    public void setEml(String eml) {
        this.eml = eml;
    }

    /**
     * @return isSubscribed
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
     * @return tree
     */
    public String getTree() {
        return this.tree;
    }

    /**
     * @param tree
     */
    public void setTree(String tree) {
        this.tree = tree;
    }

    /**
     * 
     * @return ownerName
     */
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * @param ownerName
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
}