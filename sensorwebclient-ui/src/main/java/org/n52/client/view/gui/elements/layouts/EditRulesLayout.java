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

import org.n52.client.control.I18N;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.ses.CopyEvent;
import org.n52.client.eventBus.events.ses.DeleteRuleEvent;
import org.n52.client.eventBus.events.ses.EditRuleEvent;
import org.n52.client.eventBus.events.ses.GetAllPublishedRulesEvent;
import org.n52.client.eventBus.events.ses.PublishRuleEvent;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.model.data.representations.RuleRecord;
import org.n52.client.view.gui.elements.interfaces.Layout;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;
import org.n52.shared.serializable.pojos.RuleDS;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class EditRulesLayout.
 * 
 * View with two tables. The first contains rules of the loged in user.
 * The user can edit or delete all rules.
 * The second table contains all published rules of other users. Here
 * the user has the possibility to copy rules to his own stock.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class EditRulesLayout extends Layout {

    /** The rules grid. */
    private ListGrid ownRulesGrid;
    
    /** The other rules grid. */
    private ListGrid otherRulesGrid;
    
    private RuleDS ownDataSource;
    private RuleDS otherDataSource;
    
    private boolean firstOwn = true;
    private boolean firstOther = true;

    /**
     * Instantiates a new edits the rules layout.
     */
    public EditRulesLayout() {
        super(I18N.sesClient.editRules());
        
        // init DataSource
        this.ownDataSource = new RuleDS();
        this.otherDataSource = new RuleDS();
        
        init();
    }

    /**
     * Inits the layout.
     */
    private void init() {

        this.ownRulesGrid = new ListGrid() {
            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

                if (record != null) {
                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals("edit")) {
                        // subscribe button
                        IButton editButton = new IButton(I18N.sesClient.edit());
                        editButton.setShowDown(false);
                        editButton.setShowRollOver(false);
                        editButton.setLayoutAlign(Alignment.CENTER);
                        editButton.setPrompt(I18N.sesClient.editThisRule());
                        editButton.setHeight(16);
                        editButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(1));
                                EventBus.getMainEventBus().fireEvent(new EditRuleEvent(record.getAttribute("name")));
                            }
                        });

                        return editButton;

                    } else if (fieldName.equals("published")) {
                        // publish button
                        IButton publishButton = new IButton(I18N.sesClient.publishButton());
                        publishButton.setShowDown(false);
                        publishButton.setShowRollOver(false);
                        publishButton.setLayoutAlign(Alignment.CENTER);
                        publishButton.setHeight(16);
                        publishButton.setAutoFit(true);

                        boolean published = record.getAttributeAsBoolean("published");
                        if (published) {
                            publishButton.setTitle(I18N.sesClient.unpublishButton());
                            publishButton.setPrompt(I18N.sesClient.cancelPublication());
                        } else {
                            publishButton.setTitle(I18N.sesClient.publishButton());
                            publishButton.setPrompt(I18N.sesClient.publishThisRule());
                        }

                        publishButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                String ruleName = record.getAttribute("name");
                                boolean value = record.getAttributeAsBoolean("published");
                                
                                EventBus.getMainEventBus().fireEvent(new PublishRuleEvent(ruleName, !value, "USER"));
                            }
                        });

                        return publishButton;

                    } else if (fieldName.equals("delete")) {
                        // delete button
                        IButton deleteButton = new IButton(I18N.sesClient.delete());
                        deleteButton.setShowDown(false);
                        deleteButton.setShowRollOver(false);
                        deleteButton.setLayoutAlign(Alignment.CENTER);
                        deleteButton.setPrompt(I18N.sesClient.deleteThisRule());
                        deleteButton.setHeight(16);
                        deleteButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                SC.ask(I18N.sesClient.reallyDeleteRule(), new BooleanCallback() {
                                    public void execute(Boolean value) {
                                        if (value) {
                                            EventBus.getMainEventBus().fireEvent(new DeleteRuleEvent(record.getAttribute("name"), Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)));
                                        }
                                    }
                                });
                            }
                        });
                        return deleteButton;
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
                    String fieldName = this.getFieldName(colNum);

                   if (fieldName.equals("copy")) {
                        // Copy button
                        IButton copyButton = new IButton(I18N.sesClient.copy());
                        copyButton.setShowDown(false);
                        copyButton.setShowRollOver(false);
                        copyButton.setHeight(17);
                        copyButton.setLayoutAlign(Alignment.CENTER);
                        copyButton.setAlign(Alignment.CENTER);
                        copyButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                System.out.println("copy " + record.getAttribute("name"));
                                String userID = Cookies.getCookie(SesRequestManager.COOKIE_USER_ID);
                                EventBus.getMainEventBus().fireEvent(new CopyEvent(userID, record.getAttribute("name")));
                            }
                        });
                        return copyButton;
                    }
                return null;
                }
                return null;
            }
        };

        // grid config
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

        // grid fields
        ListGridField typeField = new ListGridField("type", I18N.sesClient.type());
        typeField.setWidth(60);
        typeField.setAlign(Alignment.CENTER);

        ListGridField nameField = new ListGridField("name", I18N.sesClient.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField descriptionField = new ListGridField("description", I18N.sesClient.description());
        descriptionField.setAlign(Alignment.CENTER);

        ListGridField editField = new ListGridField("edit", I18N.sesClient.edit());
        editField.setWidth(110);
        editField.setCanFilter(false);
        editField.setAlign(Alignment.CENTER);

        ListGridField publishField = new ListGridField("published", I18N.sesClient.publishButton());
        publishField.setWidth(130);
        publishField.setCanFilter(false);
        publishField.setAlign(Alignment.CENTER);

        ListGridField deleteField = new ListGridField("delete", I18N.sesClient.delete());
        deleteField.setWidth(110);
        deleteField.setCanFilter(false);
        deleteField.setAlign(Alignment.CENTER);
        
        ListGridField copyField = new ListGridField("copy", I18N.sesClient.copy());
        copyField.setWidth(110);
        copyField.setCanFilter(false);
        copyField.setAlign(Alignment.CENTER);

        // fill grid with rows and data
        this.ownRulesGrid.setFields(typeField, nameField, descriptionField, editField, publishField, deleteField);
        this.ownRulesGrid.setCanResizeFields(false);
        
        this.otherRulesGrid.setFields(typeField, nameField, descriptionField, copyField);
        this.otherRulesGrid.setCanResizeFields(false);

        this.form.setFields(this.headerItem);
        
        DynamicForm headerForm1 = new DynamicForm();
        HeaderItem header1 = new HeaderItem();
        header1.setDefaultValue(I18N.sesClient.ownRules());
        headerForm1.setItems(header1);

        DynamicForm headerForm2 = new DynamicForm();
        HeaderItem header2 = new HeaderItem();
        header2.setDefaultValue(I18N.sesClient.otherRules());
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

    /**
     * Sets the data.
     * @param basic 
     * @param complex 
     */
    public void setOwnData(ArrayList<BasicRuleDTO> basic, ArrayList<ComplexRuleDTO> complex) {
        RuleRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOwn) {
            this.ownRulesGrid.selectAllRecords();
            this.ownRulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basic.size(); i++) {
            ruleDTO = basic.get(i);
            rule = new RuleRecord(I18N.sesClient.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), I18N.sesClient.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed());
            
            this.ownRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleRecord(I18N.sesClient.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), I18N.sesClient.sms(), complexDTO.isRelease(), complexDTO.isSubscribed());

            this.ownRulesGrid.addData(rule);
        }
        
        this.firstOwn = false;
        this.ownRulesGrid.fetchData();
    }
    
    /**
     * Fill other user rules table
     * 
     * @param basic
     * @param complex
     */
    public void setOtherData(ArrayList<BasicRuleDTO> basic, ArrayList<ComplexRuleDTO> complex) {
        RuleRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOther) {
            this.otherRulesGrid.selectAllRecords();
            this.otherRulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basic.size(); i++) {
            ruleDTO = basic.get(i);
            rule = new RuleRecord(I18N.sesClient.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), I18N.sesClient.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed());

            this.otherRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleRecord(I18N.sesClient.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), I18N.sesClient.sms(), complexDTO.isRelease(), complexDTO.isSubscribed());

            this.otherRulesGrid.addData(rule);
        }
        
        this.firstOther = false;
        this.otherRulesGrid.fetchData();
    }
}