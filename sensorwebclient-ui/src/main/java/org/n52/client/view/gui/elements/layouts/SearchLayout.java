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
import org.n52.client.eventBus.events.ses.EditRuleEvent;
import org.n52.client.eventBus.events.ses.SearchEvent;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.model.data.representations.RuleRecord;
import org.n52.client.view.gui.elements.interfaces.Layout;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;
import org.n52.shared.serializable.pojos.RuleDS;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * The Class AllRulesLayout.
 * 
 * This view conatians a table which shows all search matches. This view 
 * implements several search criteria.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class SearchLayout extends Layout {

    /** The rules grid. */
    private ListGrid rulesGrid;
    
    private TextItem textItem;
    
    private SelectItem comboBoxItem;
    
    private ButtonItem buttonItem;

    private RuleDS dataSource;
    
    private boolean first = true;
    
    private int itemWidth = 160;
    

    /**
     * Instantiates a new all rules layout.
     */
    public SearchLayout() {
        super(I18N.sesClient.search());
        
        // init DataSource
        this.dataSource = new RuleDS();
        
        init();
    }

    /**
     * Inits the layout.
     */
    private void init() {

        this.rulesGrid = new ListGrid() {
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
                                EventBus.getMainEventBus().fireEvent(new EditRuleEvent(record.getAttribute("name")));
                            }
                        });

                        return editButton;
                    }
                    return null;
                }
                return null;
            }
        };

        // grid config
        this.rulesGrid.setShowRecordComponents(true);
        this.rulesGrid.setShowRecordComponentsByCell(true);
        this.rulesGrid.setWidth100();
        this.rulesGrid.setHeight100();
        this.rulesGrid.setShowAllRecords(false);
        this.rulesGrid.setCanGroupBy(false);
        this.rulesGrid.setCanSelectAll(false);
        this.rulesGrid.setDataSource(this.dataSource);
        this.rulesGrid.setAutoFetchData(true);
        this.rulesGrid.setShowFilterEditor(true);
        this.rulesGrid.setFilterOnKeypress(true);
        this.rulesGrid.setShowRollOver(false);
        this.rulesGrid.sort(1, SortDirection.ASCENDING);

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

        // fill grid with rows and data
        this.rulesGrid.setFields(typeField, nameField, descriptionField, editField);
        this.rulesGrid.setCanResizeFields(false);

        // search bar
        this.form2 = new DynamicForm();
        this.form2.setShowEdges(true);
        this.form2.setEdgeSize(2);
//        this.form2.setFixedColWidths(true);
        this.form2.setTitleOrientation(TitleOrientation.TOP);
        this.form2.setNumCols(3);
        
        this.textItem = new TextItem("text");
        this.textItem.setTitle(I18N.sesClient.searchWord());
        this.textItem.setWidth(this.itemWidth);
        
        this.comboBoxItem = new SelectItem("select");
        this.comboBoxItem.setTitle(I18N.sesClient.searchCriterion());
        this.comboBoxItem.setType("comboBox"); 
        this.comboBoxItem.setValueMap(
                I18N.sesClient.searchFullText(),
                I18N.sesClient.name(), I18N.sesClient.description(),
                I18N.sesClient.sensor(), I18N.sesClient.phenomenon());
        
        this.comboBoxItem.setDefaultValue(I18N.sesClient.searchFullText());
        this.comboBoxItem.setWidth(this.itemWidth);
        
        this.buttonItem = new ButtonItem("button");
        this.buttonItem.setTitle(I18N.sesClient.search());
        this.buttonItem.setWidth(100);
        this.buttonItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
                if (textItem.getValue() != null && comboBoxItem.getValue() != null) {
                    String text = textItem.getValue().toString();
                    String criterion = comboBoxItem.getValue().toString();
                   
                    String userID = Cookies.getCookie(SesRequestManager.COOKIE_USER_ID);
                    int operator = 0;
                    
                    if (criterion.equals(I18N.sesClient.searchFullText())) {
                        operator = 1;
                    } else if (criterion.equals(I18N.sesClient.name())) {
                        operator = 2;
                    } else if (criterion.equals(I18N.sesClient.description())) {
                        operator = 3;
                    } else if (criterion.equals(I18N.sesClient.sensor())) {
                        operator = 4;
                    } else if (criterion.equals(I18N.sesClient.phenomenon())) {
                        operator = 5;
                    }
                    EventBus.getMainEventBus().fireEvent(new SearchEvent(text, operator, userID));
                }
            }
        });
        
        // add to mainLayout
        this.form.setFields(this.headerItem);
        this.form2.setFields(this.comboBoxItem, this.textItem, this.buttonItem);
        addMember(this.form);
        addMember(this.form2);
        addMember(this.rulesGrid);
    }

    /**
     * Fill table with search results.
     * 
     * @param basicRules 
     * @param complexRules 
     */
    public void setData(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        RuleRecord rule;
        BasicRuleDTO basicDTO;
        ComplexRuleDTO complexDTO;
        
        if (!this.first) {
            this.rulesGrid.selectAllRecords();
            this.rulesGrid.removeSelectedData(); 
        }

        for (int i = 0; i < basicRules.size(); i++) {
            basicDTO = basicRules.get(i);
            rule = new RuleRecord(I18N.sesClient.basic(), "", "", basicDTO.getName(), basicDTO.getDescription(), "SMS", "XML", basicDTO.isRelease(), basicDTO.isSubscribed());
            
            this.rulesGrid.addData(rule);
        }

        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleRecord(I18N.sesClient.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), "SMS", "XML", complexDTO.isRelease(), complexDTO.isSubscribed());
            
            this.rulesGrid.addData(rule);
        }
        
        this.first = false;
        this.rulesGrid.fetchData();
    }
}