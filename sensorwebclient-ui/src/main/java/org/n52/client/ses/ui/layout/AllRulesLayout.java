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

import java.util.ArrayList;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.SesRequestManager;
import org.n52.client.ses.data.RuleDataSource;
import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.EditRuleEvent;
import org.n52.client.ses.event.PublishRuleEvent;
import org.n52.client.ses.ui.Layout;
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
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class AllRulesLayout.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class AllRulesLayout extends Layout {

    /** The rules grid. */
    private ListGrid rulesGrid;
    
    private boolean first = true;
    
    private RuleDataSource dataSource;

    /**
     * Instantiates a new all rules layout.
     */
    public AllRulesLayout() {
        super(i18n.allRules());
        
        // init DataSource
        this.dataSource = new RuleDataSource();
        
        init();
    }

    /**
     * Inits the.
     */
    private void init() {

        this.rulesGrid = new ListGrid() {
            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

                if (record != null) {
                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals("published")) {

                        // publish button
                        IButton publishButton = new IButton(i18n.publishButton());
                        publishButton.setShowDown(false);
                        publishButton.setShowRollOver(false);
                        publishButton.setLayoutAlign(Alignment.CENTER);
                        publishButton.setHeight(16);
                        publishButton.setAutoFit(true);

                        final boolean published = record.getAttributeAsBoolean("published");
                        final String ruleName = record.getAttribute("name");
                        
                        String userID = Cookies.getCookie(SesRequestManager.COOKIE_USER_ID);
                        String recordUserID = record.getAttribute("ownerID");
                        
                        
                        if (published && !userID.equals(recordUserID)) {
                            publishButton.setTitle(i18n.unpublishButton());
                            publishButton.setPrompt(i18n.cancelPublication());
                            publishButton.addClickHandler(new ClickHandler() {
                                public void onClick(ClickEvent event) {
                                    EventBus.getMainEventBus().fireEvent(new PublishRuleEvent(ruleName, !published, "ADMIN"));
                                }
                            });

                            return publishButton;
                        } else if (userID.equals(recordUserID)) {
                            if (published) {
                                publishButton.setTitle(i18n.unpublishButton());
                                publishButton.setPrompt(i18n.cancelPublication());
                                publishButton.addClickHandler(new ClickHandler() {
                                    public void onClick(ClickEvent event) {
                                        EventBus.getMainEventBus().fireEvent(new PublishRuleEvent(ruleName, !published, "ADMIN"));
                                    }
                                });
                            } else {
                                publishButton.setTitle(i18n.publishButton());
                                publishButton.setPrompt(i18n.publishThisRule());
                                publishButton.addClickHandler(new ClickHandler() {
                                    public void onClick(ClickEvent event) {
                                        EventBus.getMainEventBus().fireEvent(new PublishRuleEvent(ruleName, !published, "ADMIN"));
                                    }
                                });
                            }

                            return publishButton;
                        } 
                        return null;

                    } else if (fieldName.equals("edit")) {
                        String userID = Cookies.getCookie(SesRequestManager.COOKIE_USER_ID);
                        if (record.getAttribute("ownerID").equals(userID)) {
                            // subscribe button
                            IButton editButton = new IButton(i18n.edit());
                            editButton.setShowDown(false);
                            editButton.setShowRollOver(false);
                            editButton.setLayoutAlign(Alignment.CENTER);
                            editButton.setPrompt(i18n.editThisRule());
                            editButton.setHeight(16);
                            editButton.addClickHandler(new ClickHandler() {
                                public void onClick(ClickEvent event) {
                                    EventBus.getMainEventBus().fireEvent(new EditRuleEvent(record.getAttribute("name")));
                                }
                            });

                            return editButton;
                        }

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
        ListGridField typeField = new ListGridField("type", i18n.type());
        typeField.setWidth(60);
        typeField.setAlign(Alignment.CENTER);
        
        ListGridField ownerField = new ListGridField("ownerName", i18n.owner());
        ownerField.setWidth(150);
        ownerField.setAlign(Alignment.CENTER);

        ListGridField nameField = new ListGridField("name", i18n.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField descriptionField = new ListGridField("description", i18n.description());
        descriptionField.setAlign(Alignment.CENTER);
        
        ListGridField publishField = new ListGridField("published", i18n.publish());
        publishField.setWidth(110);
        publishField.setCanFilter(false);
        publishField.setAlign(Alignment.CENTER);

        ListGridField editField = new ListGridField("edit", i18n.edit());
        editField.setWidth(110);
        editField.setCanFilter(false);
        editField.setAlign(Alignment.CENTER);

        ListGridField deleteField = new ListGridField("delete", i18n.delete());
        deleteField.setWidth(110);
        deleteField.setCanFilter(false);
        deleteField.setAlign(Alignment.CENTER);

        // fill grid with rows and data
        this.rulesGrid.setFields(typeField, ownerField, nameField, descriptionField, publishField, editField, deleteField);
        this.rulesGrid.setCanResizeFields(false);

        this.form.setFields(this.headerItem);

        // add to mainLayout
        addMember(this.form);
        addMember(this.rulesGrid);
    }

    /**
     * Sets the data.
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
            rule = new RuleRecord(i18n.basic(), basicDTO.getOwnerName(), String.valueOf(basicDTO.getOwnerID()), basicDTO.getName(), basicDTO.getDescription(), "SMS", "XML", basicDTO.isRelease(), basicDTO.isSubscribed());
            
            this.rulesGrid.addData(rule);
        }

        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleRecord(i18n.complex(), complexDTO.getOwnerName(), String.valueOf(complexDTO.getOwnerID()), complexDTO.getName(), complexDTO.getDescription(), "SMS", "XML", complexDTO.isRelease(), complexDTO.isSubscribed());
       
            this.rulesGrid.addData(rule);
        }
        
        this.first = false;
        this.rulesGrid.fetchData();
    }
}