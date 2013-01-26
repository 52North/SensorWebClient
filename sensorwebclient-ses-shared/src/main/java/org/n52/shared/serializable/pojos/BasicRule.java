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

public class BasicRule implements Serializable {

    private static final long serialVersionUID = -5628735312144877770L;

    private int basicRuleID;

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
    
    private FeedingMetadata feedingMetadata;
    
    public BasicRule(BasicRuleDTO basicRuleDTO) {
        this.basicRuleID = basicRuleDTO.getId();
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
        
        // XXX check for inconsistence regarding to missing FOI+offering+serviceUrl
        this.feedingMetadata = basicRuleDTO.getFeedingMetadata();
        
    }

    public BasicRule(String name, String ruleType, String type, String description, boolean release, int ownerID,
            String eml, boolean subscribed) {
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

    public int getBasicRuleID() {
        return this.basicRuleID;
    }

    public void setBasicRuleID(int id) {
        this.basicRuleID = id;
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

    public FeedingMetadata getFeedingMetadata() {
        return feedingMetadata;
    }

    public void setFeedingMetadata(FeedingMetadata feedingMetadata) {
        this.feedingMetadata = feedingMetadata;
    }

}