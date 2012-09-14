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
package org.n52.client.model.data.representations;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class UserRecord.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>s
 */
public class UserRecord extends ListGridRecord {

    /**
     * Instantiates a new user record.
     */
    public UserRecord() {
        //
    }

    /**
     * Instantiates a new user record.
     * 
     * @param parameterId
     * @param userName 
     * @param name
     *            the name
     * @param password
     *            the password
     * @param eMail
     *            the e mail
     * @param handy
     *            the handy
     * @param role
     *            the role
     */
    public UserRecord(String id, String userName, String name, String password, String eMail, String handy, String role) {
        setId(id);
        setUserName(userName);
        setName(name);
        setPassword(password);
        setEMail(eMail);
        setHandy(handy);
        setRole(role);
    }

    /**
     * Gets the parameterId.
     * 
     * @return the parameterId
     */
    public String getId() {
        return getAttributeAsString("parameterId");
    }

    /**
     * Sets the parameterId.
     * @param parameterId 
     */
    public void setId(String id) {
        setAttribute("parameterId", id);
    }

    /**
     * @return userName
     */
    public String getUserName() {
        return getAttributeAsString("userName");
    }

    /**
     * @param userName
     */
    private void setUserName(String userName) {
        setAttribute("userName", userName);

    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return getAttributeAsString("name");
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        setAttribute("name", name);
    }

    /**
     * Sets the e mail.
     * 
     * @param eMail
     *            the new e mail
     */
    public void setEMail(String eMail) {
        setAttribute("eMail", eMail);
    }

    /**
     * Gets the e mail.
     * 
     * @return the e mail
     */
    public String getEMail() {
        return getAttributeAsString("eMail");
    }

    /**
     * Sets the handy.
     * 
     * @param handy
     *            the new handy
     */
    public void setHandy(String handy) {
        setAttribute("handy", handy);
    }

    /**
     * Gets the handy.
     * 
     * @return the handy
     */
    public String getHandy() {
        return getAttributeAsString("handy");
    }

    /**
     * Sets the role.
     * 
     * @param role
     *            the new role
     */
    public void setRole(String role) {
        setAttribute("role", role);
    }

    /**
     * Gets the role.
     * 
     * @return the role
     */
    public String getRole() {
        return getAttributeAsString("role");
    }

    /**
     * Gets the field value.
     * 
     * @param field
     *            the field
     * @return the field value
     */
    public String getFieldValue(String field) {
        return getAttributeAsString(field);
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        setAttribute("password", password);
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return getAttributeAsString("password");
    }
}