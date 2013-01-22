package org.n52.shared.serializable.pojos;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

public class RuleBuilder {

    private SimpleRuleType ruleType;
    private String title;
    private String station;
    private String phenomenon;
    private String notificationType;
    private String description;
    private boolean publish;
    private boolean enterEqualsExit;
    private int rOperatorIndex;
    private int cOperatorIndex;
    private String rValue;
    private String rUnit;
    private String cValue;
    private String cUnit;
    private int cookieAsInt;
    private String count;
    private String cCount;
    private String rTime;
    private String rTimeUnit;
    private String cTime;
    private String cTimeUnit;

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

    public RuleBuilder setStation(String station) {
        this.station = station;
        return this;
    }

    public RuleBuilder setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
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

    public RuleBuilder setCondition(boolean condition) {
        this.enterEqualsExit = condition;
        return this;
    }

    public RuleBuilder setROperatorIndex(int rOperatorIndex) {
        this.rOperatorIndex = rOperatorIndex;
        return this;
    }
    
    public RuleBuilder setCOperatorIndex(int cOperatorIndex) {
        this.cOperatorIndex = cOperatorIndex;
        return this;
    }

    public RuleBuilder setRValue(String rValue) {
        this.rValue = rValue;
        return this;
    }

    public RuleBuilder setRUnit(String rUnit) {
        this.rUnit = rUnit;
        return this;
    }

    public RuleBuilder setCValue(String cValue) {
        this.cValue = cValue;
        return this;
    }

    public RuleBuilder setCUnit(String cUnit) {
        this.cUnit = cUnit;
        return this;
    }
    
    public RuleBuilder setCCount(String count) {
        this.count = count;
        return this;
    }
    
    public RuleBuilder setCount(String cCount) {
        this.cCount = cCount;
        return this;
    }

    public RuleBuilder setCookie(int cookieAsInt) {
        this.cookieAsInt = cookieAsInt;
        return this;
    }
    
    public RuleBuilder setRTime(String rTime) {
        this.rTime = rTime;
        return this;
    }
    
    public RuleBuilder setRTimeUnit(String rtimeUnit) {
        this.rTimeUnit = rtimeUnit;
        return this;
    }
    
    public RuleBuilder setCTime(String cTime) {
        this.cTime = cTime;
        return this;
    }
  
    public RuleBuilder setCTimeUnit(String ctimeUnit) {
        this.cTimeUnit = ctimeUnit;
        return this;
    }

    public Rule build() {
        Rule rule = new Rule();
        rule.setExitCount(cCount);
        rule.setExitOperatorIndex(cOperatorIndex);
        rule.setCount(count);
        rule.setExitTime(cTime);
        rule.setExitTimeUnit(cTimeUnit);
        rule.setExitUnit(cUnit);
        rule.setExitValue(cValue);
        rule.setDescription(description);
        rule.setEnterEqualsExitCondition(enterEqualsExit);
        rule.setNotificationType(notificationType);
        rule.setPhenomenon(phenomenon);
        rule.setPublish(publish);
        rule.setEntryOperatorIndex(rOperatorIndex);
        rule.setrTime(rTime);
        rule.setrTimeUnit(rTimeUnit);
        rule.setRuleType(ruleType);
        rule.setEntryUnit(rUnit);
        rule.setEntryValue(rValue);
        rule.setStation(station);
        rule.setTitle(title);
        rule.setUserID(cookieAsInt);
        return rule;
    }
    
    
}
