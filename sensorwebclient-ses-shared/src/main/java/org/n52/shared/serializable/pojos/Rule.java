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

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

public class Rule implements Serializable {

    private static final long serialVersionUID = -1913430711568432313L;

    /**
     * greater than operator
     */
    public static final int GREATERTHAN_OPERATOR = 2;
    
    /**
     * less than operator
     */
    public static final int LESSTHAN_OPERATOR = 3;
    
    /**
     * greater than or equal to operator
     */
    public static final int GREATERTHANOREQUALTO_OPERATOR = 4;
    
    /**
     * less than or equal to operator
     */
    public static final int LESSTHANOREQUALTO_OPERATOR = 5;
    
    /**
     * equal to operator
     */
    public static final int EQUALTO_OPERATOR = 0;
    
    /**
     * not equal to Operator
     */
    public static final int NOTEQUALTO_OPERATOR = 1;
    
    /** The rule type. */
    private SimpleRuleType ruleType;

    /** The title. */
    private String title;

    /** The station. */
    private String station;

    /** The phenomenon. */
    private String phenomenon;

    /** The notification type. */
    private String notificationType;

    /** The description. */
    private String description;

    /** The publish. */
    private boolean publish;

    /** The enter equals exit condition. */
    private boolean enterEqualsExitCondition;

    /** The r operator index. */
    private int rOperatorIndex;

    /** The r value. */
    private String rValue;

    /** The r unit. */
    private String rUnit;

    /** The c operator index. */
    private int cOperatorIndex;

    /** The c value. */
    private String cValue;

    /** The c unit. */
    private String cUnit;

    /** The user id. */
    private int userID;

    /** The count. */
    private String count;

    private String cCount;

    private String rTime;
    private String cTime;
    private String rTimeUnit;
    private String cTimeUnit;

    /**
     * Instantiates a new rule.
     */
    public Rule() {
        //
    }

    /**
     * Instantiates a new rule.
     * 
     * @param ruleType
     *            the rule type
     * @param title
     *            the title
     * @param station
     *            the station
     * @param phenomenon
     *            the phenomenon
     * @param notificationType
     *            the notification type
     * @param description
     *            the description
     * @param publish
     *            the publish
     * @param enterEqualsExitCondition
     *            the enter equals exit condition
     * @param rOperatorIndex
     *            the r operator index
     * @param rValue
     *            the r value
     * @param rUnit
     *            the r unit
     * @param cOperatorIndex
     *            the c operator index
     * @param cValue
     *            the c value
     * @param cUnit
     *            the c unit
     * @param userID 
     */
    public Rule(SimpleRuleType ruleType, String title, String station, String phenomenon,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int rOperatorIndex, String rValue, String rUnit, int cOperatorIndex, String cValue, String cUnit, int userID) {

        this.ruleType = ruleType;
        this.title = title;
        this.station = station;
        this.phenomenon = phenomenon;
        this.notificationType = notificationType;
        this.description = description;
        this.publish = publish;
        this.enterEqualsExitCondition = enterEqualsExitCondition;
        this.rOperatorIndex = rOperatorIndex;
        this.rValue = rValue;
        this.rUnit = rUnit;
        this.cOperatorIndex = cOperatorIndex;
        this.cValue = cValue;
        this.cUnit = cUnit;
        this.userID = userID;
    }

    /**
     * BasicRule_1: Tendenz_Anzahl
     * 
     * @param ruleType
     * @param title
     * @param station
     * @param phenomenon
     * @param notificationType
     * @param description
     * @param publish
     * @param enterEqualsExitCondition
     * @param rOperatorIndex
     * @param rValue
     * @param rUnit
     * @param cOperatorIndex
     * @param cValue
     * @param cUnit
     * @param userID
     * @param count
     * @param cCount
     */
    public Rule(SimpleRuleType ruleType, String title, String station, String phenomenon,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int rOperatorIndex, String rValue, String rUnit,int cOperatorIndex, String cValue, String cUnit, int userID, String count, String cCount) {

        this.ruleType = ruleType;
        this.title = title;
        this.station = station;
        this.phenomenon = phenomenon;
        this.notificationType = notificationType;
        this.description = description;
        this.publish = publish;
        this.enterEqualsExitCondition = enterEqualsExitCondition;
        this.rOperatorIndex = rOperatorIndex;
        this.rValue = rValue;
        this.rUnit = rUnit;
        this.cOperatorIndex = cOperatorIndex;
        this.cValue = cValue;
        this.cUnit = cUnit;
        this.userID = userID;
        this.count = count;
        this.cCount = cCount;
    }

