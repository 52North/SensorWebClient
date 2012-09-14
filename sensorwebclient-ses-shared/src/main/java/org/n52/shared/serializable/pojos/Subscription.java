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

public class Subscription implements Serializable {

    private static final long serialVersionUID = -4999105897968249627L;

    /** The id. */
    private int id;
    
    /** The userID. */
    private int userID;

    /** The ruleID. */
    private int ruleID;

    /** The subscriptionID. */
    private String subscriptionID;
    
    /** The medium. */
    private String medium;
    
    /** The format. */
    private String format;



    /**
     * Instantiates a new subscription.
     */
    public Subscription() {
        // basic constructor is needed
    }

    /**
     * 
     * @param userID 
     * @param ruleID
     * @param subscriptionID
     * @param medium 
     * @param format 
     */
    public Subscription(int userID, int ruleID, String subscriptionID, String medium, String format){
        this.userID = userID;
        this.ruleID = ruleID;
        this.subscriptionID = subscriptionID;
        this.medium = medium;
        this.format = format;
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
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

  
    /**
     * @return ruleID
     */
    public int getRuleID() {
        return this.ruleID;
    }

    /**
     * @param ruleID
     */
    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    /**
     * @return subscriptionID
     */
    public String getSubscriptionID() {
        return this.subscriptionID;
    }

    /**
     * @param subscriptionID
     */
    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    /**
     * @return the userID
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
     * @return medium
     */
    public String getMedium() {
        return this.medium;
    }

    /**
     * 
     * @param medium
     */
    public void setMedium(String medium) {
        this.medium = medium;
    }

    /**
     * 
     * @return format
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * 
     * @param format
     */
    public void setFormat(String format) {
        this.format = format;
    }
}