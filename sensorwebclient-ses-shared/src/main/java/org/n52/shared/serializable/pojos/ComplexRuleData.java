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
import java.util.ArrayList;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public class ComplexRuleData implements Serializable {
    
    private static final long serialVersionUID = -3906296062286489091L;

    private ArrayList<String> ruleNames;
    private ArrayList<String> treeContent;

    private String title;
    private String description;
    private boolean publish;
    private int userID;
    
    private String sensor;
    
    private String phenomenon;

    /**
     * 
     */
    public ComplexRuleData() {
        // empty constructor
    }

    /**
     * @param ruleNames
     * @param title 
     * @param description 
     * @param publish 
     * @param userID 
     * @param treeContent 
     * @param sensor 
     * @param phenomenon 
     */
    public ComplexRuleData(ArrayList<String> ruleNames, String title, String description, boolean publish, int userID, ArrayList<String> treeContent, String sensor, String phenomenon){
        this.ruleNames = ruleNames;
        this.title = title;
        this.description = description;
        this.publish = publish;
        this.userID = userID;
        this.treeContent = treeContent;
        this.sensor = sensor;
        this.phenomenon = phenomenon;
    }
    
    /**
     * @return {@link ArrayList} with ruleNames
     */
    public ArrayList<String> getRuleNames() {
        return this.ruleNames;
    }

    /**
     * @param ruleNames
     */
    public void setRuleNames(ArrayList<String> ruleNames) {
        this.ruleNames = ruleNames;
    }

    /**
     * 
     * @return title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return publish
     */
    public boolean isPublish() {
        return this.publish;
    }

    /**
     * @param publish
     */
    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    /**
     * @return userID
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * 
     * @return treeContent
     */
    public ArrayList<String> getTreeContent() {
        return this.treeContent;
    }

    /**
     * @param treeContent
     */
    public void setTreeContent(ArrayList<String> treeContent) {
        this.treeContent = treeContent;
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