    /**
     * BasicRule_2: Tendenz_Zeit
     * 
     * @param ruleType
     * @param title
     * @param station
     * @param phenomenon
     * @param notificationType
     * @param description
     * @param publish
     * @param enterEqualsExitCondition
     * @param rOperatorIndex
     * @param rValue
     * @param rUnit
     * @param cOperatorIndex
     * @param cValue
     * @param cUnit
     * @param userID
     * @param rTime
     * @param rTimeUnit
     * @param cTime
     * @param cTimeUnit
     */
    public Rule(SimpleRuleType ruleType, String title, String station, String phenomenon,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int rOperatorIndex, String rValue, String rUnit,int cOperatorIndex, String cValue, String cUnit, int userID,
            String rTime, String rTimeUnit, String cTime, String cTimeUnit) {

        this.ruleType = ruleType;
        this.title = title;
        this.station = station;
        this.phenomenon = phenomenon;
        this.notificationType = notificationType;
        this.description = description;
        this.publish = publish;
        this.enterEqualsExitCondition = enterEqualsExitCondition;
        this.rOperatorIndex = rOperatorIndex;
        this.rValue = rValue;
        this.rUnit = rUnit;
        this.cOperatorIndex = cOperatorIndex;
        this.cValue = cValue;
        this.cUnit = cUnit;
        this.userID = userID;
        this.rTime = rTime;
        this.rTimeUnit = rTimeUnit;
        this.cTime = cTime;
        this.cTimeUnit = cTimeUnit;
    }

    /**
     * BasicRule_5: Ausfall
     * 
     * @param ruleType
     * @param title
     * @param station
     * @param phenomenon
     * @param notificationType
     * @param description
     * @param publish
     * @param enterEqualsExitCondition
     * @param userID
     * @param rTime
     * @param rTimeUnit
     */
    public Rule(SimpleRuleType ruleType, String title, String station, String phenomenon, String notificationType, String description, 
            boolean publish, boolean enterEqualsExitCondition, int userID, String rTime, String rTimeUnit) {
        this.ruleType = ruleType;
        this.title = title;
        this.station = station;
        this.phenomenon = phenomenon;
        this.notificationType = notificationType;
        this.description = description;
        this.publish = publish;
        this.enterEqualsExitCondition = enterEqualsExitCondition;
        this.userID = userID;
        this.rTime = rTime;
        this.rTimeUnit = rTimeUnit;
    }

    /**
     * Gets the rule type.
     * 
     * @return the rule type
     */
    public SimpleRuleType getRuleType() {
        return this.ruleType;
    }

