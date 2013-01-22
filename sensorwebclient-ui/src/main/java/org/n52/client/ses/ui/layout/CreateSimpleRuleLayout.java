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
package org.n52.client.ses.ui.layout;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SUM_OVER_TIME;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.TENDENCY_OVER_COUNT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.TENDENCY_OVER_TIME;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.SesRequestManager;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.CreateSimpleRuleEvent;
import org.n52.client.ses.event.GetPhenomenaEvent;
import org.n52.client.ses.ui.Layout;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.RuleBuilder;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The Class CreateSimpleRuleLayout.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class CreateSimpleRuleLayout extends Layout {

    /** The selected type. */
    private SimpleRuleType selectedType;

    private int selectItemWidth = 250;
    
    private int selectItemWidth2 = 200;
    
    private int ruleItemWidth = 60;
    
    // further elements
    /** The title item. */
    private TextItem nameItem;

    /** The description item. */
    private TextAreaItem descriptionItem;

    /** The publish radio group. */
    private RadioGroupItem publishRadioGroup;

    /** The condition radio group. */
    private RadioGroupItem conditionRadioGroup;

    /** The create or edit button */
    private ButtonItem createButtonItem;
    
    private ButtonItem cancelButton;

    // values
    /** The title. */
    private String name;

    /** The station. */
    private String station;

    /** The phenomenon. */
    private String phenomenon;

    /** The rule type. */
    private SimpleRuleType ruleTyp;

    /** The notification type. */
    private String notificationType;

    /** The description. */
    private String description;

    /** The publish. */
    private boolean publish;

    /** The condition. */
    private boolean condition;

    private boolean edit = false;
    
    private String oldRuleName = "";
    
    // first three combo boxes
    private ComboBoxItem sensorItem;
    private SelectItem phenomenonItem;
    private SelectItem ruleTypeItem;
    
    // dynamic rule elements
    private TextItem ruleValueItem;
    private TextItem countItem;
    private TextItem timeItem;
    private TextItem ruleValueConditionItem;
    private TextItem countCondItem;
    private TextItem conditionTimeItem;
    
    private SelectItem operatorRuleItem;
    private SelectItem operatorConditionItem;
    private SelectItem ruleValueUnitItem;
    private SelectItem ruleValueUnitConditionItem;
    private SelectItem timeUnitItem;
    private SelectItem conditionTimeUnitItem;
    
    private DynamicForm ruleElementsForm;
    private DynamicForm ruleElementsConditionForm;
    private DynamicForm ruleForm;
    
    // hash maps for the selectItems
    private LinkedHashMap<String, String> sensorsHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> phenomenaHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> ruleTypesHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> operatorHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> unitHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> timeUnitHashMap = new LinkedHashMap<String, String>();
    
    /**
     * Instantiates a new creates the simple rule layout.
     */
    public CreateSimpleRuleLayout() {
        super(i18n.createBasicRule());

        DataSource dataSource = new DataSource();

        this.form.setDataSource(dataSource);

        // Title of the rule
        this.nameItem = new TextItem();
        this.nameItem.setName("title");
        this.nameItem.setTitle(i18n.name());
        this.nameItem.setRequired(true);
        this.nameItem.setLength(70);
        this.nameItem.setWidth(this.selectItemWidth);
        this.nameItem.setKeyPressFilter("[0-9a-zA-Z_]");
        this.nameItem.setHint(i18n.possibleChars() + " [0-9 a-z A-Z _]");
        this.nameItem.setShowHintInField(true);

        this.form.setFields(this.headerItem, this.nameItem);

        setHashMapData();

        // initializeComboBoxes
        initComboBoxes();

        // DescriptionItem
        this.descriptionItem = new TextAreaItem();
        this.descriptionItem.setName("description");
        this.descriptionItem.setTitle(i18n.description());
        this.descriptionItem.setRequired(true);
        this.descriptionItem.setHeight(100);
        this.descriptionItem.setWidth(250);
        this.descriptionItem.setHint(i18n.possibleChars() + " [0-9 a-z A-Z _ -]");
        this.descriptionItem.setShowHintInField(true);
        this.descriptionItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                String key = event.getKeyName();
                String [] filter = {"["};
                for (int i = 0; i < filter.length; i++) {
                    if (key.equals(filter[i])) {
                        event.cancel();
                        break;
                    }
                }
            }
        });

        // form for description item
        this.form2 = new DynamicForm();
        this.form2.setUseAllDataSourceFields(true);
        this.form2.setFields(this.descriptionItem);

        // ====================
        // radioButtons
        this.publishRadioGroup = new RadioGroupItem("publish", i18n.publish());
        this.publishRadioGroup.setValueMap(i18n.yes(), i18n.no());
        this.publishRadioGroup.setDefaultValue(i18n.yes());

        this.conditionRadioGroup = new RadioGroupItem("condition", i18n.enterExitCondition());
        this.conditionRadioGroup.setValueMap(i18n.yes(), i18n.no());
        this.conditionRadioGroup.setDefaultValue(i18n.yes());
        this.conditionRadioGroup.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                if (event.getValue().toString().equals(i18n.no())) {
                    setRuleConditionFields();
                } else {
                    CreateSimpleRuleLayout.this.ruleElementsConditionForm.setFields();
                }
            }
        });

        // form for radioButtons
        DynamicForm radioForm = new DynamicForm();
        DynamicForm radioForm2 = new DynamicForm();
        
        radioForm.setItems(this.conditionRadioGroup);
        radioForm2.setItems(this.publishRadioGroup);

        // form3
        DynamicForm form3 = new DynamicForm();
        form3.setUseAllDataSourceFields(true);

        this.createButtonItem = new ButtonItem();
        this.createButtonItem.setTitle("Create");
        this.createButtonItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (CreateSimpleRuleLayout.this.form.validate(false)
                        && CreateSimpleRuleLayout.this.form2.validate(false)) {
                    if (inputsValid()) {
                        createBasicRule();
                    }
                }
            }
        });
        
        this.cancelButton = new ButtonItem();
        this.cancelButton.setTitle(i18n.cancel());
        this.cancelButton.setVisible(false);
        this.cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String role = Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE);
                if (role.equals("ADMIN")) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
                } else {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
                }
            }
        });
        form3.setFields(this.createButtonItem, this.cancelButton);
        
        this.ruleForm = new DynamicForm();
        this.ruleForm.setWidth("50%");
        this.ruleForm.setNumCols(6);
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setWidth(100);
        this.ruleForm.setFields(spacerItem, this.sensorItem, this.phenomenonItem, this.ruleTypeItem);
        
        // Dynamic Forms
        this.ruleElementsForm = new DynamicForm();
        this.ruleElementsForm.setFixedColWidths(true);
        this.ruleElementsForm.setNumCols(5);
        this.ruleElementsForm.setWidth("30%");
        
        this.ruleElementsConditionForm = new DynamicForm();
        this.ruleElementsConditionForm.setFixedColWidths(true);
        this.ruleElementsConditionForm.setNumCols(5);
        this.ruleElementsConditionForm.setWidth("30%");
        
        VLayout v = new VLayout();
        v.addMember(this.ruleElementsForm);
        v.addMember(this.ruleElementsConditionForm);
        
        HLayout ruleLayout = new HLayout();
        ruleLayout.setHeight(40);
        ruleLayout.setWidth100();
        ruleLayout.addMember(this.ruleForm);
        ruleLayout.addMember(v);

        // add to MainLayout
        addMember(this.spacer);
        addMember(this.form);
        addMember(this.spacer);
        addMember(ruleLayout);
        addMember(this.spacer);
        addMember(radioForm);
        addMember(this.spacer);
        addMember(this.form2);
        addMember(this.spacer);
        addMember(radioForm2);
        addMember(this.spacer);
        addMember(form3);
    }

    /**
     * Check wether all input fields are filled
     * Depending on rule type, different fields must be checked
     */
    private boolean inputsValid() {
        // check ruleName
        String ruleName = this.nameItem.getValueAsString();
        if (Character.isDigit(ruleName.charAt(0))) {
            // if rulename starts with number, inform user
            SC.say(i18n.ruleNameStartsWithDigit());
            return false;
        }
        
        // check station
        String station = this.sensorItem.getValueAsString();
        if (station == null || station.equals("")) {
            SC.say(i18n.chooseStation());
            return false;
        }
        // check phenomenon
        String phenomenon = this.phenomenonItem.getValueAsString();
        if (phenomenon == null || phenomenon.equals("")) {
            SC.say(i18n.choosePhenomenon());
            return false;
        }
        // check ruleType
        if (this.selectedType == SimpleRuleType.NONE) {
            SC.say(i18n.chooseRuleType());
            return false;
        }
        // check trend over time
        if (this.selectedType == SimpleRuleType.TENDENCY_OVER_TIME) {
            // check time and unit
            if (this.timeItem.getValue() == null || this.timeUnitItem.getValue() == null) {
                SC.say(i18n.indicateTimeUnit());
                return false;
            }
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition time and value
                if (this.conditionTimeItem.getValue() == null || this.conditionTimeUnitItem.getValue() == null) {
                    SC.say(i18n.indicateConditionTimeUnit());
                    return false;
                }
                // check condition value and unit
                if (this.ruleValueConditionItem.getValue() == null || this.ruleValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check trend over count
        if (this.selectedType == SimpleRuleType.TENDENCY_OVER_COUNT) {
            // check count
            if (this.countItem.getValue() == null) {
                SC.say(i18n.indicateCount());
                return false;
            }
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition count
                if (this.countCondItem.getValue() == null) {
                    SC.say(i18n.indicateConditionCount());
                    return false;
                }
                // check condition value and unit
                if (this.ruleValueConditionItem.getValue() == null || this.ruleValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check over- undershoot
        if (this.selectedType == SimpleRuleType.OVER_UNDERSHOOT) {
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition value and unit
                if (this.ruleValueConditionItem.getValue() == null || this.ruleValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check sum over time
        if (this.selectedType == SimpleRuleType.SUM_OVER_TIME) {
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check count
            if (this.countItem.getValue() == null) {
                SC.say(i18n.indicateCount());
                return false;
            }
        }
        // sensor failure
        if (this.selectedType == SimpleRuleType.SENSOR_LOSS) {
            // check time and unit
            if (this.timeItem.getValue() == null || this.timeUnitItem.getValue() == null) {
                SC.say(i18n.indicateTimeUnit());
                return false;
            }
        }
        return true;
    }

    /**
     * Creates the basic rule.
     */
    private void createBasicRule() {
        this.name = (String) this.nameItem.getValue();
        this.station = this.sensorItem.getValueAsString();
        this.phenomenon = this.phenomenonItem.getValueAsString();

        this.ruleTyp = this.selectedType;

        this.description = (String) this.descriptionItem.getValue();

        this.publish = false;
        this.condition = false;

        if (this.publishRadioGroup.getValue().toString().equals(i18n.yes())) {
            this.publish = true;
        }

        if (this.ruleTyp == SimpleRuleType.OVER_UNDERSHOOT) {
            createOverUnderShootRule();
        } else if (this.ruleTyp == SimpleRuleType.TENDENCY_OVER_COUNT) {
            createTendenzAnzahlRule();
        } else if (this.ruleTyp == SimpleRuleType.TENDENCY_OVER_TIME) {
            createTendenzZeitRule();
        } else if (this.ruleTyp == SimpleRuleType.SUM_OVER_TIME) {
            createSummeZeitRule();
        } else if (this.ruleTyp == SimpleRuleType.SENSOR_LOSS) {
            createAusfallRule();
        }
    }

    /**
     * Sensor failure
     */
    private void createAusfallRule() {
        String rTime = this.timeItem.getValueAsString();
        String rTimeUnit = this.timeUnitItem.getValueAsString();
        
        int cookieAsInt = Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
                        .setStation(station)
                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setCondition(condition)
                        .setCookie(cookieAsInt)
                        .setRTime(rTime)
                        .setRTimeUnit(rTimeUnit)
                        .build();
        
        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
        
    }

    /**
     * Sum over Time
     */
    private void createSummeZeitRule() {
        int operatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int operatorIndexCond = 0;
        
        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();
        String cValue;
        String cUnit;
        
        String rCount = this.countItem.getValueAsString();
        
        if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorConditionItem.getValueAsString());
            cValue = this.ruleValueConditionItem.getValueAsString();
            cUnit = this.ruleValueUnitConditionItem.getValueAsString();
            this.condition = false;

        } else {
            operatorIndexCond = createCounterOperator(operatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.condition = true;
        }
        Rule rule =
                new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
                        this.description, this.publish, this.condition, operatorIndex, rValue, rUnit,
                        operatorIndexCond, cValue, cUnit, Integer.parseInt(Cookies
                                .getCookie(SesRequestManager.COOKIE_USER_ID)), rCount, null, null, null);

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
        
    }

    /**
     * Trend over Time
     */
    private void createTendenzZeitRule() {
        int operatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int operatorIndexCond = 0;

        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();
        String cValue;
        String cUnit;

        String rTime = this.timeItem.getValueAsString();
        String rTimeUnit = this.timeUnitItem.getValueAsString();
        String cTime;
        String cTimeUnit;

        if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorConditionItem.getValueAsString());
            
            cValue = this.ruleValueConditionItem.getValueAsString();
            cUnit = this.ruleValueUnitConditionItem.getValueAsString();
            this.condition = false;
            cTime = this.conditionTimeItem.getValueAsString();
            cTimeUnit = this.conditionTimeUnitItem.getValueAsString();

        } else {
            operatorIndexCond = createCounterOperator(operatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.condition = true;
            cTime = rTime;
            cTimeUnit = rTimeUnit;
        }
        Rule rule =
                new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
                        this.description, this.publish, this.condition, operatorIndex, rValue, rUnit,
                        operatorIndexCond, cValue, cUnit, Integer.parseInt(Cookies
                                .getCookie(SesRequestManager.COOKIE_USER_ID)), rTime, rTimeUnit, cTime, cTimeUnit);

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
    }

    /**
     * Trend over Count
     */
    private void createTendenzAnzahlRule() {
        int rOperatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int operatorIndexCond = 0;

        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();
        String cValue;
        String cUnit;

        String countValue = this.countItem.getValueAsString();
        String countCondValue;

        if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorConditionItem.getValueAsString());
            
            cValue = this.ruleValueConditionItem.getValueAsString();
            cUnit = this.ruleValueUnitConditionItem.getValueAsString();
            this.condition = false;
            countCondValue = this.countCondItem.getValueAsString();

        } else {
            operatorIndexCond = createCounterOperator(rOperatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.condition = true;
            countCondValue = countValue;
        }

        int cookieAsInt = Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
                        .setStation(station)
                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setCondition(condition)
                        .setROperatorIndex(rOperatorIndex)
                        .setRValue(rValue)
                        .setRUnit(rUnit)
                        .setCOperatorIndex(operatorIndexCond)
                        .setCValue(cValue)
                        .setCUnit(cUnit)
                        .setCookie(cookieAsInt)
                        .setCount(countValue)
                        .setCCount(countCondValue)
                        .build();
        
        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));

    }

    /**
     * Creates the over under shoot rule.
     */
    private void createOverUnderShootRule() {

        int operatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int cOperatorIndex = 0;
        
        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();

        String cValue;
        String cUnit;

        if (this.conditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            cOperatorIndex = getOperatorIndex(this.operatorConditionItem.getValueAsString());
            
            cValue = this.ruleValueConditionItem.getValueAsString();
            cUnit = this.ruleValueUnitConditionItem.getValueAsString();
            this.condition = false;
        } else {
            cOperatorIndex = createCounterOperator(operatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.condition = true;
        }

        int cookieAsInt = Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
                        .setStation(station)
                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setCondition(condition)
                        .setROperatorIndex(operatorIndex)
                        .setRValue(rValue)
                        .setRUnit(rUnit)
                        .setCOperatorIndex(cOperatorIndex)
                        .setCValue(cValue)
                        .setCUnit(cUnit)
                        .setCookie(cookieAsInt)
                        .build();
        
//        Rule rule =
//                new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
//                        this.description, this.publish, this.condition, operatorIndex, rValue, rUnit,
//                        cOperatorIndex, cValue, cUnit, cookieAsInt);

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
    }

    /**
     * @param operatorIndex
     * @return
     */
    private int createCounterOperator(int operatorIndex) {
        switch (operatorIndex) {
        case Rule.EQUAL_TO:
            return Rule.NOT_EQUAL_TO;
        case Rule.NOT_EQUAL_TO:
            return Rule.EQUAL_TO;
        case Rule.GREATER_THAN:
            return Rule.LESS_THAN_OR_EQUAL_TO;
        case Rule.LESS_THAN:
            return Rule.GREATER_THAN_OR_EQUAL_TO;
        case Rule.GREATER_THAN_OR_EQUAL_TO:
            return Rule.LESS_THAN;
        case Rule.LESS_THAN_OR_EQUAL_TO:
            return Rule.GREATER_THAN;
        default:
            return 0;
        }
    }

    /**
     * Fill station listbob with stations
     * 
     * @param stations
     */
    public void setStationsToList(ArrayList<String> stations) {
        
        for (int i = 0; i < stations.size(); i++) {
           this.sensorsHashMap.put(stations.get(i), stations.get(i));
        }
        this.sensorItem.clearValue();
        this.sensorItem.setValueMap(this.sensorsHashMap);
    }

    /**
     * Sets the phenomenon to list.
     * 
     * @param phenomena
     */
    public void setPhenomenonToList(ArrayList<String> phenomena) {
        if (!this.phenomenaHashMap.isEmpty()) {
            this.phenomenaHashMap.clear();
        }
        for (int i = 0; i < phenomena.size(); i++) {
            this.phenomenaHashMap.put(phenomena.get(i), phenomena.get(i));
        }
        this.phenomenonItem.clearValue();
        this.phenomenonItem.setValueMap(this.phenomenaHashMap);
        if (phenomena.size() != 0) {
            this.phenomenonItem.setValue(phenomena.get(0));
        }
    }

    /**
     * Sets the rule fileds.
     */
    private void setRuleFileds() {

        // set conditionRadio to "yes"
        this.conditionRadioGroup.setValue("yes");
        // clear condition field
        this.ruleElementsConditionForm.setFields();

        // delete all elements from condition layout
        if (this.selectedType == null) {
            this.ruleElementsConditionForm.setFields();
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        } else if (this.selectedType == SimpleRuleType.OVER_UNDERSHOOT) {
            // add elements to form
            
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENCY_OVER_TIME) {
            // add elements to form
            
            setTimeItem();
            setTimeUnitItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.timeItem, this.timeUnitItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENCY_OVER_COUNT) {
            // add elements to form
            
            setCountItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        }else if (this.selectedType == SimpleRuleType.SUM_OVER_TIME) {
            // add elements to form
            
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            setCountItem();
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.hide();
            this.ruleElementsForm.redraw();
        }else if (this.selectedType == SimpleRuleType.SENSOR_LOSS) {
            // add elements to form
            
            setTimeItem();
            setTimeUnitItem();
            
            this.ruleElementsForm.setFields(this.timeItem, this.timeUnitItem);
            this.conditionRadioGroup.hide();
            this.ruleElementsForm.redraw();
        }
    }

    /**
     * Sets the rule condition fields. This is the second elements bar under the
     * regular bar. It is only visible if enter != exit condition is true
     * 
     * set if enter condition != exit condition
     */
    private void setRuleConditionFields() {

        if (this.selectedType == SimpleRuleType.OVER_UNDERSHOOT) {
            // add elements to layout
            setOperatorConditionItem();
            setRuleValueConditionItem();
            setRuleValueUnitConditionItem();
            
            this.ruleElementsConditionForm.setFields(this.operatorConditionItem, this.ruleValueConditionItem, this.ruleValueUnitConditionItem);
            this.ruleElementsConditionForm.redraw();

        } else if (this.selectedType == SimpleRuleType.SUM_OVER_TIME) {
            //
        } else if (this.selectedType == SimpleRuleType.TENDENCY_OVER_COUNT) {
            // add elements to layout
            
            setCountConditionItem();
            setOperatorConditionItem();
            setRuleValueConditionItem();
            setRuleValueUnitConditionItem();
            
            this.ruleElementsConditionForm.setFields(this.countCondItem, this.operatorConditionItem, this.ruleValueConditionItem, this.ruleValueUnitConditionItem);
            this.ruleElementsConditionForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENCY_OVER_TIME) {
            // add elements to layout
            
            setConditionTimeItem();
            setConditionTimeUnitItem();
            setOperatorConditionItem();
            setRuleValueConditionItem();
            setRuleValueUnitConditionItem();
            
            this.ruleElementsConditionForm.setFields(this.conditionTimeItem, this.conditionTimeUnitItem, this.operatorConditionItem, this.ruleValueConditionItem, this.ruleValueUnitConditionItem);
            this.ruleElementsConditionForm.redraw();
        }
    }

    /**
     * Clear all fields
     */
    public void clearFields() {
        this.edit = false;
        this.oldRuleName = "";
        // rename header
        this.headerItem.setDefaultValue(i18n.createBasicRule());
        // rename button
        this.createButtonItem.setTitle(i18n.create());
        
        // hide cancelButton
        this.cancelButton.setVisible(false);

        // hide publish Radio Group
        this.publishRadioGroup.show();

        // clear title field
        this.nameItem.clearValue();

        // set selectedType to nothing
        this.selectedType = SimpleRuleType.NONE;
        this.conditionRadioGroup.setValue(i18n.yes());

        // clear first three list boxes
        this.station = null;
        this.sensorItem.clearValue();
        this.phenomenonItem.clearValue();

        // clear description field
        this.descriptionItem.clearValue();

        // set radio buttons to "yes"
        this.publishRadioGroup.setValue(i18n.yes());

        if (this.ruleTypeItem != null) {
            this.ruleTypeItem.clearValue();
            this.ruleTypeItem.setValueMap(this.ruleTypesHashMap);
        }
        if (this.operatorRuleItem != null) {
            this.operatorRuleItem.setValueMap(this.operatorHashMap);
        }
        if (this.operatorConditionItem != null) {
            this.operatorConditionItem.setValueMap(this.operatorHashMap);
        }
        
        this.phenomenonItem.setDisabled(true);
        this.ruleTypeItem.setDisabled(true);
        
        // clear 
        this.ruleElementsForm.setFields();
        this.ruleElementsConditionForm.setFields();

    }

    /**
     * Init the first and always visible combo boxes of the simple rule.
     */
    private void initComboBoxes() {
        
        this.sensorItem = new ComboBoxItem("sensors", i18n.sensor());
        this.sensorItem.setWidth(this.selectItemWidth);
        this.sensorItem.setTitleOrientation(TitleOrientation.TOP);
        this.sensorItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String station = event.getValue().toString();
                phenomenonItem.setDisabled(false);
                ruleTypeItem.setDisabled(false);

                if ((station != null) && (!station.equals(""))) {
                    EventBus.getMainEventBus().fireEvent(new GetPhenomenaEvent(station));
                }
            }
        });
        
        this.phenomenonItem = new SelectItem("phenomena", i18n.phenomenon());
        this.phenomenonItem.setWidth(this.selectItemWidth2);
        this.phenomenonItem.setTitleOrientation(TitleOrientation.TOP);
        this.phenomenonItem.setDisabled(true);
        
        this.ruleTypeItem = new SelectItem("ruleType", i18n.ruleType());
        this.ruleTypeItem.setWidth(this.selectItemWidth2);
        this.ruleTypeItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleTypeItem.setDisabled(true);
        this.ruleTypeItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {

                String value = event.getValue().toString();

                if (value.equals(i18n.overUnderShoot())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.OVER_UNDERSHOOT;
                } else if (value.equals(i18n.trendOverTime())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.TENDENCY_OVER_TIME;
                } else if (value.equals(i18n.trendOverCount())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.TENDENCY_OVER_COUNT;
                } else if (value.equals(i18n.sumOverCountMeasurements())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.SUM_OVER_TIME;
                } else if (value.equals(i18n.sensorFailure())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.SENSOR_LOSS;
                } else {
                    CreateSimpleRuleLayout.this.selectedType = null;
                }
                setRuleFileds();
            }
        });
    }

    /**
     * This method fills the GUI with rule data. It is used to visualize
     * a rule which should be edited.
     * 
     * @param rule
     */
    public void setEditRule(Rule rule) {
        edit = true;
        cancelButton.setVisible(true);
        oldRuleName = rule.getTitle();
        // rename header
        headerItem.setDefaultValue(i18n.editBasicRule());
        // rename button
        createButtonItem.setTitle(i18n.saveChanges());
        
        // set publishrRadioGroup
        if (rule.isPublish()) {
            publishRadioGroup.setValue(i18n.yes());
        } else {
            publishRadioGroup.setValue(i18n.no());
        }
        
        nameItem.setValue(rule.getTitle());
        descriptionItem.setValue(rule.getDescription());
        phenomenon = rule.getPhenomenon();
        station = rule.getStation();
        sensorItem.setValue(station);
        
        // get Phenomena
        EventBus.getMainEventBus().fireEvent(new GetPhenomenaEvent(station));
        
        // enable fields
        phenomenonItem.setDisabled(false);
        ruleTypeItem.setDisabled(false);
        
        // ruleType
        selectedType = rule.getRuleType();
        if (selectedType.equals(TENDENCY_OVER_TIME)) {
            ruleTypeItem.setValue(i18n.trendOverTime());
        }
        if (selectedType.equals(TENDENCY_OVER_COUNT)) {
            ruleTypeItem.setValue(i18n.trendOverCount());
        }
        if (selectedType.equals(OVER_UNDERSHOOT)) {
            ruleTypeItem.setValue(i18n.overUnderShoot());
        }
        if (selectedType.equals(SUM_OVER_TIME)) {
            ruleTypeItem.setValue(i18n.sumOverCountMeasurements());
        }
        if (selectedType.equals(SENSOR_LOSS)) {
            ruleTypeItem.setValue(i18n.sensorFailure());
        }
        
        String ruleCondition = "";
        if (rule.getRuleOperatorIndex() == 0) {
            ruleCondition = "=";
        } else if (rule.getRuleOperatorIndex() == 1) {
            ruleCondition = "<>";
        } else if (rule.getRuleOperatorIndex() == 2) {
            ruleCondition = ">";
        } else if (rule.getRuleOperatorIndex() == 3) {
            ruleCondition = "&lt;";
        } else if (rule.getRuleOperatorIndex() == 4) {
            ruleCondition = ">=";
        } else if (rule.getRuleOperatorIndex() == 5) {
            ruleCondition = "<=";
        }
        
        String conditionOperator = "";
        if (rule.getConditionOperatorIndex() == 0) {
            conditionOperator = "=";
        } else if (rule.getConditionOperatorIndex() == 1) {
            conditionOperator = "<>";
        } else if (rule.getConditionOperatorIndex() == 2) {
            conditionOperator = ">";
        } else if (rule.getConditionOperatorIndex() == 3) {
            conditionOperator = "&lt;";
        } else if (rule.getConditionOperatorIndex() == 4) {
            conditionOperator = ">=";
        } else if (rule.getConditionOperatorIndex() == 5) {
            conditionOperator = "<=";
        }
        
        // set condition radio group
        if (rule.isEnterEqualsExitCondition()) {
            condition = true;
            conditionRadioGroup.setValue(i18n.yes());
        } else {
            condition = false;
            conditionRadioGroup.setValue(i18n.no());
            
            setRuleValueConditionItem();
            setRuleValueUnitConditionItem();
            
            ruleValueConditionItem.setValue(rule.getConditionValue());
            ruleValueUnitConditionItem.setValue(rule.getConditionUnit());
        }
        
        if (rule.getRuleType().equals(OVER_UNDERSHOOT)) {
            setRuleValueItem();
            setRuleValueUnitItem();
            setOperatorRuleItem();
            
            ruleValueItem.setValue(rule.getRuleValue());
            ruleValueUnitItem.setValue(rule.getRuleUnit());
            operatorRuleItem.setValue(ruleCondition);
            ruleElementsForm.setFields(operatorRuleItem, ruleValueItem, ruleValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorConditionItem();
                this.operatorConditionItem.setValue(conditionOperator);
                
                this.ruleElementsConditionForm.setFields(operatorConditionItem, ruleValueConditionItem, ruleValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(TENDENCY_OVER_TIME)) {
            setTimeItem();
            setTimeUnitItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            timeItem.setValue(rule.getRuleTime());
            timeUnitItem.setValue(rule.getrTimeUnit());
            operatorRuleItem.setValue(ruleCondition);
            ruleValueItem.setValue(rule.getRuleValue());
            ruleValueUnitItem.setValue(rule.getRuleUnit());
            
            ruleElementsForm.setFields(timeItem, timeUnitItem, operatorRuleItem, ruleValueItem, ruleValueUnitItem);
           
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorConditionItem();
                setConditionTimeItem();
                setConditionTimeUnitItem();
                
                operatorConditionItem.setValue(conditionOperator);
                conditionTimeItem.setValue(rule.getcTime());
                conditionTimeUnitItem.setValue(rule.getcTimeUnit());
                
                ruleElementsConditionForm.setFields(conditionTimeItem, conditionTimeUnitItem, operatorConditionItem, ruleValueConditionItem, ruleValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(TENDENCY_OVER_COUNT)) {
            setCountItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.countItem.setValue(rule.getCount());
            this.operatorRuleItem.setValue(ruleCondition);
            this.ruleValueItem.setValue(rule.getRuleValue());
            this.ruleValueUnitItem.setValue(rule.getRuleUnit());
            
            this.ruleElementsForm.setFields(countItem, operatorRuleItem, ruleValueItem, ruleValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorConditionItem();
                setCountConditionItem();
                
                operatorConditionItem.setValue(conditionOperator);
                countCondItem.setValue(rule.getCount());
                
                ruleElementsConditionForm.setFields(countCondItem, operatorConditionItem, ruleValueConditionItem, ruleValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(SUM_OVER_TIME)) {
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            setCountItem();
            
            operatorRuleItem.setValue(ruleCondition);
            ruleValueItem.setValue(rule.getRuleValue());
            ruleValueUnitItem.setValue(rule.getRuleUnit());
            countItem.setValue(rule.getRuleTime());
            
            ruleElementsForm.setFields(countItem, operatorRuleItem, ruleValueItem, ruleValueUnitItem);
        } else if (rule.getRuleType().equals(SENSOR_LOSS)) {
            setTimeItem();
            setTimeUnitItem();
            
            timeItem.setValue(rule.getRuleTime());
            timeUnitItem.setValue(rule.getrTimeUnit());
            
            ruleElementsForm.setFields(timeItem, timeUnitItem);
        }
    }

    /**
     * @return titleItem
     */
    public TextItem getTitleItem() {
        return this.nameItem;
    }
    
    /**
     * set ruletypes, operators and time units to lists
     */
    private void setHashMapData(){
        // set ruletypes
        this.ruleTypesHashMap.put(i18n.trendOverTime(), i18n.trendOverTime());
        this.ruleTypesHashMap.put(i18n.trendOverCount(), i18n.trendOverCount());
        this.ruleTypesHashMap.put(i18n.overUnderShoot(), i18n.overUnderShoot());
        this.ruleTypesHashMap.put(i18n.sumOverCountMeasurements(), i18n.sumOverCountMeasurements());
        this.ruleTypesHashMap.put(i18n.sensorFailure(), i18n.sensorFailure());

        // set operators
        this.operatorHashMap.put("=", "=");
        this.operatorHashMap.put("<>", "<>");
        this.operatorHashMap.put(">", ">");
        this.operatorHashMap.put("&lt;", "&lt;");
        this.operatorHashMap.put(">=", ">=");
        this.operatorHashMap.put("<=", "<=");
        
        // set time units
        this.timeUnitHashMap.put("S", "S");
        this.timeUnitHashMap.put("M", "M");
        this.timeUnitHashMap.put("H", "H");
    }

    private void setRuleValueItem() {
        this.ruleValueItem = new TextItem();
        this.ruleValueItem.setWidth(this.ruleItemWidth);
        this.ruleValueItem.setTitle(i18n.value());
        this.ruleValueItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueItem.setKeyPressFilter("[0-9]");
    }

    private void setRuleValueUnitItem() {
        this.ruleValueUnitItem = new SelectItem();
        this.ruleValueUnitItem.setWidth(this.ruleItemWidth);
        this.ruleValueUnitItem.setTitle(i18n.unit());
        this.ruleValueUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueUnitItem.setValueMap(this.unitHashMap);
        this.ruleValueUnitItem.setTextAlign(Alignment.CENTER);
        
        ArrayList<String> list = new ArrayList<String>(this.unitHashMap.values());
        if (list.size() != 0) {
            this.ruleValueUnitItem.setDefaultValue(list.get(0));
        }
    }

    private void setCountItem() {
        this.countItem = new TextItem();
        this.countItem.setWidth(this.ruleItemWidth);
        this.countItem.setTitle(i18n.count());
        this.countItem.setTitleOrientation(TitleOrientation.TOP);
        this.countItem.setKeyPressFilter("[0-9]");
    }

    private void setTimeItem() {
        this.timeItem = new TextItem();
        this.timeItem.setWidth(this.ruleItemWidth);
        this.timeItem.setTitle("<nobr>" + i18n.timeValue() + "</nobr>");
        this.timeItem.setTitleOrientation(TitleOrientation.TOP);
        this.timeItem.setKeyPressFilter("[0-9]");
    }

    private void setTimeUnitItem() {
        this.timeUnitItem = new SelectItem();
        this.timeUnitItem.setWidth(this.ruleItemWidth);
        this.timeUnitItem.setTitle(i18n.timeUnit());
        this.timeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.timeUnitItem.setTooltip("<nobr>" + i18n.unitsTime() + "</nobr>");
        this.timeUnitItem.setValueMap(this.timeUnitHashMap);
        this.timeUnitItem.setDefaultValue("H");
        this.timeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setRuleValueConditionItem() {
        this.ruleValueConditionItem = new TextItem();
        this.ruleValueConditionItem.setWidth(this.ruleItemWidth);
        this.ruleValueConditionItem.setTitle(i18n.value());
        this.ruleValueConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueConditionItem.setKeyPressFilter("[0-9]");
    }

    private void setRuleValueUnitConditionItem() {
        this.ruleValueUnitConditionItem = new SelectItem();
        this.ruleValueUnitConditionItem.setWidth(this.ruleItemWidth);
        this.ruleValueUnitConditionItem.setTitle(i18n.unit());
        this.ruleValueUnitConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueUnitConditionItem.setValueMap(this.unitHashMap);
        this.ruleValueUnitConditionItem.setTextAlign(Alignment.CENTER);
        
        ArrayList<String> list = new ArrayList<String>(this.unitHashMap.values());
        if (list.size() != 0) {
            this.ruleValueUnitConditionItem.setDefaultValue(list.get(0));
        }
    }

    private void setCountConditionItem() {
        this.countCondItem = new TextItem();
        this.countCondItem.setWidth(this.ruleItemWidth);
        this.countCondItem.setTitle(i18n.count());
        this.countCondItem.setTitleOrientation(TitleOrientation.TOP);
        this.countCondItem.setKeyPressFilter("[0-9]");
    }

    private void setConditionTimeItem() {
        this.conditionTimeItem = new TextItem();
        this.conditionTimeItem.setWidth(this.ruleItemWidth);
        this.conditionTimeItem.setTitle("<nobr>" + i18n.timeValue() + "</nobr>");
        this.conditionTimeItem.setTitleOrientation(TitleOrientation.TOP);
        this.conditionTimeItem.setKeyPressFilter("[0-9]");
    }

    private void setConditionTimeUnitItem() {
        this.conditionTimeUnitItem = new SelectItem();
        this.conditionTimeUnitItem.setWidth(this.ruleItemWidth);
        this.conditionTimeUnitItem.setTitle(i18n.timeUnit());
        this.conditionTimeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.conditionTimeUnitItem.setTooltip("<nobr>" + i18n.unitsTime() + "</nobr>");
        this.conditionTimeUnitItem.setValueMap(this.timeUnitHashMap);
        this.conditionTimeUnitItem.setDefaultValue("H");
        this.conditionTimeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setOperatorRuleItem() {
        this.operatorRuleItem = new SelectItem();
        this.operatorRuleItem.setWidth(this.ruleItemWidth);
        this.operatorRuleItem.setTitle(i18n.operator());
        this.operatorRuleItem.setTitleOrientation(TitleOrientation.TOP);
        this.operatorRuleItem.setValueMap(this.operatorHashMap);
        this.operatorRuleItem.setDefaultValue(">");
        this.operatorRuleItem.setTextAlign(Alignment.CENTER);
        this.operatorRuleItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                if (operatorConditionItem != null) {
                    operatorConditionItem.setValue(getInverseOperator((String)event.getValue()));
                }
            }
        });
    }

    private void setOperatorConditionItem() {
        this.operatorConditionItem = new SelectItem();
        this.operatorConditionItem.setWidth(this.ruleItemWidth);
        this.operatorConditionItem.setTitle(i18n.operator());
        this.operatorConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.operatorConditionItem.setValueMap(this.operatorHashMap);
        this.operatorConditionItem.setTextAlign(Alignment.CENTER);
        
        String defaultValue = "";
        if (this.operatorRuleItem.getValueAsString().equals("=")) {
            defaultValue = "<>";
        } else if (this.operatorRuleItem.getValueAsString().equals("<>")) {
            defaultValue = "=";
        } else if (this.operatorRuleItem.getValueAsString().equals(">")) {
            defaultValue = "<=";
        } else if (this.operatorRuleItem.getValueAsString().equals("&lt;")) {
            defaultValue = ">=";
        } else if (this.operatorRuleItem.getValueAsString().equals(">=")) {
            defaultValue = "<";
        } else if (this.operatorRuleItem.getValueAsString().equals("<=")) {
            defaultValue = ">";
        }
        this.operatorConditionItem.setDefaultValue(defaultValue);
    }
    
    /**
     * 
     * @param units
     */
    public void setUnit(ArrayList<String> units){
        for (int i = 0; i < units.size(); i++) {
            this.unitHashMap.put(units.get(i), units.get(i));
        }
    }
    
    private int getOperatorIndex(String operator){
        int operatorIndex = 0;
        if (operator.equals("=")) {
            operatorIndex = 0;
        } else if (operator.equals("<>")) {
            operatorIndex = 1;
        } else if (operator.equals(">")) {
            operatorIndex = 2;
        } else if (operator.equals("&lt;")) {
            operatorIndex = 3;
        } else if (operator.equals(">=")) {
            operatorIndex = 4;
        } else if (operator.equals("<=")) {
            operatorIndex = 5;
        }
        
        return operatorIndex;
    }
    
    private String getInverseOperator(String operator){
        String inverseOperator = "";
        if (operator.equals("=")) {
            inverseOperator = "<>";
        } else if (operator.equals("<>")) {
            inverseOperator = "=";
        } else if (operator.equals(">")) {
            inverseOperator = "<=";
        } else if (operator.equals("&lt;")) {
            inverseOperator = ">=";
        } else if (operator.equals(">=")) {
            inverseOperator = "&lt;";
        } else if (operator.equals("<=")) {
            inverseOperator = ">";
        }
        
        return inverseOperator;
    }
}