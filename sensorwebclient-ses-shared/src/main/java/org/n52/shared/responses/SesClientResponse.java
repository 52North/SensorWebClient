/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.responses;

import static org.n52.shared.responses.SesClientResponseType.ERROR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.session.SessionInfo;

public class SesClientResponse implements Serializable {

    private static final long serialVersionUID = 5521713761376060103L;
    
    private String UserNameMessage = "User name is invalid!";
    
    private String EmailMessage = "E-Mail is invalid!";

    private SesClientResponseType responseType = ERROR;
    
    private SessionInfo sessionInfo;

    private UserDTO user;

    private ArrayList basicList;
    
    private ArrayList complexList;
    
    private ArrayList<UserDTO> userList;
    
    private Rule rule;
    
    private String message;
    
    private List objectList;
    
    public SesClientResponse() {
        // empty constructor
    }

    public SesClientResponse(SesClientResponseType type){
        this.responseType = type;
    }
    
    public SesClientResponse(SesClientResponseType type, UserDTO user){
        this.responseType = type;
        this.user = user;
    }
    
    public SesClientResponse(SesClientResponseType type, UserDTO user, ArrayList list){
        this.responseType = type;
        this.user = user;
        this.complexList = list;
    }
    
    public SesClientResponse(SesClientResponseType type, ArrayList list){
        this.responseType = type;
        this.basicList = list;
    }
    
    public SesClientResponse(SesClientResponseType type, List list){
        this.responseType = type;
        this.objectList = list;
    }
    
    public SesClientResponse(SesClientResponseType type, ArrayList list, ArrayList complexList){
        this.responseType = type;
        this.basicList = list;
        this.complexList = complexList;
    }


    public SesClientResponse(SesClientResponseType type, Rule rule) {
        this.responseType = type;
        this.rule = rule;
    }

    public SesClientResponse(SesClientResponseType type, String message) {
        this.responseType = type;
        this.message = message;
    }
    
    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getUserNameMessage() {
        return this.UserNameMessage;
    }

    public String getEmailMessage() {
        return this.EmailMessage;
    }

    public SesClientResponseType getType() {
        return this.responseType;
    }

    public ArrayList getBasicRules() {
        return this.basicList;
    }
    
    public Rule getBasicRule() {
        return this.rule;
    }

    public String getMessage() {
        return this.message;
    }

    public ArrayList getComplexRules() {
        return this.complexList;
    }

    public void setComplexList(ArrayList complexList) {
        this.complexList = complexList;
    }

    public Rule getRule() {
        return this.rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public List getObjectList() {
        return this.objectList;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }
}