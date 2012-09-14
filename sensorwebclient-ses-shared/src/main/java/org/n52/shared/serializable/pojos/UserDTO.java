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

/**
 * The Class UserDTO.
 * 
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 */
public class UserDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9034725345386455848L;

    /** The id. */
    private int id;

    /** The user name. */
    private String userName;

    /** The name. */
    private String name;

    /** The password. */
    private String password;

    /** The e mail. */
    private String eMail;

    /** The handy nr. */
    private String handyNr;

    /** The register ID. */
    private String registerID;

    /** The role. */
    private UserRole role;

    /** The activated. */
    private boolean activated;

    /** The wnsSmsId. */
    private String wnsSmsId;

    /** The wnsEmailId. */
    private String wnsEmailId;

    /** The basic rules. */
    private Set<BasicRuleDTO> basicRules = new HashSet<BasicRuleDTO>();

    /** The complex rules */
    private Set<ComplexRuleDTO> complexRules = new HashSet<ComplexRuleDTO>();
    
    /** The registration time */
    private Date date;
    
    private boolean emailVerified;
    
    private boolean passwordChanged;
    
    private String newPassword;

    /**
     * Instantiates a new user dto.
     */
    public UserDTO() {
        // empty constructor
    }

    /**
     * Instantiates a new user dto.
     * 
     * @param id
     *            the id
     */
    public UserDTO(int id) {
        this.id = id;
    }

    /**
     * Instantiates a new user dto.
     * 
     * @param id
     *            the id
     * @param userName
     *            the user name
     * @param name
     *            the name
     * @param password
     *            the password
     * @param eMail
     *            the e mail
     * @param handyNr
     *            the handynr
     * @param registerID
     *            the register ID
     * @param role
     *            the role
     * @param activated
     *            the activated
     * @param wnsSmsId
     *            the WNS sms id
     * @param wnsEmailId
     *            the WNS email id
     * @param basicRules
     *            the basic rules
     * @param complexRules
     *            the complex rules
     * @param date 
     */
    public UserDTO(int id, String userName, String name, String password, String eMail, String handyNr,
            String registerID, UserRole role, boolean activated, String wnsSmsId, String wnsEmailId,
            Set<BasicRuleDTO> basicRules, Set<ComplexRuleDTO> complexRules, Date date) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.handyNr = handyNr;
        this.registerID = registerID;
        this.role = role;
        this.activated = activated;
        this.basicRules = basicRules;
        this.wnsSmsId = wnsSmsId;
        this.wnsEmailId = wnsEmailId;
        this.complexRules = complexRules;
        this.date = date;
    }

    /**
     * Instantiates a new user dto.
     * 
     * @param id
     *            the id
     * @param userName
     *            the user name
     * @param name
     *            the name
     * @param password
     *            the password
     * @param email
     *            the email
     * @param handyNr
     *            the handy nr
     * @param role
     *            the role
     * @param date 
     */
    public UserDTO(int id, String userName, String name, String password, String email, String handyNr, UserRole role, Date date) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = email;
        this.handyNr = handyNr;
        this.role = role;
        this.date = date;
    }

    /**
     * Instantiates a new user dto.
     * 
     * @param userName
     *            the user name
     * @param name
     *            the name
     * @param password
     *            the password
     * @param eMail
     *            the e mail
     * @param handyNr
     *            the handy nr
     * @param role
     *            the role
     * @param activated
     *            the activated
     * @param date 
     */
    public UserDTO(String userName, String name, String password, String eMail, String handyNr, UserRole role,
            boolean activated, Date date) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.eMail = eMail;
        this.handyNr = handyNr;
        this.role = role;
        this.activated = activated;
        this.date = date;
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
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user name.
     * 
     * @return the user name
     */
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
     * Gets the handy nr.
     * 
     * @return the handy nr
     */
    public String getHandyNr() {
        return this.handyNr;
    }

    /**
     * Sets the handy nr.
     * 
     * @param handyNr
     *            the new handy nr
     */
    public void setHandyNr(String handyNr) {
        this.handyNr = handyNr;
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
     * Sets the role.
     * 
     * @param role
     *            the new role
     */
    public void setRole(UserRole role) {
        this.role = role;
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
    public Set<BasicRuleDTO> getBasicRules() {
        return this.basicRules;
    }

    /**
     * Sets the basic rules.
     * 
     * @param basicRules
     *            the new basic rules
     */
    public void setBasicRules(Set<BasicRuleDTO> basicRules) {
        this.basicRules = basicRules;
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
     * @return the WNS sms id
     */
    public String getWnsSmsId() {
        return this.wnsSmsId;
    }

    /**
     * @param wnsSmsId
     *            the WNS sms id
     */
    public void setWnsSmsId(String wnsSmsId) {
        this.wnsSmsId = wnsSmsId;
    }

    /**
     * @return the WNS email id
     */
    public String getWnsEmailId() {
        return this.wnsEmailId;
    }

    /**
     * @param wnsEmailId
     *            the WNS email id
     */
    public void setWnsEmailId(String wnsEmailId) {
        this.wnsEmailId = wnsEmailId;
    }

    /**
     * @param complexRules
     *            the complexRules to set
     */
    public void setComplexRules(Set<ComplexRuleDTO> complexRules) {
        this.complexRules = complexRules;
    }

    /**
     * @return the complexRules
     */
    public Set<ComplexRuleDTO> getComplexRules() {
        return this.complexRules;
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
