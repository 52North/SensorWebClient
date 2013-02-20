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
import static org.n52.client.ses.ui.RuleRecord.UUID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ROLE;

import java.util.ArrayList;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.data.RuleDataSource;
import org.n52.client.ses.event.CopyEvent;
import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.EditRuleEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.PublishRuleEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.ses.ui.RuleRecord;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

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
public class EditRulesLayout extends FormLayout {

    private static final String EDIT_RULES_DELETE = "editRulesDelete";

    private static final String EDIT_RULES_EDIT = "editRulesEdit";

    private static final String EDIT_RULES_DESCRIPTION = "editRulesDescription";

    private static final String EDIT_RULES_NAME = "editRulesName";

    private static final String EDIT_RULES_TYPE = "editRulesType";

    private static final String EDIT_RULES_COPY = "editRulesCopy";

    private static final String EDIT_RULES_PUBLISHED = "editRulesPublished";

    /** The rules grid. */
    private ListGrid ownRulesGrid;
    
    /** The other rules grid. */
    private ListGrid otherRulesGrid;
    
    private RuleDataSource ownDataSource;
    private RuleDataSource otherDataSource;
    
    private boolean firstOwn = true;
    private boolean firstOther = true;

    /**
     * Instantiates a new edits the rules layout.
     */
    public EditRulesLayout() {
        super(i18n.editRules());
        
        // init DataSource
        this.ownDataSource = new RuleDataSource();
        this.otherDataSource = new RuleDataSource();
        
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

                    if (fieldName.equals(EDIT_RULES_EDIT)) {
                        // subscribe button
                        IButton editButton = new IButton(i18n.edit());
                        editButton.setShowDown(false);
                        editButton.setShowRollOver(false);
                        editButton.setLayoutAlign(Alignment.CENTER);
                        editButton.setPrompt(i18n.editThisRule());
                        editButton.setHeight(16);
                        editButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(1));
                                EventBus.getMainEventBus().fireEvent(new EditRuleEvent(record.getAttribute("name")));
                            }
                        });

                        return editButton;

                    } else if (fieldName.equals(EDIT_RULES_PUBLISHED)) {
                        // publish button
                        IButton publishButton = new IButton(i18n.publishButton());
                        publishButton.setShowDown(false);
                        publishButton.setShowRollOver(false);
                        publishButton.setLayoutAlign(Alignment.CENTER);
                        publishButton.setHeight(16);
                        publishButton.setAutoFit(true);

                        boolean published = record.getAttributeAsBoolean("published");
                        if (published) {
                            publishButton.setTitle(i18n.unpublishButton());
                            publishButton.setPrompt(i18n.cancelPublication());
                        } else {
                            publishButton.setTitle(i18n.publishButton());
                            publishButton.setPrompt(i18n.publishThisRule());
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
                        IButton deleteButton = new IButton(i18n.delete());
                        deleteButton.setShowDown(false);
                        deleteButton.setShowRollOver(false);
                        deleteButton.setLayoutAlign(Alignment.CENTER);
                        deleteButton.setPrompt(i18n.deleteThisRule());
                        deleteButton.setHeight(16);
                        deleteButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                SC.ask(i18n.reallyDeleteRule(), new BooleanCallback() {
                                    public void execute(Boolean value) {
                                        if (value) {
                                            EventBus.getMainEventBus().fireEvent(new DeleteRuleEvent(record.getAttribute(UUID), Cookies.getCookie(COOKIE_USER_ROLE)));
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

                   if (fieldName.equals(EDIT_RULES_COPY)) {
                        // Copy button
                        IButton copyButton = new IButton(i18n.copy());
                        copyButton.setShowDown(false);
                        copyButton.setShowRollOver(false);
                        copyButton.setHeight(17);
                        copyButton.setLayoutAlign(Alignment.CENTER);
                        copyButton.setAlign(Alignment.CENTER);
                        copyButton.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                System.out.println("copy " + record.getAttribute("name"));
                                String userID = Cookies.getCookie(COOKIE_USER_ID);
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
        ListGridField typeField = new ListGridField(EDIT_RULES_TYPE, i18n.type());
        typeField.setWidth(60);
        typeField.setAlign(Alignment.CENTER);

        ListGridField nameField = new ListGridField(EDIT_RULES_NAME, i18n.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField descriptionField = new ListGridField(EDIT_RULES_DESCRIPTION, i18n.description());
        descriptionField.setAlign(Alignment.CENTER);

        ListGridField editField = new ListGridField(EDIT_RULES_EDIT, i18n.edit());
        editField.setWidth(110);
        editField.setCanFilter(false);
        editField.setAlign(Alignment.CENTER);

        ListGridField publishField = new ListGridField(EDIT_RULES_PUBLISHED, i18n.publishButton());
        publishField.setWidth(130);
        publishField.setCanFilter(false);
        publishField.setAlign(Alignment.CENTER);

        ListGridField deleteField = new ListGridField(EDIT_RULES_DELETE, i18n.delete());
        deleteField.setWidth(110);
        deleteField.setCanFilter(false);
        deleteField.setAlign(Alignment.CENTER);
        
        ListGridField copyField = new ListGridField(EDIT_RULES_COPY, i18n.copy());
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
            rule = new RuleRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), i18n.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());
            
            this.ownRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), i18n.sms(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");

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
            rule = new RuleRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), i18n.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());

            this.otherRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), i18n.sms(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");

            this.otherRulesGrid.addData(rule);
        }
        
        this.firstOther = false;
        this.otherRulesGrid.fetchData();
    }
}