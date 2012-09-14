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

public class ComplexRule implements Serializable {

    private static final long serialVersionUID = 7282124349339730944L;

    private int complexRuleID;

    private String name;

    private String ruleType;

    private String description;

    private boolean release;

    private int ownerID;
    
    private String eml;
    
    private boolean subscribed;
    
    private String medium;
    
    private String format;
    
    private String tree;
    
    private String sensor;
    
    private String phenomenon;

    /**
     * Instantiates a new complex rule by a given complexRuleDTO
     * 
     * @param complexRuleDTO
     *            the complex Rule as DTO
     */
    public ComplexRule(ComplexRuleDTO complexRuleDTO) {
        this.complexRuleID = complexRuleDTO.getId();
        this.name = complexRuleDTO.getName();
        this.ruleType = complexRuleDTO.getRuleType();
        this.description = complexRuleDTO.getDescription();
        this.release = complexRuleDTO.isRelease();
        this.ownerID = complexRuleDTO.getOwnerID();
        this.eml = complexRuleDTO.getEml();
        this.subscribed = complexRuleDTO.isSubscribed();
        this.medium = complexRuleDTO.getMedium();
        this.format = complexRuleDTO.getFormat();
        this.tree = complexRuleDTO.getTree();
        this.sensor = complexRuleDTO.getSensor();
        this.phenomenon  =complexRuleDTO.getPhenomenon();
    }

    /**
     * Instantiates a new complex rule.
     * 
     * @param name
     *            the name
     * @param ruleType
     *            the rule type
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
     */
    public ComplexRule(String name, String ruleType, String description, boolean release, int ownerID, String eml, boolean subscribed, String medium, String format, String tree) {
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
    }

    /**
     * Instantiates a new complex rule.
     */
    public ComplexRule() {
        // basic constructor is needed
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getComplexRuleID() {
        return this.complexRuleID;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setComplexRuleID(int id) {
        this.complexRuleID = id;
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
     * Gets the ownerID.
     * 
     * @return the ownerID
     */
    public int getOwnerID() {
        return this.ownerID;
    }

    /**
     * Sets the ownerID.
     * 
     * @param ownerID
     *            the new ownerID
     */
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
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