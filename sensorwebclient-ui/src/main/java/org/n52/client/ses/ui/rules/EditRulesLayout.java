/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ses.ui.rules;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.ArrayList;

import org.n52.client.ses.ui.FormLayout;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class EditRulesLayout extends FormLayout {
    
    private static final String EDIT_RULES_DELETE = "editRulesDelete";

    static final String EDIT_RULES_EDIT = "editRulesEdit";

    private static final String EDIT_RULES_DESCRIPTION = "editRulesDescription";

    private static final String EDIT_RULES_NAME = "editRulesName";

    private static final String EDIT_RULES_TYPE = "editRulesType";

    public static final String EDIT_RULES_COPY = "editRulesCopy";

    static final String EDIT_RULES_PUBLISHED = "editRulesPublished";

    private ListGrid ownRulesGrid;
    
    private ListGrid otherRulesGrid;
    
    private boolean firstOwn = true;
    private boolean firstOther = true;

    public EditRulesLayout() {
        super(i18n.editRules());
        
        // init DataSource
        
        init();
    }

    private void init() {

        this.ownRulesGrid = new OwnRulesListGrid();
        this.otherRulesGrid = new OtherUserRulesListGrid();


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
        ListGridField[] ownRulesFields = new ListGridField[] { typeField, nameField, descriptionField, editField, publishField, deleteField };
        this.ownRulesGrid.setFields(ownRulesFields);
        this.ownRulesGrid.setCanResizeFields(false);
        
        ListGridField[] otherRulesfields = new ListGridField[] { typeField, nameField, descriptionField, copyField };
        this.otherRulesGrid.setDefaultFields(otherRulesfields);
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

    public void setOwnData(ArrayList<BasicRuleDTO> basic, ArrayList<ComplexRuleDTO> complex) {
        RuleDataSourceRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOwn) {
            this.ownRulesGrid.selectAllRecords();
            this.ownRulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basic.size(); i++) {
            ruleDTO = basic.get(i);
            rule = new RuleDataSourceRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), i18n.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());
            
            this.ownRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleDataSourceRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), i18n.sms(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");

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
        RuleDataSourceRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.firstOther) {
            this.otherRulesGrid.selectAllRecords();
            this.otherRulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basic.size(); i++) {
            ruleDTO = basic.get(i);
            rule = new RuleDataSourceRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), i18n.sms(), "XML", ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());

            this.otherRulesGrid.addData(rule);
        }
        for (int i = 0; i < complex.size(); i++) {
            complexDTO = complex.get(i);
            rule = new RuleDataSourceRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), i18n.sms(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");

            this.otherRulesGrid.addData(rule);
        }
        
        this.firstOther = false;
        this.otherRulesGrid.fetchData();
    }
}