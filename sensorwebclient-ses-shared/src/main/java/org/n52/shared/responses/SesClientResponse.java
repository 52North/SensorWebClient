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
    
    private Rule rule;
    
    private String message;
    
    private List sensorList;
    
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
        this.sensorList = list;
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

    public List getSensorList() {
        return this.sensorList;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }
}