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