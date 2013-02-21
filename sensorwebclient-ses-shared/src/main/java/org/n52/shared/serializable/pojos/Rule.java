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

import static org.n52.shared.util.MathSymbolUtil.getInverse;
import static org.n52.shared.util.MathSymbolUtil.getSymbolForIndex;

import java.io.Serializable;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.shared.util.MathSymbolUtil;

public class Rule implements Serializable {
    
    private static final long serialVersionUID = -1913430711568432313L;

    public static final int EQUAL_TO = 0;

    public static final int NOT_EQUAL_TO = 1;
    
    public static final int GREATER_THAN = 2;
    
    public static final int LESS_THAN = 3;
    
    public static final int GREATER_THAN_OR_EQUAL_TO = 4;
    
    public static final int LESS_THAN_OR_EQUAL_TO = 5;
    
    private SimpleRuleType ruleType;

    private String uuid;

    private String title;

    private TimeseriesMetadata timeseriesMetadata;
    
    private String notificationType;

    private String description;

    private boolean publish;

    private boolean enterEqualsExitCondition;

    private int entryOperatorIndex;

    private String entryValue;

    private String entryUnit;

    private int exitOperatorIndex;

    private String exitValue;

    private String exitUnit;

    private int userID;

    private String entryCount;

    private String exitCount;

    private String entryTime;
    private String exitTime;
    private String entryTimeUnit;
    private String exitTimeUnit;
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rule: [ ");
        sb.append("uuid: ").append(uuid).append(", ");
        sb.append("title: ").append(title).append(", ");
        sb.append("notificationType: ").append(notificationType).append(", ");
        sb.append("entryFilter: ");
        sb.append(getFilterValueString(entryOperatorIndex, entryValue, entryUnit));
        sb.append("exitFilter: ");
        sb.append(getFilterValueString(exitOperatorIndex, exitValue, exitUnit));
        sb.append("type: ").append(ruleType.name()).append(", ");
        return sb.append(" ]").toString();
    }

