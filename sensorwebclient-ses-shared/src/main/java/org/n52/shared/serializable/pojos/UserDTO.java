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

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 9034725345386455848L;

    private int id;

    private String userName;

    private String name;

    private String password;

    private String eMail;

    private String registerID;

    private UserRole role;

    private boolean activated;

    private String wnsSmsId;

    private String wnsEmailId;

    private Set<BasicRuleDTO> basicRules = new HashSet<BasicRuleDTO>();

    private Set<ComplexRuleDTO> complexRules = new HashSet<ComplexRuleDTO>();
    
    private Date date;
    
    private boolean emailVerified;
    
    private boolean passwordChanged;
    
    private String newPassword;

    UserDTO() {
        // for serialization only
    }

    public UserDTO(int id, String userName, String name, String password, String eMail,
            String registerID, UserRole role, boolean activated, String wnsSmsId, String wnsEmailId,
            Set<BasicRuleDTO> basicRules, Set<ComplexRuleDTO> complexRules, Date date) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.registerID = registerID;
        this.role = role;
        this.activated = activated;
        this.basicRules = basicRules;
        this.wnsSmsId = wnsSmsId;
        this.wnsEmailId = wnsEmailId;
        this.complexRules = complexRules;
        this.date = date;
    }

    public UserDTO(int id, String userName, String name, String password, String email, UserRole role, Date date) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = email;
        this.role = role;
        this.date = date;
    }

    public UserDTO(String userName, String name, String password, String eMail, UserRole role,
            boolean activated, Date date) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.role = role;
        this.activated = activated;
        this.date = date;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String geteMail() {
        return this.eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean getActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Set<BasicRuleDTO> getBasicRules() {
        return this.basicRules;
    }

    public void setBasicRules(Set<BasicRuleDTO> basicRules) {
        this.basicRules = basicRules;
    }

    public void setRegisterID(String registerID) {
        this.registerID = registerID;
    }

    public String getRegisterID() {
        return this.registerID;
    }

    public String getWnsSmsId() {
        return this.wnsSmsId;
    }

    public void setWnsSmsId(String wnsSmsId) {
        this.wnsSmsId = wnsSmsId;
    }

    public String getWnsEmailId() {
        return this.wnsEmailId;
    }

    public void setWnsEmailId(String wnsEmailId) {
        this.wnsEmailId = wnsEmailId;
    }

    public void setComplexRules(Set<ComplexRuleDTO> complexRules) {
        this.complexRules = complexRules;
    }

    public Set<ComplexRuleDTO> getComplexRules() {
        return this.complexRules;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
