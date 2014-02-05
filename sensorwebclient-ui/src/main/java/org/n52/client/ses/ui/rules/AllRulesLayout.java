/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import static com.smartgwt.client.types.Alignment.CENTER;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.ArrayList;

import org.n52.client.ses.ui.FormLayout;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;

public class AllRulesLayout extends FormLayout {

    private AllRulesListGrid rulesGrid;
    
    private boolean first = true;
    
    public AllRulesLayout() {
        super(i18n.allRules());
        rulesGrid = createAllRulesListGrid();
        form.setFields(headerItem);
        addMember(form);
        addMember(rulesGrid);
    }

    private AllRulesListGrid createAllRulesListGrid() {
        AllRulesListGrid grid = new AllRulesListGrid();
        ListGridField nameField = createRuleNameField(grid);
        ListGridField editField = createEditRuleField(grid);
        ListGridField typeField = createRuleTypeField(grid);
        ListGridField ownerField = createRuleOwnerField(grid);
        ListGridField deleteField = createDeleteRuleField(grid);
        ListGridField publishedField = createRulePublishedField(grid);
        ListGridField descriptionField = createRuleDescriptionField(grid);
        grid.setFields(typeField, ownerField, nameField, descriptionField, publishedField, editField, deleteField);
        grid.setCanResizeFields(false);
        return grid;
    }

    private ListGridField createRuleTypeField(AllRulesListGrid grid) {
        ListGridField typeField = grid.createRuleTypeField();
        typeField.setAlign(CENTER);
        typeField.setWidth(60);
        return typeField;
    }

    private ListGridField createRuleOwnerField(AllRulesListGrid grid) {
        ListGridField ownerField = grid.createRuleOwnerField();
        ownerField.setAlign(CENTER);
        ownerField.setWidth(150);
        return ownerField;
    }

    private ListGridField createRuleNameField(AllRulesListGrid grid) {
        ListGridField nameField = grid.createRuleNameField();
        nameField.setAlign(CENTER);
        return nameField;
    }

    private ListGridField createRuleDescriptionField(AllRulesListGrid grid) {
        ListGridField descriptionField = grid.createRuleDescriptionField();
        descriptionField.setAlign(Alignment.CENTER);
        return descriptionField;
    }

    private ListGridField createRulePublishedField(AllRulesListGrid grid) {
        ListGridField publishField = grid.createRulePublishedField();
        publishField.setCanFilter(false);
        publishField.setAlign(CENTER);
        publishField.setWidth(110);
        return publishField;
    }

    private ListGridField createEditRuleField(AllRulesListGrid grid) {
        ListGridField editField = grid.createEditRuleField();
        editField.setCanFilter(false);
        editField.setAlign(CENTER);
        editField.setWidth(110);
        return editField;
    }

    private ListGridField createDeleteRuleField(AllRulesListGrid grid) {
        ListGridField deleteField = grid.createDeleteRuleField();
        deleteField.setCanFilter(false);
        deleteField.setAlign(CENTER);
        deleteField.setWidth(110);
        return deleteField;
    }

    public void setData(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        RuleDataSourceRecord rule;
        BasicRuleDTO basicDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.first) {
            this.rulesGrid.selectAllRecords();
            this.rulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basicRules.size(); i++) {
            basicDTO = basicRules.get(i);
            rule = new RuleDataSourceRecord(i18n.basic(), basicDTO.getOwnerName(), String.valueOf(basicDTO.getOwnerID()), basicDTO.getName(), basicDTO.getDescription(), "SMS", "XML", basicDTO.isRelease(), basicDTO.isSubscribed(), basicDTO.getUuid());
            
            this.rulesGrid.addData(rule);
        }

        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleDataSourceRecord(i18n.complex(), complexDTO.getOwnerName(), String.valueOf(complexDTO.getOwnerID()), complexDTO.getName(), complexDTO.getDescription(), "SMS", "XML", complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID");
       
            this.rulesGrid.addData(rule);
        }
        
        this.first = false;
        this.rulesGrid.fetchData();
    }
}