    private String getFilterValueString(int operator, String value, String unit) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSymbolForIndex(operator)).append(" ");
        sb.append(value).append(" (").append(unit).append("), ");
        return sb.toString();
    }

    public Rule() {
        //
    }
    
    /**
     * Instantiates a new rule.
     * @deprecated use {@link RuleBuilder}
     */
    @Deprecated
    public Rule(SimpleRuleType ruleType, String title, TimeseriesMetadata timeseriesMetadata,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int entryOperatorIndex, String entryValue, String entryUnit, int exitOperatorIndex, String exitValue, String exitUnit, int userID) {
        this(ruleType, title, timeseriesMetadata, notificationType, description, publish, enterEqualsExitCondition, userID);
        this.entryOperatorIndex = entryOperatorIndex;
        this.entryValue = entryValue;
        this.entryUnit = entryUnit;
        this.exitOperatorIndex = exitOperatorIndex;
        this.exitValue = exitValue;
        this.exitUnit = exitUnit;
    }

    /**
     * BasicRule_1: Tendenz_Anzahl
     * @deprecated use {@link RuleBuilder}
     */
    @Deprecated
    public Rule(SimpleRuleType ruleType, String title, TimeseriesMetadata timeseriesMetadata,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int entryOperatorIndex, String entryValue, String entryUnit,int exitOperatorIndex, String exitValue, String exitUnit, int userID, String count, String exitCount) {
        this(ruleType, title, timeseriesMetadata, notificationType, description, publish, enterEqualsExitCondition, userID);
        this.entryOperatorIndex = entryOperatorIndex;
        this.entryValue = entryValue;
        this.entryUnit = entryUnit;
        this.exitOperatorIndex = exitOperatorIndex;
        this.exitValue = exitValue;
        this.exitUnit = exitUnit;
        this.userID = userID;
        this.entryCount = count;
        
        // XXX refactor: above is redundant
        
        this.exitCount = exitCount;
    }

    /**
     * BasicRule_2: Tendenz_Zeit
     * @deprecated use {@link RuleBuilder}
     */
    @Deprecated
    public Rule(SimpleRuleType ruleType, String title, TimeseriesMetadata timeseriesMetadata,
            String notificationType, String description, boolean publish, boolean enterEqualsExitCondition,
            int entryOperatorIndex, String entryValue, String entryUnit,int exitOperatorIndex, String exitValue, String exitUnit, int userID,
            String entryTime, String entryTimeUnit, String exitTime, String exitTimeUnit) {
        this(ruleType, title, timeseriesMetadata, notificationType, description, publish, enterEqualsExitCondition, userID);
        
        this.entryOperatorIndex = entryOperatorIndex;
        this.entryValue = entryValue;
        this.entryUnit = entryUnit;
        this.exitOperatorIndex = exitOperatorIndex;
        this.exitValue = exitValue;
        this.exitUnit = exitUnit;
        
        // XXX refactor: above is redundant

        this.entryTime = entryTime;
        this.entryTimeUnit = entryTimeUnit;
        this.exitTime = exitTime;
        this.exitTimeUnit = exitTimeUnit;
    }

    /**
     * BasicRule_5: Ausfall
     * @deprecated use {@link RuleBuilder}
     */
    @Deprecated
    public Rule(SimpleRuleType ruleType, String title, TimeseriesMetadata timeseriesMetadata, String notificationType, String description, 
            boolean publish, boolean enterEqualsExitCondition, int userID, String entryTime, String entryTimeUnit) {
        this(ruleType, title, timeseriesMetadata, notificationType, description, publish, enterEqualsExitCondition, userID);
        this.entryTime = entryTime;
        this.entryTimeUnit = entryTimeUnit;
    }
    
    private Rule(SimpleRuleType ruleType, String title, TimeseriesMetadata timeseriesMetadata, String notificationType, String description, 
            boolean publish, boolean enterEqualsExitCondition, int userID) {
        this.ruleType = ruleType;
        this.title = title;
        this.timeseriesMetadata = timeseriesMetadata;
        this.notificationType = notificationType;
        this.description = description;
        this.publish = publish;
        this.enterEqualsExitCondition = enterEqualsExitCondition;
        this.userID = userID;
    }

    public SimpleRuleType getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(SimpleRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public TimeseriesMetadata getTimeseriesMetadata() {
        return timeseriesMetadata;
    }

    public void setTimeseriesMetadata(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublish() {
        return this.publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public boolean isEnterEqualsExitCondition() {
        return this.enterEqualsExitCondition;
    }

    public void setEnterEqualsExitCondition(boolean enterEqualsExitCondition) {
        this.enterEqualsExitCondition = enterEqualsExitCondition;
    }

    public int getEntryOperatorIndex() {
        return this.entryOperatorIndex;
    }

    public void setEntryOperatorIndex(int entryOperatorIndex) {
        this.entryOperatorIndex = entryOperatorIndex;
    }

    public String getEntryValue() {
        return this.entryValue;
    }

    public void setEntryValue(String entryValue) {
        this.entryValue = entryValue;
    }

    public String getEntryUnit() {
        return this.entryUnit;
    }

    public void setEntryUnit(String entryUnit) {
        this.entryUnit = entryUnit;
    }

    public int getExitOperatorIndex() {
        return this.exitOperatorIndex;
    }

    public void setExitOperatorIndex(int exitOperatorIndex) {
        this.exitOperatorIndex = exitOperatorIndex;
    }

    public String getExitValue() {
        return this.exitValue;
    }

    public void setExitValue(String exitValue) {
        this.exitValue = exitValue;
    }

    public String getExitUnit() {
        return this.exitUnit;
    }

    public void setExitUnit(String exitUnit) {
        this.exitUnit = exitUnit;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEntryCount() {
        return this.entryCount;
    }

    public void setCount(String entryCount) {
        this.entryCount = entryCount;
    }

    public String getExitCount() {
        return this.exitCount;
    }

    public void setExitCount(String exitCount) {
        this.exitCount = exitCount;
    }

    public String getEntryTime() {
        return this.entryTime;
    }

    public void setrTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return this.exitTime;
    }

    public void setExitTime(String ExitTime) {
        this.exitTime = ExitTime;
    }

    public String getEntryTimeUnit() {
        return this.entryTimeUnit;
    }

    public void setrTimeUnit(String entryTimeUnit) {
        this.entryTimeUnit = entryTimeUnit;
    }

    public String getExitTimeUnit() {
        return this.exitTimeUnit;
    }

    public void setExitTimeUnit(String exitTimeUnit) {
        this.exitTimeUnit = exitTimeUnit;
    }
    
    public boolean determineEqualEntryExitCondition() {
        return entryOperatorIndex == getInverse(exitOperatorIndex) 
                && entryValue.equals(exitValue);
    }

}
