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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {

    private static final long serialVersionUID = -6378856998449759965L;

    private int id;

    private String userName; // login name

    private String name;

    private String password;

    private String eMail;

    private String registerID;

    private UserRole role;

    private boolean activated;

    private String wnsSmsId;

    private String wnsEmailId;

    private Set<BasicRule> basicRules = new HashSet<BasicRule>();

    private Set<ComplexRule> complexRules = new HashSet<ComplexRule>();
    
    private Date date;
    
    private Date falseLoginDate;
    
    private int falseLoginCount;
    
    private boolean active;
    
    private boolean emailVerified;
    
    private boolean passwordChanged;

    public User() {
        // for serialization only
    }

    public User(UserDTO userDTO) {
        this.id = userDTO.getId();
        this.userName = userDTO.getUserName();
        this.name = userDTO.getName();
        this.password = userDTO.getPassword();
        this.eMail = userDTO.geteMail();
        this.registerID = userDTO.getRegisterID();
        this.role = userDTO.getRole();
        this.activated = userDTO.getActivated();
        this.wnsEmailId = userDTO.getWnsEmailId();
        this.wnsSmsId = userDTO.getWnsSmsId();
        this.date = userDTO.getDate();
    }

    public User(int id, String userName, String name, String password, String eMail, UserRole role,
            boolean activated) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.role = role;
        this.activated = activated;
        this.date = new Date();
    }

    public User(int id, String userName, String name, String password, String eMail, UserRole role) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.role = role;
        this.date = new Date();
    }

    public User(String userName, String name, String password, String eMail, UserRole role,
            boolean activated) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.role = role;
        this.activated = activated;
        this.date = new Date();
    }

    public User(String userName, String name, String password, String eMail, UserRole role,
            boolean activated, String wnsSmsmId, String wnsEmailId) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.role = role;
        this.activated = activated;
        this.wnsSmsId = wnsSmsmId;
        this.wnsEmailId = wnsEmailId;
        this.date = new Date();
    }

    public User(String userName, String name, String password, String eMail) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.date = new Date();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the user name.
     * 
     * @param userName
     *            the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the e mail.
     * 
     * @return the e mail
     */
    public String geteMail() {
        return this.eMail;
    }

    /**
     * Sets the e mail.
     * 
     * @param eMail
     *            the new e mail
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Sets the role.
     * 
     * @param role
     *            the new role
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Gets the role.
     * 
     * @return the role
     */
    public UserRole getRole() {
        return this.role;
    }

    /**
     * Gets the activated.
     * 
     * @return the activated
     */
    public boolean getActivated() {
        return this.activated;
    }

    /**
     * Sets the activated.
     * 
     * @param activated
     *            the new activated
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * Gets the basic rules.
     * 
     * @return the basic rules
     */
    public Set<BasicRule> getBasicRules() {
        return this.basicRules;
    }

    /**
     * Sets the basic rules.
     * 
     * @param basicRules
     *            the new basic rules
     */
    public void setBasicRules(Set<BasicRule> basicRules) {
        this.basicRules = basicRules;
    }

    /**
     * Sets the complex rules.
     * 
     * @param complexRules
     *            the new complex rules
     */
    public void setComplexRules(Set<ComplexRule> complexRules) {
        this.complexRules = complexRules;
    }

    /**
     * Gets the complex rules.
     * 
     * @return the complex rules
     */
    public Set<ComplexRule> getComplexRules() {
        return this.complexRules;
    }

    /**
     * Sets the register ID
     * 
     * @param registerID
     *            the register ID
     */
    public void setRegisterID(String registerID) {
        this.registerID = registerID;
    }

    /**
     * Gets the register ID
     * 
     * @return the register ID
     */
    public String getRegisterID() {
        return this.registerID;
    }

    /**
     * @return wns sms id
     */
    public String getWnsSmsId() {
        return this.wnsSmsId;
    }

    /**
     * @param wnsSmsId
     */
    public void setWnsSmsId(String wnsSmsId) {
        this.wnsSmsId = wnsSmsId;
    }

    /**
     * @return wns email id
     */
    public String getWnsEmailId() {
        return this.wnsEmailId;
    }

    /**
     * @param wnsEmailId
     */
    public void setWnsEmailId(String wnsEmailId) {
        this.wnsEmailId = wnsEmailId;
    }

    /**
     * @return {@link Date}
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    public Date getFalseLoginDate() {
        return falseLoginDate;
    }

    public void setFalseLoginDate(Date falseLoginDate) {
        this.falseLoginDate = falseLoginDate;
    }

    public int getFalseLoginCount() {
        return falseLoginCount;
    }

    public void setFalseLoginCount(int falseLoginCount) {
        this.falseLoginCount = falseLoginCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }
}
