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