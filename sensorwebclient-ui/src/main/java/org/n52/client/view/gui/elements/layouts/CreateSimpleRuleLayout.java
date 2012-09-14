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
package org.n52.client.view.gui.elements.layouts;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.n52.client.control.I18N;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.ses.ChangeLayoutEvent;
import org.n52.client.eventBus.events.ses.CreateSimpleRuleEvent;
import org.n52.client.eventBus.events.ses.GetPhenomenaEvent;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.view.gui.elements.interfaces.Layout;
import org.n52.shared.serializable.pojos.Rule;

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
    private TextItem ruleValueCondItem;
    private TextItem countCondItem;
    private TextItem cTimeItem;
    
    private SelectItem operatorRuleItem;
    private SelectItem operatorCondItem;
    private SelectItem ruleValueUnitItem;
    private SelectItem ruleValueUnitCondItem;
    private SelectItem timeUnitItem;
    private SelectItem cTimeUnitItem;
    
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
        super(I18N.sesClient.createBasicRule());

        DataSource dataSource = new DataSource();

        this.form.setDataSource(dataSource);

        // Title of the rule
        this.nameItem = new TextItem();
        this.nameItem.setName("title");
        this.nameItem.setTitle(I18N.sesClient.name());
        this.nameItem.setRequired(true);
        this.nameItem.setLength(70);
        this.nameItem.setWidth(this.selectItemWidth);
        this.nameItem.setKeyPressFilter("[0-9a-zA-Z_]");
        this.nameItem.setHint(I18N.sesClient.possibleChars() + " [0-9 a-z A-Z _]");
        this.nameItem.setShowHintInField(true);

        this.form.setFields(this.headerItem, this.nameItem);

        setHashMapData();

        // initializeComboBoxes
        initComboBoxes();

        // DescriptionItem
        this.descriptionItem = new TextAreaItem();
        this.descriptionItem.setName("description");
        this.descriptionItem.setTitle(I18N.sesClient.description());
        this.descriptionItem.setRequired(true);
        this.descriptionItem.setHeight(100);
        this.descriptionItem.setWidth(250);
        this.descriptionItem.setHint(I18N.sesClient.possibleChars() + " [0-9 a-z A-Z _ -]");
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
        this.publishRadioGroup = new RadioGroupItem("publish", I18N.sesClient.publish());
        this.publishRadioGroup.setValueMap(I18N.sesClient.yes(), I18N.sesClient.no());
        this.publishRadioGroup.setDefaultValue(I18N.sesClient.yes());

        this.conditionRadioGroup = new RadioGroupItem("condition", I18N.sesClient.enterExitCondition());
        this.conditionRadioGroup.setValueMap(I18N.sesClient.yes(), I18N.sesClient.no());
        this.conditionRadioGroup.setDefaultValue(I18N.sesClient.yes());
        this.conditionRadioGroup.addChangedHandler(new ChangedHandler() {
            @SuppressWarnings("synthetic-access")
            public void onChanged(ChangedEvent event) {
                if (event.getValue().toString().equals(I18N.sesClient.no())) {
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
            @SuppressWarnings({ "synthetic-access" })
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
        this.cancelButton.setTitle(I18N.sesClient.cancel());
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
            SC.say(I18N.sesClient.ruleNameStartsWithDigit());
            return false;
        }
        
        // check station
        String station = this.sensorItem.getValueAsString();
        if (station == null || station.equals("")) {
            SC.say(I18N.sesClient.chooseStation());
            return false;
        }
        // check phenomenon
        String phenomenon = this.phenomenonItem.getValueAsString();
        if (phenomenon == null || phenomenon.equals("")) {
            SC.say(I18N.sesClient.choosePhenomenon());
            return false;
        }
        // check ruleType
        if (this.selectedType == SimpleRuleType.NONE) {
            SC.say(I18N.sesClient.chooseRuleType());
            return false;
        }
        // check trend over time
        if (this.selectedType == SimpleRuleType.TENDENZ_ZEIT) {
            // check time and unit
            if (this.timeItem.getValue() == null || this.timeUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateTimeUnit());
                return false;
            }
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
                // check condition time and value
                if (this.cTimeItem.getValue() == null || this.cTimeUnitItem.getValue() == null) {
                    SC.say(I18N.sesClient.indicateConditionTimeUnit());
                    return false;
                }
                // check condition value and unit
                if (this.ruleValueCondItem.getValue() == null || this.ruleValueUnitCondItem.getValue() == null) {
                    SC.say(I18N.sesClient.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check trend over count
        if (this.selectedType == SimpleRuleType.TENDENZ_ANZAHL) {
            // check count
            if (this.countItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateCount());
                return false;
            }
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
                // check condition count
                if (this.countCondItem.getValue() == null) {
                    SC.say(I18N.sesClient.indicateConditionCount());
                    return false;
                }
                // check condition value and unit
                if (this.ruleValueCondItem.getValue() == null || this.ruleValueUnitCondItem.getValue() == null) {
                    SC.say(I18N.sesClient.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check over- undershoot
        if (this.selectedType == SimpleRuleType.UEBER_UNTERSCHREITUNG) {
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
                // check condition value and unit
                if (this.ruleValueCondItem.getValue() == null || this.ruleValueUnitCondItem.getValue() == null) {
                    SC.say(I18N.sesClient.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check sum over time
        if (this.selectedType == SimpleRuleType.SUMME_ZEIT) {
            // check value and unit
            if (this.ruleValueItem.getValue() == null || this.ruleValueUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateValueUnit());
                return false;
            }
            // check count
            if (this.countItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateCount());
                return false;
            }
        }
        // sensor failure
        if (this.selectedType == SimpleRuleType.AUSFALL) {
            // check time and unit
            if (this.timeItem.getValue() == null || this.timeUnitItem.getValue() == null) {
                SC.say(I18N.sesClient.indicateTimeUnit());
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

        if (this.publishRadioGroup.getValue().toString().equals(I18N.sesClient.yes())) {
            this.publish = true;
        }

        if (this.ruleTyp == SimpleRuleType.UEBER_UNTERSCHREITUNG) {
            createOverUnderShootRule();
        } else if (this.ruleTyp == SimpleRuleType.TENDENZ_ANZAHL) {
            createTendenzAnzahlRule();
        } else if (this.ruleTyp == SimpleRuleType.TENDENZ_ZEIT) {
            createTendenzZeitRule();
        } else if (this.ruleTyp == SimpleRuleType.SUMME_ZEIT) {
            createSummeZeitRule();
        } else if (this.ruleTyp == SimpleRuleType.AUSFALL) {
            createAusfallRule();
        }
    }

    /**
     * Sensor failure
     */
    private void createAusfallRule() {
        String rTime = this.timeItem.getValueAsString();
        String rTimeUnit = this.timeUnitItem.getValueAsString();
        
        Rule rule =
            new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
                    this.description, this.publish, this.condition, Integer.parseInt(Cookies
                            .getCookie(SesRequestManager.COOKIE_USER_ID)), rTime, rTimeUnit);

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
        
        if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorCondItem.getValueAsString());
            cValue = this.ruleValueCondItem.getValueAsString();
            cUnit = this.ruleValueUnitCondItem.getValueAsString();
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

        if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorCondItem.getValueAsString());
            
            cValue = this.ruleValueCondItem.getValueAsString();
            cUnit = this.ruleValueUnitCondItem.getValueAsString();
            this.condition = false;
            cTime = this.cTimeItem.getValueAsString();
            cTimeUnit = this.cTimeUnitItem.getValueAsString();

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
        int operatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int operatorIndexCond = 0;

        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();
        String cValue;
        String cUnit;

        String countValue = this.countItem.getValueAsString();
        String countCondValue;

        if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorCondItem.getValueAsString());
            
            cValue = this.ruleValueCondItem.getValueAsString();
            cUnit = this.ruleValueUnitCondItem.getValueAsString();
            this.condition = false;
            countCondValue = this.countCondItem.getValueAsString();

        } else {
            operatorIndexCond = createCounterOperator(operatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.condition = true;
            countCondValue = countValue;
        }
        Rule rule =
                new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
                        this.description, this.publish, this.condition, operatorIndex, rValue, rUnit,
                        operatorIndexCond, cValue, cUnit, Integer.parseInt(Cookies
                                .getCookie(SesRequestManager.COOKIE_USER_ID)), countValue, countCondValue);

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));

    }

    /**
     * Creates the over under shoot rule.
     */
    private void createOverUnderShootRule() {

        int operatorIndex = getOperatorIndex(this.operatorRuleItem.getValueAsString());
        
        int operatorIndexCond = 0;
        
        String rValue = this.ruleValueItem.getValueAsString();
        String rUnit = this.ruleValueUnitItem.getValueAsString();

        String cValue;
        String cUnit;

        if (this.conditionRadioGroup.getValue().toString().equals(I18N.sesClient.no())) {
            // enter condition != exit condition
            operatorIndexCond = getOperatorIndex(this.operatorCondItem.getValueAsString());
            
            cValue = this.ruleValueCondItem.getValueAsString();
            cUnit = this.ruleValueUnitCondItem.getValueAsString();
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
                                .getCookie(SesRequestManager.COOKIE_USER_ID)));

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
    }

    /**
     * @param operatorIndex
     * @return
     */
    private int createCounterOperator(int operatorIndex) {
        switch (operatorIndex) {
        case Rule.EQUALTO_OPERATOR:
            return Rule.NOTEQUALTO_OPERATOR;
        case Rule.NOTEQUALTO_OPERATOR:
            return Rule.EQUALTO_OPERATOR;
        case Rule.GREATERTHAN_OPERATOR:
            return Rule.LESSTHANOREQUALTO_OPERATOR;
        case Rule.LESSTHAN_OPERATOR:
            return Rule.GREATERTHANOREQUALTO_OPERATOR;
        case Rule.GREATERTHANOREQUALTO_OPERATOR:
            return Rule.LESSTHAN_OPERATOR;
        case Rule.LESSTHANOREQUALTO_OPERATOR:
            return Rule.GREATERTHAN_OPERATOR;
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
        } else if (this.selectedType == SimpleRuleType.UEBER_UNTERSCHREITUNG) {
            // add elements to form
            
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENZ_ZEIT) {
            // add elements to form
            
            setTimeItem();
            setTimeUnitItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.timeItem, this.timeUnitItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENZ_ANZAHL) {
            // add elements to form
            
            setCountItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.show();
            this.ruleElementsForm.redraw();
        }else if (this.selectedType == SimpleRuleType.SUMME_ZEIT) {
            // add elements to form
            
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            setCountItem();
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            this.conditionRadioGroup.hide();
            this.ruleElementsForm.redraw();
        }else if (this.selectedType == SimpleRuleType.AUSFALL) {
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

        if (this.selectedType == SimpleRuleType.UEBER_UNTERSCHREITUNG) {
            // add elements to layout
            setOperatorCondItem();
            setRuleValueCondItem();
            setRuleValueUnitCondItem();
            
            this.ruleElementsConditionForm.setFields(this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
            this.ruleElementsConditionForm.redraw();

        } else if (this.selectedType == SimpleRuleType.SUMME_ZEIT) {
            //
        } else if (this.selectedType == SimpleRuleType.TENDENZ_ANZAHL) {
            // add elements to layout
            
            setCountCondItem();
            setOperatorCondItem();
            setRuleValueCondItem();
            setRuleValueUnitCondItem();
            
            this.ruleElementsConditionForm.setFields(this.countCondItem, this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
            this.ruleElementsConditionForm.redraw();
        } else if (this.selectedType == SimpleRuleType.TENDENZ_ZEIT) {
            // add elements to layout
            
            setcTimeItem();
            setcTimeUnitItem();
            setOperatorCondItem();
            setRuleValueCondItem();
            setRuleValueUnitCondItem();
            
            this.ruleElementsConditionForm.setFields(this.cTimeItem, this.cTimeUnitItem, this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
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
        this.headerItem.setDefaultValue(I18N.sesClient.createBasicRule());
        // rename button
        this.createButtonItem.setTitle(I18N.sesClient.create());
        
        // hide cancelButton
        this.cancelButton.setVisible(false);

        // hide publish Radio Group
        this.publishRadioGroup.show();

        // clear title field
        this.nameItem.clearValue();

        // set selectedType to nothing
        this.selectedType = SimpleRuleType.NONE;
        this.conditionRadioGroup.setValue(I18N.sesClient.yes());

        // clear first three list boxes
        this.station = null;
        this.sensorItem.clearValue();
        this.phenomenonItem.clearValue();

        // clear description field
        this.descriptionItem.clearValue();

        // set radio buttons to "yes"
        this.publishRadioGroup.setValue(I18N.sesClient.yes());

        if (this.ruleTypeItem != null) {
            this.ruleTypeItem.clearValue();
            this.ruleTypeItem.setValueMap(this.ruleTypesHashMap);
        }
        if (this.operatorRuleItem != null) {
            this.operatorRuleItem.setValueMap(this.operatorHashMap);
        }
        if (this.operatorCondItem != null) {
            this.operatorCondItem.setValueMap(this.operatorHashMap);
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
        
        this.sensorItem = new ComboBoxItem("sensors", I18N.sesClient.sensor());
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
        
        this.phenomenonItem = new SelectItem("phenomena", I18N.sesClient.phenomenon());
        this.phenomenonItem.setWidth(this.selectItemWidth2);
        this.phenomenonItem.setTitleOrientation(TitleOrientation.TOP);
        this.phenomenonItem.setDisabled(true);
        
        this.ruleTypeItem = new SelectItem("ruleType", I18N.sesClient.ruleType());
        this.ruleTypeItem.setWidth(this.selectItemWidth2);
        this.ruleTypeItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleTypeItem.setDisabled(true);
        this.ruleTypeItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {

                String value = event.getValue().toString();

                if (value.equals(I18N.sesClient.overUnderShoot())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.UEBER_UNTERSCHREITUNG;
                } else if (value.equals(I18N.sesClient.trendOverTime())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.TENDENZ_ZEIT;
                } else if (value.equals(I18N.sesClient.trendOverCount())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.TENDENZ_ANZAHL;
                } else if (value.equals(I18N.sesClient.sumOverCountMeasurements())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.SUMME_ZEIT;
                } else if (value.equals(I18N.sesClient.sensorFailure())) {
                    CreateSimpleRuleLayout.this.selectedType = SimpleRuleType.AUSFALL;
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
        this.edit = true;
        this.cancelButton.setVisible(true);
        this.oldRuleName = rule.getTitle();
        // rename header
        this.headerItem.setDefaultValue(I18N.sesClient.editBasicRule());
        // rename button
        this.createButtonItem.setTitle(I18N.sesClient.saveChanges());
        
        // set publishrRadioGroup
        if (rule.isPublish()) {
            this.publishRadioGroup.setValue(I18N.sesClient.yes());
        } else {
            this.publishRadioGroup.setValue(I18N.sesClient.no());
        }
        
        // title
        this.nameItem.setValue(rule.getTitle());
        // description
        this.descriptionItem.setValue(rule.getDescription());
        // station
        this.station = rule.getStation();
        // phenomenon
        this.phenomenon = rule.getPhenomenon();
        
        this.sensorItem.setValue(this.station);
        
        // get Phenomena
        EventBus.getMainEventBus().fireEvent(new GetPhenomenaEvent(this.station));
        
        // enable fields
        this.phenomenonItem.setDisabled(false);
        this.ruleTypeItem.setDisabled(false);
        
        // ruleType
        this.selectedType = rule.getRuleType();
        if (this.selectedType.equals(SimpleRuleType.TENDENZ_ZEIT)) {
            this.ruleTypeItem.setValue(I18N.sesClient.trendOverTime());
        }
        if (this.selectedType.equals(SimpleRuleType.TENDENZ_ANZAHL)) {
            this.ruleTypeItem.setValue(I18N.sesClient.trendOverCount());
        }
        if (this.selectedType.equals(SimpleRuleType.UEBER_UNTERSCHREITUNG)) {
            this.ruleTypeItem.setValue(I18N.sesClient.overUnderShoot());
        }
        if (this.selectedType.equals(SimpleRuleType.SUMME_ZEIT)) {
            this.ruleTypeItem.setValue(I18N.sesClient.sumOverCountMeasurements());
        }
        if (this.selectedType.equals(SimpleRuleType.AUSFALL)) {
            this.ruleTypeItem.setValue(I18N.sesClient.sensorFailure());
        }
        
        String operator = "";
        if (rule.getrOperatorIndex() == 0) {
            operator = "=";
        } else if (rule.getrOperatorIndex() == 1) {
            operator = "<>";
        } else if (rule.getrOperatorIndex() == 2) {
            operator = ">";
        } else if (rule.getrOperatorIndex() == 3) {
            operator = "&lt;";
        } else if (rule.getrOperatorIndex() == 4) {
            operator = ">=";
        } else if (rule.getrOperatorIndex() == 5) {
            operator = "<=";
        }
        
        String operatorCondition = "";
        if (rule.getcOperatorIndex() == 0) {
            operatorCondition = "=";
        } else if (rule.getcOperatorIndex() == 1) {
            operatorCondition = "<>";
        } else if (rule.getcOperatorIndex() == 2) {
            operatorCondition = ">";
        } else if (rule.getcOperatorIndex() == 3) {
            operatorCondition = "&lt;";
        } else if (rule.getcOperatorIndex() == 4) {
            operatorCondition = ">=";
        } else if (rule.getcOperatorIndex() == 5) {
            operatorCondition = "<=";
        }
        
        // set condition radio group
        if (rule.isEnterEqualsExitCondition()) {
            this.condition = true;
            this.conditionRadioGroup.setValue(I18N.sesClient.yes());
        } else {
            this.condition = false;
            this.conditionRadioGroup.setValue(I18N.sesClient.no());
            
            setRuleValueCondItem();
            setRuleValueUnitCondItem();
            
            this.ruleValueCondItem.setValue(rule.getcValue());
            this.ruleValueUnitCondItem.setValue(rule.getcUnit());
        }
        
        if (rule.getRuleType().equals(SimpleRuleType.UEBER_UNTERSCHREITUNG)) {
            setRuleValueItem();
            setRuleValueUnitItem();
            setOperatorRuleItem();
            
            this.ruleValueItem.setValue(rule.getrValue());
            this.ruleValueUnitItem.setValue(rule.getrUnit());
            this.operatorRuleItem.setValue(operator);
            this.ruleElementsForm.setFields(this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorCondItem();
                this.operatorCondItem.setValue(operatorCondition);
                
                this.ruleElementsConditionForm.setFields(this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
            }
        } else if (rule.getRuleType().equals(SimpleRuleType.TENDENZ_ZEIT)) {
            setTimeItem();
            setTimeUnitItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.timeItem.setValue(rule.getrTime());
            this.timeUnitItem.setValue(rule.getrTimeUnit());
            this.operatorRuleItem.setValue(operator);
            this.ruleValueItem.setValue(rule.getrValue());
            this.ruleValueUnitItem.setValue(rule.getrUnit());
            
            this.ruleElementsForm.setFields(this.timeItem, this.timeUnitItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
           
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorCondItem();
                setcTimeItem();
                setcTimeUnitItem();
                
                this.operatorCondItem.setValue(operatorCondition);
                this.cTimeItem.setValue(rule.getcTime());
                this.cTimeUnitItem.setValue(rule.getcTimeUnit());
                
                this.ruleElementsConditionForm.setFields(this.cTimeItem, this.cTimeUnitItem, this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
            }
        } else if (rule.getRuleType().equals(SimpleRuleType.TENDENZ_ANZAHL)) {
            setCountItem();
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            
            this.countItem.setValue(rule.getCount());
            this.operatorRuleItem.setValue(operator);
            this.ruleValueItem.setValue(rule.getrValue());
            this.ruleValueUnitItem.setValue(rule.getrUnit());
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setOperatorCondItem();
                setCountCondItem();
                
                this.operatorCondItem.setValue(operatorCondition);
                this.countCondItem.setValue(rule.getCount());
                
                this.ruleElementsConditionForm.setFields(this.countCondItem, this.operatorCondItem, this.ruleValueCondItem, this.ruleValueUnitCondItem);
            }
        } else if (rule.getRuleType().equals(SimpleRuleType.SUMME_ZEIT)) {
            setOperatorRuleItem();
            setRuleValueItem();
            setRuleValueUnitItem();
            setCountItem();
            
            this.operatorRuleItem.setValue(operator);
            this.ruleValueItem.setValue(rule.getrValue());
            this.ruleValueUnitItem.setValue(rule.getrUnit());
            this.countItem.setValue(rule.getrTime());
            
            this.ruleElementsForm.setFields(this.countItem, this.operatorRuleItem, this.ruleValueItem, this.ruleValueUnitItem);
        } else if (rule.getRuleType().equals(SimpleRuleType.AUSFALL)) {
            setTimeItem();
            setTimeUnitItem();
            
            this.timeItem.setValue(rule.getrTime());
            this.timeUnitItem.setValue(rule.getrTimeUnit());
            
            this.ruleElementsForm.setFields(this.timeItem, this.timeUnitItem);
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
        this.ruleTypesHashMap.put(I18N.sesClient.trendOverTime(), I18N.sesClient.trendOverTime());
        this.ruleTypesHashMap.put(I18N.sesClient.trendOverCount(), I18N.sesClient.trendOverCount());
        this.ruleTypesHashMap.put(I18N.sesClient.overUnderShoot(), I18N.sesClient.overUnderShoot());
        this.ruleTypesHashMap.put(I18N.sesClient.sumOverCountMeasurements(), I18N.sesClient.sumOverCountMeasurements());
        this.ruleTypesHashMap.put(I18N.sesClient.sensorFailure(), I18N.sesClient.sensorFailure());

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
        this.ruleValueItem.setTitle(I18N.sesClient.value());
        this.ruleValueItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueItem.setKeyPressFilter("[0-9]");
    }

    private void setRuleValueUnitItem() {
        this.ruleValueUnitItem = new SelectItem();
        this.ruleValueUnitItem.setWidth(this.ruleItemWidth);
        this.ruleValueUnitItem.setTitle(I18N.sesClient.unit());
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
        this.countItem.setTitle(I18N.sesClient.count());
        this.countItem.setTitleOrientation(TitleOrientation.TOP);
        this.countItem.setKeyPressFilter("[0-9]");
    }

    private void setTimeItem() {
        this.timeItem = new TextItem();
        this.timeItem.setWidth(this.ruleItemWidth);
        this.timeItem.setTitle("<nobr>" + I18N.sesClient.timeValue() + "</nobr>");
        this.timeItem.setTitleOrientation(TitleOrientation.TOP);
        this.timeItem.setKeyPressFilter("[0-9]");
    }

    private void setTimeUnitItem() {
        this.timeUnitItem = new SelectItem();
        this.timeUnitItem.setWidth(this.ruleItemWidth);
        this.timeUnitItem.setTitle(I18N.sesClient.timeUnit());
        this.timeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.timeUnitItem.setTooltip("<nobr>" + I18N.sesClient.unitsTime() + "</nobr>");
        this.timeUnitItem.setValueMap(this.timeUnitHashMap);
        this.timeUnitItem.setDefaultValue("H");
        this.timeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setRuleValueCondItem() {
        this.ruleValueCondItem = new TextItem();
        this.ruleValueCondItem.setWidth(this.ruleItemWidth);
        this.ruleValueCondItem.setTitle(I18N.sesClient.value());
        this.ruleValueCondItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueCondItem.setKeyPressFilter("[0-9]");
    }

    private void setRuleValueUnitCondItem() {
        this.ruleValueUnitCondItem = new SelectItem();
        this.ruleValueUnitCondItem.setWidth(this.ruleItemWidth);
        this.ruleValueUnitCondItem.setTitle(I18N.sesClient.unit());
        this.ruleValueUnitCondItem.setTitleOrientation(TitleOrientation.TOP);
        this.ruleValueUnitCondItem.setValueMap(this.unitHashMap);
        this.ruleValueUnitCondItem.setTextAlign(Alignment.CENTER);
        
        ArrayList<String> list = new ArrayList<String>(this.unitHashMap.values());
        if (list.size() != 0) {
            this.ruleValueUnitCondItem.setDefaultValue(list.get(0));
        }
    }

    private void setCountCondItem() {
        this.countCondItem = new TextItem();
        this.countCondItem.setWidth(this.ruleItemWidth);
        this.countCondItem.setTitle(I18N.sesClient.count());
        this.countCondItem.setTitleOrientation(TitleOrientation.TOP);
        this.countCondItem.setKeyPressFilter("[0-9]");
    }

    private void setcTimeItem() {
        this.cTimeItem = new TextItem();
        this.cTimeItem.setWidth(this.ruleItemWidth);
        this.cTimeItem.setTitle("<nobr>" + I18N.sesClient.timeValue() + "</nobr>");
        this.cTimeItem.setTitleOrientation(TitleOrientation.TOP);
        this.cTimeItem.setKeyPressFilter("[0-9]");
    }

    private void setcTimeUnitItem() {
        this.cTimeUnitItem = new SelectItem();
        this.cTimeUnitItem.setWidth(this.ruleItemWidth);
        this.cTimeUnitItem.setTitle(I18N.sesClient.timeUnit());
        this.cTimeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.cTimeUnitItem.setTooltip("<nobr>" + I18N.sesClient.unitsTime() + "</nobr>");
        this.cTimeUnitItem.setValueMap(this.timeUnitHashMap);
        this.cTimeUnitItem.setDefaultValue("H");
        this.cTimeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setOperatorRuleItem() {
        this.operatorRuleItem = new SelectItem();
        this.operatorRuleItem.setWidth(this.ruleItemWidth);
        this.operatorRuleItem.setTitle(I18N.sesClient.operator());
        this.operatorRuleItem.setTitleOrientation(TitleOrientation.TOP);
        this.operatorRuleItem.setValueMap(this.operatorHashMap);
        this.operatorRuleItem.setDefaultValue(">");
        this.operatorRuleItem.setTextAlign(Alignment.CENTER);
        this.operatorRuleItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                if (operatorCondItem != null) {
                    operatorCondItem.setValue(getInverseOperator((String)event.getValue()));
                }
            }
        });
    }

    private void setOperatorCondItem() {
        this.operatorCondItem = new SelectItem();
        this.operatorCondItem.setWidth(this.ruleItemWidth);
        this.operatorCondItem.setTitle(I18N.sesClient.operator());
        this.operatorCondItem.setTitleOrientation(TitleOrientation.TOP);
        this.operatorCondItem.setValueMap(this.operatorHashMap);
        this.operatorCondItem.setTextAlign(Alignment.CENTER);
        
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
        this.operatorCondItem.setDefaultValue(defaultValue);
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