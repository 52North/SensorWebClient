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
package org.n52.client.ses.ui.rules;

import static com.google.gwt.user.client.Cookies.getCookie;
import static java.lang.Integer.parseInt;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.EDIT_RULES;
import static org.n52.client.ses.ui.FormLayout.LayoutType.RULELIST;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SUM_OVER_TIME;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.TENDENCY_OVER_COUNT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.TENDENCY_OVER_TIME;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ROLE;
import static org.n52.shared.util.MathSymbolUtil.getIndexFor;
import static org.n52.shared.util.MathSymbolUtil.getInverse;
import static org.n52.shared.util.MathSymbolUtil.getMathSymbols;
import static org.n52.shared.util.MathSymbolUtil.getSymbolForIndex;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.CreateSimpleRuleEvent;
import org.n52.client.ses.event.GetPhenomenaEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.RuleBuilder;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;

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
public class CreateSimpleRuleLayout extends FormLayout {

    /** The selected type. */
    private SimpleRuleType selectedRuleType;

    private int selectItemWidth = 250;
    
    private int selectItemWidth2 = 200;
    
    private int entryItemWidth = 60;
    
    // further elements
    /** The title item. */
    private TextItem nameItem;

    /** The description item. */
    private TextAreaItem descriptionItem;

    /** The publish radio group. */
    private RadioGroupItem publishRadioGroup;

    /** The condition radio group. */
    private RadioGroupItem enterConditionIsSameExitConditionRadioGroup;

    /** The create or edit button */
    private ButtonItem createButtonItem;
    
    private ButtonItem cancelButton;

    // values
    /** The title. */
    private String name;

    /** The station. */
    private String procedure;

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
    private boolean enterConditionIsSameAsExitCondition;

    private boolean edit = false;
    
    private String oldRuleName = "";
    
    // first three combo boxes
    private ComboBoxItem procedureItem;
    private SelectItem phenomenonItem;
    private SelectItem ruleTypeItem;
    
    // dynamic rule elements
    private TextItem entryValueItem;
    private TextItem countItem;
    private TextItem entryTimeItem;
    private TextItem entryValueConditionItem;
    private TextItem countConditionItem;
    private TextItem exitTimeItem;
    
    private SelectItem entryOperatorItem;
    private SelectItem exitOperatorItem;
    private SelectItem entryValueUnitItem;
    private SelectItem entryValueUnitConditionItem;
    private SelectItem entryTimeUnitItem;
    private SelectItem exitTimeUnitItem;
    
    private DynamicForm entryConditionItemsForm;
    private DynamicForm exitConditionItemsForm;
    private DynamicForm ruleForm;
    
    // hash maps for the selectItems
    private LinkedHashMap<String, String> sensorsHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> phenomenaHashMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> ruleTypesHashMap = new LinkedHashMap<String, String>();
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

