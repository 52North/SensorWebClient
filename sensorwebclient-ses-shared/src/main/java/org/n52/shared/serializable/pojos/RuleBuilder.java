/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.serializable.pojos;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

public class RuleBuilder {

    private SimpleRuleType ruleType;
    private String title;
    private String notificationType;
    private String description;
    private boolean publish;
    private boolean enterEqualsExit;
    private int entryOperatorIndex;
    private int exitOperatorIndex;
    private String entryValue;
    private String entryUnit;
    private String exitValue;
    private String exitUnit;
    private int userId;
    private String entrycount;
    private String exitCount;
    private String entryTime;
    private String entryTimeUnit;
    private String exitTime;
    private String exitTimeUnit;
    private TimeseriesMetadata timeseriesMetadata;

    public static RuleBuilder aRule() {
        return new RuleBuilder();
    }

    public RuleBuilder setRuleType(SimpleRuleType ruleTyp) {
        this.ruleType = ruleTyp;
        return this;
    }


    public RuleBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public RuleBuilder setNotificationType(String notificationType) {
        this.notificationType = notificationType;
        return this;
        
    }

    public RuleBuilder setDescription(String description) {
        this.description = description;
        return this;
        
    }

    public RuleBuilder setPublish(boolean publish) {
        this.publish = publish;
        return this;
    }

    public RuleBuilder setEnterIsSameAsExitCondition(boolean condition) {
        this.enterEqualsExit = condition;
        return this;
    }

    public RuleBuilder setEntryOperatorIndex(int entryOperatorIndex) {
        this.entryOperatorIndex = entryOperatorIndex;
        return this;
    }
    
    public RuleBuilder setExitOperatorIndex(int exitOperatorIndex) {
        this.exitOperatorIndex = exitOperatorIndex;
        return this;
    }

    public RuleBuilder setEntryValue(String entryValue) {
        this.entryValue = entryValue;
        return this;
    }

    public RuleBuilder setEntryUnit(String entryUnit) {
        this.entryUnit = entryUnit;
        return this;
    }

    public RuleBuilder setExitValue(String exitValue) {
        this.exitValue = exitValue;
        return this;
    }

    public RuleBuilder setExitUnit(String exitUnit) {
        this.exitUnit = exitUnit;
        return this;
    }
    
    public RuleBuilder setEntryCount(String entryCount) {
        this.entrycount = entryCount;
        return this;
    }
    
    public RuleBuilder setExitCount(String exitCount) {
        this.exitCount = exitCount;
        return this;
    }

    public RuleBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }
    
    public RuleBuilder setEntryTime(String entryTime) {
        this.entryTime = entryTime;
        return this;
    }
    
    public RuleBuilder setEntryTimeUnit(String entryTimeUnit) {
        this.entryTimeUnit = entryTimeUnit;
        return this;
    }
    
    public RuleBuilder setExitTime(String exitTime) {
        this.exitTime = exitTime;
        return this;
    }
  
    public RuleBuilder setExitTimeUnit(String exitTimeUnit) {
        this.exitTimeUnit = exitTimeUnit;
        return this;
    }

    public RuleBuilder setTimeseriesMetadata(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
        return this;
    }

    public Rule build() {
        Rule rule = new Rule();
        rule.setExitCount(exitCount);
        rule.setExitOperatorIndex(exitOperatorIndex);
        rule.setCount(entrycount);
        rule.setExitTime(exitTime);
        rule.setExitTimeUnit(exitTimeUnit);
        rule.setExitUnit(exitUnit);
        rule.setExitValue(exitValue);
        rule.setDescription(description);
        rule.setEnterEqualsExitCondition(enterEqualsExit);
        rule.setNotificationType(notificationType);
        rule.setPublish(publish);
        rule.setTimeseriesMetadata(timeseriesMetadata);
        rule.setEntryOperatorIndex(entryOperatorIndex);
        rule.setrTime(entryTime);
        rule.setrTimeUnit(entryTimeUnit);
        rule.setRuleType(ruleType);
        rule.setEntryUnit(entryUnit);
        rule.setEntryValue(entryValue);
        rule.setTitle(title);
        rule.setUserID(userId);
        return rule;
    }

}
