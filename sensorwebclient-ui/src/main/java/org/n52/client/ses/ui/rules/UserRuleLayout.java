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

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.FORMAT;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.MEDIUM;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.UUID;
import static org.n52.client.util.ClientSessionManager.currentSession;

import java.util.ArrayList;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.DataControlsSes;
import org.n52.client.ses.event.SubscribeEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.util.ClientSessionManager;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class UserRuleLayout.
 * 
 * This layout shows two tables. First table lists all rules of the current
 * logedin user. The second one shows all published rules from other users. 
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class UserRuleLayout extends FormLayout {

    /** The own rules grid. */
    private ListGrid ownRulesGrid;

    /** The other rules grid. */
    private ListGrid otherRulesGrid;
    
    private RuleDataSource ownDataSource;
    private RuleDataSource otherDataSource;
    
    private boolean firstOwn = true;
    private boolean firstOther = true;
    
    /**
     * Instantiates a new user rule layout.
     */
    public UserRuleLayout() {
        super(i18n.subscribeRules());
        
        // init DataSource 
        this.ownDataSource = new RuleDataSource();
        this.otherDataSource = new RuleDataSource();
        
        init();
    }

    private void init() {

        this.ownRulesGrid = new ListGrid() {
            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
                if (record != null) {

                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals("medium")) {
                        DynamicForm dynamic = new DynamicForm();
                        dynamic.setWidth(90);
                        
                        final SelectItem selectMediumItem = new SelectItem();
                        selectMediumItem.setMultiple(true);
                        selectMediumItem.setShowTitle(false);
                        selectMediumItem.setWidth(90);
                        selectMediumItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
                        
                        selectMediumItem.setValueMap(DataControlsSes.getAvailableWNSMedia());
                        selectMediumItem.setDefaultValue(DataControlsSes.getDefaultMedium());
                        
                        selectMediumItem.addChangedHandler(new ChangedHandler() {
                            public void onChanged(ChangedEvent event) {
                                String[] values = selectMediumItem.getValues();
                                String medium = "";
                                for (int i = 0; i < values.length; i++) {
                                    medium = medium + values[i];
                                    if (i < values.length) {
                                        medium = medium + "_";
                                    }
                                }
                                record.setAttribute(RuleDataSourceRecord.MEDIUM, medium);
                                
                                UserRuleLayout.warnForLongSmsMessages(record);
                            }
                        });
                        
                        dynamic.setFields(selectMediumItem);
                        return dynamic;

                    } else if (fieldName.equals("format")) {
                        DynamicForm dynamic = new DynamicForm();
                        dynamic.setWidth(90);

                        final SelectItem selectFormatItem = new SelectItem();
                        selectFormatItem.setMultiple(true);
                        selectFormatItem.setShowTitle(false);
                        selectFormatItem.setWidth(90);
                        selectFormatItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
                       
                        selectFormatItem.setValueMap(DataControlsSes.getAvailableFormats());
                        selectFormatItem.setDefaultValue(DataControlsSes.getDefaultFormat());
                        
                        selectFormatItem.addChangedHandler(new ChangedHandler() {
                            public void onChanged(ChangedEvent event) {
                                String[] values = selectFormatItem.getValues();
                                String format = "";
                                for (int i = 0; i < values.length; i++) {
                                    format = format + values[i];
                                    if (i < values.length) {
                                        format = format + "_";
                                    }
                                }
                                record.setAttribute(FORMAT, format);
                                UserRuleLayout.warnForLongSmsMessages(record);
                            }
                        });
                        
                        dynamic.setFields(selectFormatItem);
                        return dynamic;

                    } else if (fieldName.equals("subscribe")) {
                        // subscribe button
                        IButton subscribeButton = new IButton(i18n.subscribe());
                        subscribeButton.setShowDown(false);
                        subscribeButton.setShowRollOver(false);
                        subscribeButton.setHeight(16);
                        subscribeButton.setAutoFit(true);
                        subscribeButton.setLayoutAlign(Alignment.CENTER);
                        subscribeButton.setAlign(Alignment.CENTER);
                        subscribeButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                String medium = record.getAttribute(MEDIUM);
                                if (medium.equals("")){
                                    SC.say(i18n.selectMedium());
                                }
                                else {
                                    String format = record.getAttribute(FORMAT);
                                    if (format.equals("")){
                                        SC.say(i18n.selectFormat());
                                    } else {
                                        String uuid = record.getAttribute(UUID);
                                        SubscribeEvent subscribeEvent = new SubscribeEvent(currentSession(), uuid, medium, format);
                                        EventBus.getMainEventBus().fireEvent(subscribeEvent);
                                    }
                                }
                            }
                        });
                        subscribeButton.setTitle(i18n.subscribe());
                        subscribeButton.setPrompt(i18n.subscribeThisRule());
                        
                        return subscribeButton;
                    } else {
                        return null;
                    }
                }
                return null;
            }
        };

        this.otherRulesGrid = new ListGrid() {
            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
                if (record != null) {
                    final RuleDataSourceRecord ruleRecord = (RuleDataSourceRecord) record;
                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals("medium")) {
                        DynamicForm dynamic = new DynamicForm();
                        dynamic.setWidth(90);
                        
                        final SelectItem selectMediumItem = new SelectItem();
                        selectMediumItem.setMultiple(true);
                        selectMediumItem.setShowTitle(false);
                        selectMediumItem.setWidth(90);
                        selectMediumItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
                        
                        selectMediumItem.setValueMap(DataControlsSes.getAvailableWNSMedia());
                        selectMediumItem.setDefaultValue(DataControlsSes.getDefaultMedium());
                        
                        selectMediumItem.addChangedHandler(new ChangedHandler() {
                            public void onChanged(ChangedEvent event) {
                                String[] values = selectMediumItem.getValues();
                                String medium = "";
                                for (int i = 0; i < values.length; i++) {
                                    medium = medium + values[i];
                                    if (i < values.length) {
                                        medium = medium + "_";
                                    }
                                }
                                ruleRecord.setMedium(medium);

                                if (DataControlsSes.warnUserLongNotification) {
                                    if (ruleRecord.getMedium().contains("SMS")) {
                                        if (ruleRecord.getFormat().contains("XML") || ruleRecord.getFormat().contains("EML")) {
                                            SC.say(i18n.longNotificationMessage());
                                            return;
                                        }
                                    }
                                }
                            }
                        });
                        
                        dynamic.setFields(selectMediumItem);
                        return dynamic;

                    } else if (fieldName.equals("format")) {
                        DynamicForm dynamic = new DynamicForm();
                        dynamic.setWidth(90);
                        
                        final SelectItem selectFormatItem = new SelectItem();
                        selectFormatItem.setMultiple(true);
                        selectFormatItem.setShowTitle(false);
                        selectFormatItem.setWidth(90);
                        selectFormatItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
                        
                        selectFormatItem.setValueMap(DataControlsSes.getAvailableFormats());
                        selectFormatItem.setDefaultValue(DataControlsSes.getDefaultFormat());
                        
                        selectFormatItem.addChangedHandler(new ChangedHandler() {
                            public void onChanged(ChangedEvent event) {
                                String[] values = selectFormatItem.getValues();
                                String format = "";
                                for (int i = 0; i < values.length; i++) {
                                    format = format + values[i];
                                    if (i < values.length) {
                                        format = format + "_";
                                    }
                                }
                                ruleRecord.setFormat(format);
                                
                                if (DataControlsSes.warnUserLongNotification) {
                                    if (ruleRecord.getMedium().contains("SMS")) {
                                        if (ruleRecord.getFormat().contains("XML") || ruleRecord.getFormat().contains("EML")) {
                                            SC.say(i18n.longNotificationMessage());
                                            return;
                                        }
                                    }
                                }
                            }
                        });
                        
                        dynamic.setFields(selectFormatItem);
                        return dynamic;

                    } else if (fieldName.equals("subscribe")) {
                        // subscribe button
                        IButton subscribeButton = new IButton(i18n.subscribe());
                        subscribeButton.setShowDown(false);
                        subscribeButton.setShowRollOver(false);
                        subscribeButton.setHeight(19);
                        subscribeButton.setAutoFit(true);
                        subscribeButton.setLayoutAlign(Alignment.CENTER);
                        subscribeButton.setAlign(Alignment.CENTER);
                        subscribeButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                String medium = ruleRecord.getMedium();
                                if (medium.equals("")){
                                    SC.say(i18n.selectMedium());
                                }
                                else {
                                    String format = ruleRecord.getFormat();
                                    if (format.equals("")){
                                        SC.say(i18n.selectFormat());
                                    } else {
                                        String uuid = ruleRecord.getUuid();
                                        SubscribeEvent subscribeEvent = new SubscribeEvent(currentSession(), uuid, medium, format);
                                        EventBus.getMainEventBus().fireEvent(subscribeEvent);
                                    }
                                }
                            }
                        });

                        subscribeButton.setTitle(i18n.subscribe());
                        subscribeButton.setPrompt(i18n.subscribeThisRule());

                        return subscribeButton;
                    } else {
                        return null;
                    }
                }
                return null;
            }
        };

        // own rules grid config
        this.ownRulesGrid.setShowRecordComponents(true);
        this.ownRulesGrid.setShowRecordComponentsByCell(true);
        this.ownRulesGrid.setWidth100();
        this.ownRulesGrid.setHeight100();
        this.ownRulesGrid.setCanGroupBy(false);
        this.ownRulesGrid.setDataSource(this.ownDataSource);
        this.ownRulesGrid.setAutoFetchData(true);
        this.ownRulesGrid.setShowFilterEditor(true);
        this.ownRulesGrid.setFilterOnKeypress(true);
        this.ownRulesGrid.setShowRollOver(false);
        this.ownRulesGrid.sort(1, SortDirection.ASCENDING);

        // other rules grid config
        this.otherRulesGrid.setShowRecordComponents(true);
        this.otherRulesGrid.setShowRecordComponentsByCell(true);
        this.otherRulesGrid.setWidth100();
        this.otherRulesGrid.setHeight100();
        this.otherRulesGrid.setCanGroupBy(false);
        this.otherRulesGrid.setDataSource(this.otherDataSource);
        this.otherRulesGrid.setAutoFetchData(true);
        this.otherRulesGrid.setShowFilterEditor(true);
        this.otherRulesGrid.setFilterOnKeypress(true);
        this.otherRulesGrid.setShowRollOver(false);
        this.otherRulesGrid.sort(1, SortDirection.ASCENDING);

        // fields of tables
        ListGridField typeField = new ListGridField("type", i18n.type());
        typeField.setWidth(60);
        typeField.setAlign(Alignment.CENTER);

        ListGridField nameField = new ListGridField("name", i18n.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField descriptionField = new ListGridField("description", i18n.description());
        descriptionField.setAlign(Alignment.CENTER);

        ListGridField mediumField = new ListGridField("medium", i18n.medium());
        mediumField.setWidth(90);
        mediumField.setAlign(Alignment.CENTER);
        mediumField.setCanFilter(false);

        ListGridField formatField = new ListGridField("format", "Format");
        formatField.setWidth(120);
        formatField.setAlign(Alignment.CENTER);
        formatField.setCanFilter(false);

        ListGridField subscribeField = new ListGridField("subscribe", i18n.subscribe());
        subscribeField.setWidth(150);
        subscribeField.setAlign(Alignment.CENTER);
        subscribeField.setCanFilter(false);

        // set Fields
        this.ownRulesGrid.setFields(typeField, nameField, descriptionField, mediumField, formatField, subscribeField);
        this.ownRulesGrid.setCanResizeFields(false);

        this.otherRulesGrid.setFields(typeField, nameField, descriptionField, mediumField, formatField, subscribeField);
        this.otherRulesGrid.setCanResizeFields(false);

        this.form.setFields(this.headerItem);

        // table header
        DynamicForm headerForm1 = new DynamicForm();
        HeaderItem header1 = new HeaderItem();
        header1.setDefaultValue(i18n.ownRules());
        headerForm1.setItems(header1);

        DynamicForm headerForm2 = new DynamicForm();
        HeaderItem header2 = new HeaderItem();
        header2.setDefaultValue(i18n.otherRules());
        headerForm2.setItems(header2);

        // add to mainLayout
        addMember(this.form);
        addMember(this.spacer);
        addMember(headerForm1);
        addMember(this.ownRulesGrid);
        addMember(this.spacer);
        addMember(headerForm2);
        addMember(this.otherRulesGrid);
    }

    private static void warnForLongSmsMessages(ListGridRecord record) {
        if (DataControlsSes.warnUserLongNotification) {
            if (record.getAttribute(MEDIUM).contains("SMS")) {
                String format = record.getAttribute(FORMAT);
                if (format.contains("XML") || format.contains("EML")) {
                    SC.say(i18n.longNotificationMessage());
                    return;
                }
            }
        }
    }

    /**
     * Fills the table with own rules.
     * 
     * @param basicRules 
     * @param complexRules 
     */
    public void setDataOwnRules(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        RuleDataSourceRecord rule;
        BasicRuleDTO basicDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOwn) {
            this.ownRulesGrid.selectAllRecords();
            this.ownRulesGrid.removeSelectedData(); 
        }
        
        
        for (int i = 0; i < basicRules.size(); i++) {
            basicDTO = basicRules.get(i);
            rule = new RuleDataSourceRecord(i18n.basic(), "", "", basicDTO.getName(), basicDTO.getDescription(), DataControlsSes.getDefaultMedium(), DataControlsSes.getDefaultFormat(), basicDTO.isRelease(), basicDTO.isSubscribed(), basicDTO.getUuid());
            
            this.ownRulesGrid.addData(rule);
        }
        
        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleDataSourceRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), DataControlsSes.getDefaultMedium(), DataControlsSes.getDefaultFormat(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");
            
            this.ownRulesGrid.addData(rule);
        }
        
        this.ownRulesGrid.fetchData();
        this.firstOwn = false;
    }

    /**
     * Fill table with other rules.
     * 
     * @param basicRules 
     * @param complexRules 
     */
    public void setDataOtherRules(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        RuleDataSourceRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOther) {
            this.otherRulesGrid.selectAllRecords();
            this.otherRulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basicRules.size(); i++) {
            ruleDTO = basicRules.get(i);
            rule = new RuleDataSourceRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), DataControlsSes.getDefaultMedium(), DataControlsSes.getDefaultFormat(), ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());
            
            this.otherRulesGrid.addData(rule);
        }
        
        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleDataSourceRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), DataControlsSes.getDefaultMedium(), DataControlsSes.getDefaultFormat(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");

            this.otherRulesGrid.addData(rule);
            
        }
        
        this.otherRulesGrid.fetchData();
        this.firstOther = false;
    }
}