        this.enterConditionIsSameExitConditionRadioGroup = new RadioGroupItem("condition", i18n.enterExitCondition());
        this.enterConditionIsSameExitConditionRadioGroup.setValueMap(i18n.yes(), i18n.no());
        this.enterConditionIsSameExitConditionRadioGroup.setDefaultValue(i18n.yes());
        this.enterConditionIsSameExitConditionRadioGroup.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                if (event.getValue().toString().equals(i18n.no())) {
                    setRuleConditionFields();
                } else {
                    CreateSimpleRuleLayout.this.exitConditionItemsForm.setFields();
                }
            }
        });

        // form for radioButtons
        DynamicForm radioForm = new DynamicForm();
        DynamicForm radioForm2 = new DynamicForm();
        
        radioForm.setItems(this.enterConditionIsSameExitConditionRadioGroup);
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
                String role = Cookies.getCookie(COOKIE_USER_ROLE);
                if (role.equals("ADMIN")) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(RULELIST));
                } else {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(EDIT_RULES));
                }
            }
        });
        form3.setFields(this.createButtonItem, this.cancelButton);
        
        this.ruleForm = new DynamicForm();
        this.ruleForm.setWidth("50%");
        this.ruleForm.setNumCols(6);
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setWidth(100);
        this.ruleForm.setFields(spacerItem, procedureItem, phenomenonItem, ruleTypeItem);
        
        // Dynamic Forms
        this.entryConditionItemsForm = new DynamicForm();
        this.entryConditionItemsForm.setFixedColWidths(true);
        this.entryConditionItemsForm.setNumCols(5);
        this.entryConditionItemsForm.setWidth("30%");
        
        this.exitConditionItemsForm = new DynamicForm();
        this.exitConditionItemsForm.setFixedColWidths(true);
        this.exitConditionItemsForm.setNumCols(5);
        this.exitConditionItemsForm.setWidth("30%");
        
        VLayout v = new VLayout();
        v.addMember(entryConditionItemsForm);
        v.addMember(exitConditionItemsForm);
        
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
        String station = this.procedureItem.getValueAsString();
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
        if (this.selectedRuleType == SimpleRuleType.NONE) {
            SC.say(i18n.chooseRuleType());
            return false;
        }
        // check trend over time
        if (this.selectedRuleType == SimpleRuleType.TENDENCY_OVER_TIME) {
            // check time and unit
            if (this.entryTimeItem.getValue() == null || this.entryTimeUnitItem.getValue() == null) {
                SC.say(i18n.indicateTimeUnit());
                return false;
            }
            // check value and unit
            if (this.entryValueItem.getValue() == null || this.entryValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition time and value
                if (this.exitTimeItem.getValue() == null || this.exitTimeUnitItem.getValue() == null) {
                    SC.say(i18n.indicateConditionTimeUnit());
                    return false;
                }
                // check condition value and unit
                if (this.entryValueConditionItem.getValue() == null || this.entryValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check trend over count
        if (this.selectedRuleType == SimpleRuleType.TENDENCY_OVER_COUNT) {
            // check count
            if (this.countItem.getValue() == null) {
                SC.say(i18n.indicateCount());
                return false;
            }
            // check value and unit
            if (this.entryValueItem.getValue() == null || this.entryValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition count
                if (this.countConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionCount());
                    return false;
                }
                // check condition value and unit
                if (this.entryValueConditionItem.getValue() == null || this.entryValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check over- undershoot
        if (this.selectedRuleType == SimpleRuleType.OVER_UNDERSHOOT) {
            // check value and unit
            if (this.entryValueItem.getValue() == null || this.entryValueUnitItem.getValue() == null) {
                SC.say(i18n.indicateValueUnit());
                return false;
            }
            // check condition
            if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
                // check condition value and unit
                if (this.entryValueConditionItem.getValue() == null || this.entryValueUnitConditionItem.getValue() == null) {
                    SC.say(i18n.indicateConditionValueUnit());
                    return false;
                }
            }
        }
        // check sum over time
        if (this.selectedRuleType == SimpleRuleType.SUM_OVER_TIME) {
            // check value and unit
            if (this.entryValueItem.getValue() == null || this.entryValueUnitItem.getValue() == null) {
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
        if (this.selectedRuleType == SimpleRuleType.SENSOR_LOSS) {
            // check time and unit
            if (this.entryTimeItem.getValue() == null || this.entryTimeUnitItem.getValue() == null) {
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
        this.procedure = this.procedureItem.getValueAsString();
        this.phenomenon = this.phenomenonItem.getValueAsString();

        this.ruleTyp = this.selectedRuleType;

        this.description = (String) this.descriptionItem.getValue();

        this.publish = false;
        this.enterConditionIsSameAsExitCondition = false;

        if (this.publishRadioGroup.getValue().toString().equals(i18n.yes())) {
            this.publish = true;
        }

        if (this.ruleTyp == SimpleRuleType.OVER_UNDERSHOOT) {
            createOverUnderShootRule();
//        } else if (this.ruleTyp == SimpleRuleType.TENDENCY_OVER_COUNT) {
//            createTendenzAnzahlRule();
//        } else if (this.ruleTyp == SimpleRuleType.TENDENCY_OVER_TIME) {
//            createTendenzZeitRule();
//        } else if (this.ruleTyp == SimpleRuleType.SUM_OVER_TIME) {
//            createSummeZeitRule();
        } else if (this.ruleTyp == SimpleRuleType.SENSOR_LOSS) {
            createAusfallRule();
        }
    }

    /**
     * Sensor failure
     */
    private void createAusfallRule() {
        String rTime = this.entryTimeItem.getValueAsString();
        String rTimeUnit = this.entryTimeUnitItem.getValueAsString();
        
        int cookieAsInt = Integer.parseInt(Cookies.getCookie(COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
//                        .setProcedure(procedure)
//                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setEnterIsSameAsExitCondition(enterConditionIsSameAsExitCondition)
                        .setUserId(cookieAsInt)
                        .setEntryTime(rTime)
                        .setEntryTimeUnit(rTimeUnit)
                        .build();
        
        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
        
    }

    /**
     * Sum over Time
     */
    private void createSummeZeitRule() {
        int entryOperatorIndex = getIndexFor(this.entryOperatorItem.getValueAsString());
        
        int exitOperatorIndex = 0;
        
        String entryValue = this.entryValueItem.getValueAsString();
        String entryUnit = this.entryValueUnitItem.getValueAsString();
        String exitValue;
        String exitUnit;
        
        String entryCount = this.countItem.getValueAsString();
        
        if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            exitOperatorIndex = getIndexFor(this.exitOperatorItem.getValueAsString());
            exitValue = this.entryValueConditionItem.getValueAsString();
            exitUnit = this.entryValueUnitConditionItem.getValueAsString();
            this.enterConditionIsSameAsExitCondition = false;

        } else {
            exitOperatorIndex = getInverse(entryOperatorIndex);
            exitValue = entryValue;
            exitUnit = entryUnit;
            this.enterConditionIsSameAsExitCondition = true;
        }
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setEnterIsSameAsExitCondition(enterConditionIsSameAsExitCondition)
                        .setEntryOperatorIndex(entryOperatorIndex)
                        .setEntryValue(entryValue)
                        .setEntryUnit(entryUnit)
                        .setExitOperatorIndex(exitOperatorIndex)
                        .setExitValue(exitValue)
                        .setExitUnit(exitUnit)
                        .setUserId(parseInt(getCookie(COOKIE_USER_ID)))
                        .setEntryCount(entryCount)
                        .build();

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
        
    }

    /**
     * Trend over Time
     */
    private void createTendenzZeitRule() {
        int entryOperatorIndex = getIndexFor(this.entryOperatorItem.getValueAsString());
        
        int exitOperatorIndex = 0;

        String entryValue = this.entryValueItem.getValueAsString();
        String entryUnit = this.entryValueUnitItem.getValueAsString();
        String exitValue;
        String exitUnit;

        String entryTime = this.entryTimeItem.getValueAsString();
        String entryTimeUnit = this.entryTimeUnitItem.getValueAsString();
        String exitTime;
        String exitTimeUnit;

        if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            exitOperatorIndex = getIndexFor(this.exitOperatorItem.getValueAsString());
            
            exitValue = this.entryValueConditionItem.getValueAsString();
            exitUnit = this.entryValueUnitConditionItem.getValueAsString();
            this.enterConditionIsSameAsExitCondition = false;
            exitTime = this.exitTimeItem.getValueAsString();
            exitTimeUnit = this.exitTimeUnitItem.getValueAsString();

        } else {
            exitOperatorIndex = getInverse(entryOperatorIndex);
            exitValue = entryValue;
            exitUnit = entryUnit;
            this.enterConditionIsSameAsExitCondition = true;
            exitTime = entryTime;
            exitTimeUnit = entryTimeUnit;
        }
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
//                        .setProcedure(procedure)
//                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setEnterIsSameAsExitCondition(enterConditionIsSameAsExitCondition)
                        .setEntryOperatorIndex(entryOperatorIndex)
                        .setEntryValue(entryValue)
                        .setEntryUnit(entryUnit)
                        .setUserId(parseInt(getCookie(COOKIE_USER_ID)))
                        .setEntryTime(entryTime)
                        .setEntryTimeUnit(entryTimeUnit)
                        .setExitTime(exitTime)
                        .setExitTimeUnit(exitTimeUnit)
                        .build();

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
    }

    /**
     * Trend over Count
     */
    private void createTendenzAnzahlRule() {
        int rOperatorIndex = getIndexFor(this.entryOperatorItem.getValueAsString());
        
        int operatorIndexCond = 0;

        String rValue = this.entryValueItem.getValueAsString();
        String rUnit = this.entryValueUnitItem.getValueAsString();
        String cValue;
        String cUnit;

        String countValue = this.countItem.getValueAsString();
        String countCondValue;

        if (this.enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            operatorIndexCond = getIndexFor(this.exitOperatorItem.getValueAsString());
            
            cValue = this.entryValueConditionItem.getValueAsString();
            cUnit = this.entryValueUnitConditionItem.getValueAsString();
            this.enterConditionIsSameAsExitCondition = false;
            countCondValue = this.countConditionItem.getValueAsString();

        } else {
            operatorIndexCond = getInverse(rOperatorIndex);
            cValue = rValue;
            cUnit = rUnit;
            this.enterConditionIsSameAsExitCondition = true;
            countCondValue = countValue;
        }

        int cookieAsInt = Integer.parseInt(Cookies.getCookie(COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
//                        .setProcedure(procedure)
//                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setEnterIsSameAsExitCondition(enterConditionIsSameAsExitCondition)
                        .setEntryOperatorIndex(rOperatorIndex)
                        .setEntryValue(rValue)
                        .setEntryUnit(rUnit)
                        .setExitOperatorIndex(operatorIndexCond)
                        .setExitValue(cValue)
                        .setExitUnit(cUnit)
                        .setUserId(cookieAsInt)
                        .setExitCount(countValue)
                        .setEntryCount(countCondValue)
                        .build();
        
        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));

    }

    /**
     * Creates the over under shoot rule.
     */
    private void createOverUnderShootRule() {

        int entryOperatorIndex = getIndexFor(entryOperatorItem.getValueAsString());
        int exitOperatorIndex = 0;
        
        String entryValue = entryValueItem.getValueAsString();
        String entryUnit = entryValueUnitItem.getValueAsString();

        String exitValue;
        String exitUnit;

        if (enterConditionIsSameExitConditionRadioGroup.getValue().toString().equals(i18n.no())) {
            // enter condition != exit condition
            exitOperatorIndex = getIndexFor(this.exitOperatorItem.getValueAsString());
            
            exitValue = entryValueConditionItem.getValueAsString();
            exitUnit = entryValueUnitConditionItem.getValueAsString();
            enterConditionIsSameAsExitCondition = false;
        } else {
            exitOperatorIndex = getInverse(entryOperatorIndex);
            exitValue = entryValue;
            exitUnit = entryUnit;
            enterConditionIsSameAsExitCondition = true;
        }

        int cookieAsInt = Integer.parseInt(Cookies.getCookie(COOKIE_USER_ID));
        Rule rule = RuleBuilder.aRule()
                        .setRuleType(ruleTyp)
                        .setTitle(name)
//                        .setProcedure(procedure)
//                        .setPhenomenon(phenomenon)
                        .setNotificationType(notificationType)
                        .setDescription(description)
                        .setPublish(publish)
                        .setEnterIsSameAsExitCondition(enterConditionIsSameAsExitCondition)
                        .setEntryOperatorIndex(entryOperatorIndex)
                        .setEntryValue(entryValue)
                        .setEntryUnit(entryUnit)
                        .setExitOperatorIndex(exitOperatorIndex)
                        .setExitValue(exitValue)
                        .setExitUnit(exitUnit)
                        .setUserId(cookieAsInt)
                        .build();
        
//        Rule rule =
//                new Rule(this.ruleTyp, this.name, this.station, this.phenomenon, this.notificationType,
//                        this.description, this.publish, this.condition, operatorIndex, rValue, rUnit,
//                        cOperatorIndex, cValue, cUnit, cookieAsInt);

        EventBus.getMainEventBus().fireEvent(new CreateSimpleRuleEvent(rule, this.edit, this.oldRuleName));
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
        this.procedureItem.clearValue();
        this.procedureItem.setValueMap(this.sensorsHashMap);
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
        enterConditionIsSameExitConditionRadioGroup.setValue("yes");
        // clear condition field
        exitConditionItemsForm.setFields();

        // delete all elements from condition layout
        if (selectedRuleType == null) {
            exitConditionItemsForm.setFields();
            enterConditionIsSameExitConditionRadioGroup.show();
            entryConditionItemsForm.redraw();
        } else if (selectedRuleType == OVER_UNDERSHOOT) {
            // add elements to form
            
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            
            entryConditionItemsForm.setFields(entryOperatorItem, entryValueItem, entryValueUnitItem);
            enterConditionIsSameExitConditionRadioGroup.show();
            entryConditionItemsForm.redraw();
        } else if (selectedRuleType == TENDENCY_OVER_TIME) {
            // add elements to form
            
            setEntryTimeItem();
            setEntryTimeUnitItem();
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            
            entryConditionItemsForm.setFields(entryTimeItem, entryTimeUnitItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
            enterConditionIsSameExitConditionRadioGroup.show();
            entryConditionItemsForm.redraw();
        } else if (selectedRuleType == TENDENCY_OVER_COUNT) {
            // add elements to form
            
            setCountItem();
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            
            entryConditionItemsForm.setFields(countItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
            enterConditionIsSameExitConditionRadioGroup.show();
            entryConditionItemsForm.redraw();
        }else if (selectedRuleType == SUM_OVER_TIME) {
            // add elements to form
            
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            setCountItem();
            
            entryConditionItemsForm.setFields(countItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
            enterConditionIsSameExitConditionRadioGroup.hide();
            entryConditionItemsForm.redraw();
        }else if (selectedRuleType == SENSOR_LOSS) {
            // add elements to form
            
            setEntryTimeItem();
            setEntryTimeUnitItem();
            
            entryConditionItemsForm.setFields(entryTimeItem, entryTimeUnitItem);
            enterConditionIsSameExitConditionRadioGroup.hide();
            entryConditionItemsForm.redraw();
        }
    }

    /**
     * Sets the rule condition fields. This is the second elements bar under the
     * regular bar. It is only visible if enter != exit condition is true
     * 
     * set if enter condition != exit condition
     */
    private void setRuleConditionFields() {

        if (selectedRuleType == OVER_UNDERSHOOT) {
            // add elements to layout
            setExitOperatorItem();
            setEntryValueConditionItem();
            setEntryValueUnitConditionItem();
            
            exitConditionItemsForm.setFields(exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            exitConditionItemsForm.redraw();

        } else if (selectedRuleType == SUM_OVER_TIME) {
            //
        } else if (selectedRuleType == TENDENCY_OVER_COUNT) {
            // add elements to layout
            
            setCountConditionItem();
            setExitOperatorItem();
            setEntryValueConditionItem();
            setEntryValueUnitConditionItem();
            
            exitConditionItemsForm.setFields(countConditionItem, exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            exitConditionItemsForm.redraw();
        } else if (selectedRuleType == TENDENCY_OVER_TIME) {
            // add elements to layout
            
            setExitTimeItem();
            setExitTimeUnitItem();
            setExitOperatorItem();
            setEntryValueConditionItem();
            setEntryValueUnitConditionItem();
            
            exitConditionItemsForm.setFields(exitTimeItem, exitTimeUnitItem, exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            exitConditionItemsForm.redraw();
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
        this.selectedRuleType = SimpleRuleType.NONE;
        this.enterConditionIsSameExitConditionRadioGroup.setValue(i18n.yes());

        // clear first three list boxes
        this.procedure = null;
        this.procedureItem.clearValue();
        this.phenomenonItem.clearValue();

        // clear description field
        this.descriptionItem.clearValue();

        // set radio buttons to "yes"
        this.publishRadioGroup.setValue(i18n.yes());

        if (this.ruleTypeItem != null) {
            this.ruleTypeItem.clearValue();
            this.ruleTypeItem.setValueMap(this.ruleTypesHashMap);
        }
        if (this.entryOperatorItem != null) {
            this.entryOperatorItem.setValueMap(getMathSymbols());
        }
        if (this.exitOperatorItem != null) {
            this.exitOperatorItem.setValueMap(getMathSymbols());
        }
        
        this.phenomenonItem.setDisabled(true);
        this.ruleTypeItem.setDisabled(true);
        
        // clear 
        this.entryConditionItemsForm.setFields();
        this.exitConditionItemsForm.setFields();

    }

    /**
     * Init the first and always visible combo boxes of the simple rule.
     */
    private void initComboBoxes() {
        
        this.procedureItem = new ComboBoxItem("sensors", i18n.sensor());
        this.procedureItem.setWidth(this.selectItemWidth);
        this.procedureItem.setTitleOrientation(TitleOrientation.TOP);
        this.procedureItem.addChangedHandler(new ChangedHandler() {
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
                    CreateSimpleRuleLayout.this.selectedRuleType = OVER_UNDERSHOOT;
                } else if (value.equals(i18n.trendOverTime())) {
                    CreateSimpleRuleLayout.this.selectedRuleType = TENDENCY_OVER_TIME;
                } else if (value.equals(i18n.trendOverCount())) {
                    CreateSimpleRuleLayout.this.selectedRuleType = TENDENCY_OVER_COUNT;
                } else if (value.equals(i18n.sumOverCountMeasurements())) {
                    CreateSimpleRuleLayout.this.selectedRuleType = SUM_OVER_TIME;
                } else if (value.equals(i18n.sensorFailure())) {
                    CreateSimpleRuleLayout.this.selectedRuleType = SENSOR_LOSS;
                } else {
                    CreateSimpleRuleLayout.this.selectedRuleType = null;
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
        TimeseriesMetadata metadata = rule.getTimeseriesMetadata();
        phenomenon = metadata.getPhenomenon();
        procedure = metadata.getProcedure();
        procedureItem.setValue(procedure);
        
        // get Phenomena
        EventBus.getMainEventBus().fireEvent(new GetPhenomenaEvent(procedure));
        
        // enable fields
        phenomenonItem.setDisabled(false);
        ruleTypeItem.setDisabled(false);
        
        // ruleType
        selectedRuleType = rule.getRuleType();
        if (selectedRuleType.equals(TENDENCY_OVER_TIME)) {
            ruleTypeItem.setValue(i18n.trendOverTime());
        }
        if (selectedRuleType.equals(TENDENCY_OVER_COUNT)) {
            ruleTypeItem.setValue(i18n.trendOverCount());
        }
        if (selectedRuleType.equals(OVER_UNDERSHOOT)) {
            ruleTypeItem.setValue(i18n.overUnderShoot());
        }
        if (selectedRuleType.equals(SUM_OVER_TIME)) {
            ruleTypeItem.setValue(i18n.sumOverCountMeasurements());
        }
        if (selectedRuleType.equals(SENSOR_LOSS)) {
            ruleTypeItem.setValue(i18n.sensorFailure());
        }

        String entryOperator = getSymbolForIndex(rule.getEntryOperatorIndex());
        String exitOperator = getSymbolForIndex(rule.getExitOperatorIndex());
        
        // set condition radio group
        if (rule.isEnterEqualsExitCondition()) {
            enterConditionIsSameAsExitCondition = true;
            enterConditionIsSameExitConditionRadioGroup.setValue(i18n.yes());
        } else {
            enterConditionIsSameAsExitCondition = false;
            enterConditionIsSameExitConditionRadioGroup.setValue(i18n.no());
            
            setEntryValueConditionItem();
            setEntryValueUnitConditionItem();
            
            entryValueConditionItem.setValue(rule.getExitValue());
            entryValueUnitConditionItem.setValue(rule.getExitUnit());
        }
        
        if (rule.getRuleType().equals(OVER_UNDERSHOOT)) {
            setEntryValueItem();
            setEntryValueUnitItem();
            setEntryOperatorRuleItem();
            
            entryValueItem.setValue(rule.getEntryValue());
            entryValueUnitItem.setValue(rule.getEntryUnit());
            entryOperatorItem.setValue(entryOperator);
            entryConditionItemsForm.setFields(entryOperatorItem, entryValueItem, entryValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setExitOperatorItem();
                exitOperatorItem.setValue(exitOperator);
                
                exitConditionItemsForm.setFields(exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(TENDENCY_OVER_TIME)) {
            setEntryTimeItem();
            setEntryTimeUnitItem();
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            
            entryTimeItem.setValue(rule.getEntryTime());
            entryTimeUnitItem.setValue(rule.getEntryTimeUnit());
            entryOperatorItem.setValue(entryOperator);
            entryValueItem.setValue(rule.getEntryValue());
            entryValueUnitItem.setValue(rule.getEntryUnit());
            
            entryConditionItemsForm.setFields(entryTimeItem, entryTimeUnitItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
           
            if (!rule.isEnterEqualsExitCondition()) {
                setExitOperatorItem();
                setExitTimeItem();
                setExitTimeUnitItem();
                
                exitOperatorItem.setValue(exitOperator);
                exitTimeItem.setValue(rule.getExitTime());
                exitTimeUnitItem.setValue(rule.getExitTimeUnit());
                
                exitConditionItemsForm.setFields(exitTimeItem, exitTimeUnitItem, exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(TENDENCY_OVER_COUNT)) {
            setCountItem();
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            
            this.countItem.setValue(rule.getEntryCount());
            this.entryOperatorItem.setValue(entryOperator);
            this.entryValueItem.setValue(rule.getEntryValue());
            this.entryValueUnitItem.setValue(rule.getEntryUnit());
            
            this.entryConditionItemsForm.setFields(countItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
            
            if (!rule.isEnterEqualsExitCondition()) {
                setExitOperatorItem();
                setCountConditionItem();
                
                exitOperatorItem.setValue(exitOperator);
                countConditionItem.setValue(rule.getEntryCount());
                
                exitConditionItemsForm.setFields(countConditionItem, exitOperatorItem, entryValueConditionItem, entryValueUnitConditionItem);
            }
        } else if (rule.getRuleType().equals(SUM_OVER_TIME)) {
            setEntryOperatorRuleItem();
            setEntryValueItem();
            setEntryValueUnitItem();
            setCountItem();
            
            entryOperatorItem.setValue(entryOperator);
            entryValueItem.setValue(rule.getEntryValue());
            entryValueUnitItem.setValue(rule.getEntryUnit());
            countItem.setValue(rule.getEntryTime());
            
            entryConditionItemsForm.setFields(countItem, entryOperatorItem, entryValueItem, entryValueUnitItem);
        } else if (rule.getRuleType().equals(SENSOR_LOSS)) {
            setEntryTimeItem();
            setEntryTimeUnitItem();
            
            entryTimeItem.setValue(rule.getEntryTime());
            entryTimeUnitItem.setValue(rule.getEntryTimeUnit());
            
            entryConditionItemsForm.setFields(entryTimeItem, entryTimeUnitItem);
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

        // set time units
        this.timeUnitHashMap.put("S", "S");
        this.timeUnitHashMap.put("M", "M");
        this.timeUnitHashMap.put("H", "H");
    }

    private void setEntryValueItem() {
        this.entryValueItem = new TextItem();
        this.entryValueItem.setWidth(this.entryItemWidth);
        this.entryValueItem.setTitle(i18n.value());
        this.entryValueItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryValueItem.setKeyPressFilter("[0-9]");
    }

    private void setEntryValueUnitItem() {
        this.entryValueUnitItem = new SelectItem();
        this.entryValueUnitItem.setWidth(this.entryItemWidth);
        this.entryValueUnitItem.setTitle(i18n.unit());
        this.entryValueUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryValueUnitItem.setValueMap(this.unitHashMap);
        this.entryValueUnitItem.setTextAlign(Alignment.CENTER);
        
        ArrayList<String> list = new ArrayList<String>(this.unitHashMap.values());
        if (list.size() != 0) {
            this.entryValueUnitItem.setDefaultValue(list.get(0));
        }
    }

    private void setCountItem() {
        this.countItem = new TextItem();
        this.countItem.setWidth(this.entryItemWidth);
        this.countItem.setTitle(i18n.count());
        this.countItem.setTitleOrientation(TitleOrientation.TOP);
        this.countItem.setKeyPressFilter("[0-9]");
    }

    private void setEntryTimeItem() {
        this.entryTimeItem = new TextItem();
        this.entryTimeItem.setWidth(this.entryItemWidth);
        this.entryTimeItem.setTitle("<nobr>" + i18n.timeValue() + "</nobr>");
        this.entryTimeItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryTimeItem.setKeyPressFilter("[0-9]");
    }

    private void setEntryTimeUnitItem() {
        this.entryTimeUnitItem = new SelectItem();
        this.entryTimeUnitItem.setWidth(this.entryItemWidth);
        this.entryTimeUnitItem.setTitle(i18n.timeUnit());
        this.entryTimeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryTimeUnitItem.setTooltip("<nobr>" + i18n.unitsTime() + "</nobr>");
        this.entryTimeUnitItem.setValueMap(this.timeUnitHashMap);
        this.entryTimeUnitItem.setDefaultValue("H");
        this.entryTimeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setEntryValueConditionItem() {
        this.entryValueConditionItem = new TextItem();
        this.entryValueConditionItem.setWidth(this.entryItemWidth);
        this.entryValueConditionItem.setTitle(i18n.value());
        this.entryValueConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryValueConditionItem.setKeyPressFilter("[0-9]");
    }

    private void setEntryValueUnitConditionItem() {
        this.entryValueUnitConditionItem = new SelectItem();
        this.entryValueUnitConditionItem.setWidth(this.entryItemWidth);
        this.entryValueUnitConditionItem.setTitle(i18n.unit());
        this.entryValueUnitConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryValueUnitConditionItem.setValueMap(this.unitHashMap);
        this.entryValueUnitConditionItem.setTextAlign(Alignment.CENTER);
        
        ArrayList<String> list = new ArrayList<String>(this.unitHashMap.values());
        if (list.size() != 0) {
            this.entryValueUnitConditionItem.setDefaultValue(list.get(0));
        }
    }

    private void setCountConditionItem() {
        this.countConditionItem = new TextItem();
        this.countConditionItem.setWidth(this.entryItemWidth);
        this.countConditionItem.setTitle(i18n.count());
        this.countConditionItem.setTitleOrientation(TitleOrientation.TOP);
        this.countConditionItem.setKeyPressFilter("[0-9]");
    }

    private void setExitTimeItem() {
        this.exitTimeItem = new TextItem();
        this.exitTimeItem.setWidth(this.entryItemWidth);
        this.exitTimeItem.setTitle("<nobr>" + i18n.timeValue() + "</nobr>");
        this.exitTimeItem.setTitleOrientation(TitleOrientation.TOP);
        this.exitTimeItem.setKeyPressFilter("[0-9]");
    }

    private void setExitTimeUnitItem() {
        this.exitTimeUnitItem = new SelectItem();
        this.exitTimeUnitItem.setWidth(this.entryItemWidth);
        this.exitTimeUnitItem.setTitle(i18n.timeUnit());
        this.exitTimeUnitItem.setTitleOrientation(TitleOrientation.TOP);
        this.exitTimeUnitItem.setTooltip("<nobr>" + i18n.unitsTime() + "</nobr>");
        this.exitTimeUnitItem.setValueMap(this.timeUnitHashMap);
        this.exitTimeUnitItem.setDefaultValue("H");
        this.exitTimeUnitItem.setTextAlign(Alignment.CENTER);
    }

    private void setEntryOperatorRuleItem() {
        this.entryOperatorItem = new SelectItem();
        this.entryOperatorItem.setWidth(this.entryItemWidth);
        this.entryOperatorItem.setTitle(i18n.operator());
        this.entryOperatorItem.setTitleOrientation(TitleOrientation.TOP);
        this.entryOperatorItem.setValueMap(getMathSymbols());
        this.entryOperatorItem.setDefaultValue(">");
        this.entryOperatorItem.setTextAlign(Alignment.CENTER);
        this.entryOperatorItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                if (exitOperatorItem != null) {
                    TextItem exitOperatorTextItem = (TextItem) event.getSource();
                    exitOperatorItem.setValue(getInverse(exitOperatorTextItem.getValueAsString()));
                }
            }
        });
    }

    private void setExitOperatorItem() {
        this.exitOperatorItem = new SelectItem();
        this.exitOperatorItem.setWidth(this.entryItemWidth);
        this.exitOperatorItem.setTitle(i18n.operator());
        this.exitOperatorItem.setTitleOrientation(TitleOrientation.TOP);
        this.exitOperatorItem.setValueMap(getMathSymbols());
        this.exitOperatorItem.setTextAlign(Alignment.CENTER);
        
        String entryOperator = entryOperatorItem.getValueAsString();
        this.exitOperatorItem.setDefaultValue(getInverse(entryOperator));
    }
    
    public void setUnit(ArrayList<String> units){
        for (int i = 0; i < units.size(); i++) {
            this.unitHashMap.put(units.get(i), units.get(i));
        }
    }
    
    
}