    /**
     * Sets the rule type.
     * 
     * @param ruleType
     *            the new rule type
     */
    public void setRuleType(SimpleRuleType ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the station.
     * 
     * @return the station
     */
    public String getStation() {
        return this.station;
    }

    /**
     * Sets the station.
     * 
     * @param station
     *            the new station
     */
    public void setStation(String station) {
        this.station = station;
    }

    /**
     * Gets the phenomenon.
     * 
     * @return the phenomenon
     */
    public String getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * Sets the phenomenon.
     * 
     * @param phenomenon
     *            the new phenomenon
     */
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    /**
     * Gets the notification type.
     * 
     * @return the notification type
     */
    public String getNotificationType() {
        return this.notificationType;
    }

    /**
     * Sets the notification type.
     * 
     * @param notificationType
     *            the new notification type
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if is publish.
     * 
     * @return true, if is publish
     */
    public boolean isPublish() {
        return this.publish;
    }

    /**
     * Sets the publish.
     * 
     * @param publish
     *            the new publish
     */
    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    /**
     * Checks if is enter equals exit condition.
     * 
     * @return true, if is enter equals exit condition
     */
    public boolean isEnterEqualsExitCondition() {
        return this.enterEqualsExitCondition;
    }

    /**
     * Sets the enter equals exit condition.
     * 
     * @param enterEqualsExitCondition
     *            the new enter equals exit condition
     */
    public void setEnterEqualsExitCondition(boolean enterEqualsExitCondition) {
        this.enterEqualsExitCondition = enterEqualsExitCondition;
    }

    /**
     * Gets the r operator index.
     * 
     * @return the r operator index
     */
    public int getrOperatorIndex() {
        return this.rOperatorIndex;
    }

    /**
     * Sets the r operator index.
     * 
     * @param rOperatorIndex
     *            the new r operator index
     */
    public void setrOperatorIndex(int rOperatorIndex) {
        this.rOperatorIndex = rOperatorIndex;
    }

    /**
     * Gets the r value.
     * 
     * @return the r value
     */
    public String getrValue() {
        return this.rValue;
    }

    /**
     * Sets the r value.
     * 
     * @param rValue
     *            the new r value
     */
    public void setrValue(String rValue) {
        this.rValue = rValue;
    }

    /**
     * Gets the r unit.
     * 
     * @return the r unit
     */
    public String getrUnit() {
        return this.rUnit;
    }

    /**
     * Sets the r unit.
     * 
     * @param rUnit
     *            the new r unit
     */
    public void setrUnit(String rUnit) {
        this.rUnit = rUnit;
    }

    /**
     * Gets the c operator index.
     * 
     * @return the c operator index
     */
    public int getcOperatorIndex() {
        return this.cOperatorIndex;
    }

    /**
     * Sets the c operator index.
     * 
     * @param cOperatorIndex
     *            the new c operator index
     */
    public void setcOperatorIndex(int cOperatorIndex) {
        this.cOperatorIndex = cOperatorIndex;
    }

    /**
     * Gets the c value.
     * 
     * @return the c value
     */
    public String getcValue() {
        return this.cValue;
    }

    /**
     * Sets the c value.
     * 
     * @param cValue
     *            the new c value
     */
    public void setcValue(String cValue) {
        this.cValue = cValue;
    }

    /**
     * Gets the c unit.
     * 
     * @return the c unit
     */
    public String getcUnit() {
        return this.cUnit;
    }

    /**
     * Sets the c unit.
     * 
     * @param cUnit
     *            the new c unit
     */
    public void setcUnit(String cUnit) {
        this.cUnit = cUnit;
    }

    /**
     * @return userID
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
     * @return count
     */
    public String getCount() {
        return this.count;
    }

    /**
     * @param count
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return cCount
     */
    public String getcCount() {
        return this.cCount;
    }

    /**
     * @param cCount
     */
    public void setcCount(String cCount) {
        this.cCount = cCount;
    }

    /**
     * @return rTime
     */
    public String getrTime() {
        return this.rTime;
    }

    /**
     * @param rTime
     */
    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    /**
     * @return cTime
     */
    public String getcTime() {
        return this.cTime;
    }

    /**
     * @param cTime
     */
    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    /**
     * @return rTimeUnit
     */
    public String getrTimeUnit() {
        return this.rTimeUnit;
    }

    /**
     * @param rTimeUnit
     */
    public void setrTimeUnit(String rTimeUnit) {
        this.rTimeUnit = rTimeUnit;
    }

    /**
     * @return cTimeUnit
     */
    public String getcTimeUnit() {
        return this.cTimeUnit;
    }

    /**
     * @param cTimeUnit
     */
    public void setcTimeUnit(String cTimeUnit) {
        this.cTimeUnit = cTimeUnit;
    }
}
