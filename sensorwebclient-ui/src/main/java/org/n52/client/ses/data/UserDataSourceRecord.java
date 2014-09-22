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
package org.n52.client.ses.data;

import com.smartgwt.client.widgets.grid.ListGridRecord;

public class UserDataSourceRecord extends ListGridRecord {
	
	public static final String PARAMETERID = "parameterId";
	public static final String USERNAME = "userName";
	public static final String NAME = "name";
	public static final String EMAIL = "eMail";
	public static final String ROLE = "role";
	public static final String PASSWORD = "password";
	
    public UserDataSourceRecord() {
        //
    }

    public UserDataSourceRecord(String id, String userName, String name, String password, String eMail, String role) {
        setId(id);
        setUserName(userName);
        setName(name);
        setPassword(password);
        setEMail(eMail);
        setRole(role);
    }

    public String getId() {
        return getAttributeAsString(PARAMETERID);
    }

    public void setId(String id) {
        setAttribute(PARAMETERID, id);
    }

    public String getUserName() {
        return getAttributeAsString(USERNAME);
    }

    private void setUserName(String userName) {
        setAttribute(USERNAME, userName);
    }

    public String getName() {
        return getAttributeAsString(NAME);
    }

    public void setName(String name) {
        setAttribute(NAME, name);
    }

    public void setEMail(String eMail) {
        setAttribute(EMAIL, eMail);
    }

    public String getEMail() {
        return getAttributeAsString(EMAIL);
    }

    public void setRole(String role) {
        setAttribute(ROLE, role);
    }

    public String getRole() {
        return getAttributeAsString(ROLE);
    }

    public String getFieldValue(String field) {
        return getAttributeAsString(field);
    }

    public void setPassword(String password) {
        setAttribute(PASSWORD, password);
    }

    public String getPassword() {
        return getAttributeAsString(PASSWORD);
    }
}