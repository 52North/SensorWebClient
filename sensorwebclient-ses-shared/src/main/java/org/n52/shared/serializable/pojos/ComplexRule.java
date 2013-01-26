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

    private boolean published;

    private int ownerID;
    
    private String eml;
    
    private boolean subscribed;
    
    private String medium;
    
    private String format;
    
    private String tree;
    
    private String sensor;
    
    private String phenomenon;

    public ComplexRule(ComplexRuleDTO complexRuleDTO) {
        this.complexRuleID = complexRuleDTO.getId();
        this.name = complexRuleDTO.getName();
        this.ruleType = complexRuleDTO.getRuleType();
        this.description = complexRuleDTO.getDescription();
        this.published = complexRuleDTO.isRelease();
        this.ownerID = complexRuleDTO.getOwnerID();
        this.eml = complexRuleDTO.getEml();
        this.subscribed = complexRuleDTO.isSubscribed();
        this.medium = complexRuleDTO.getMedium();
        this.format = complexRuleDTO.getFormat();
        this.tree = complexRuleDTO.getTree();
        this.sensor = complexRuleDTO.getSensor();
        this.phenomenon  =complexRuleDTO.getPhenomenon();
    }

    public ComplexRule(String name, String ruleType, String description, boolean release, int ownerID, String eml, boolean subscribed, String medium, String format, String tree) {
        this.name = name;
        this.ruleType = ruleType;
        this.description = description;
        this.published = release;
        this.ownerID = ownerID;
        this.eml = eml;
        this.subscribed = subscribed;
        this.medium = medium;
        this.format = format;
        this.tree = tree;
    }

    public ComplexRule() {
        // for serialization
    }

    public int getComplexRuleID() {
        return this.complexRuleID;
    }

    public void setComplexRuleID(int id) {
        this.complexRuleID = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerID() {
        return this.ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isPublished() {
        return this.published;
    }

    public void setPublished(boolean published) {
        this.published = published;
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

    public String getTree() {
        return this.tree;
    }

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