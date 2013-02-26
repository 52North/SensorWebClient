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