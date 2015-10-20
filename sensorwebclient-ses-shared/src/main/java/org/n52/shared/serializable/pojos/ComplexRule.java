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

public class ComplexRule implements Serializable {

    private static final long serialVersionUID = 7282124349339730944L;

    private int id;

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
        this.id = complexRuleDTO.getId();
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