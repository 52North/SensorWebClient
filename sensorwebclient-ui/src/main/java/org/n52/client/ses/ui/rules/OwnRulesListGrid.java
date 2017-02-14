/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static com.smartgwt.client.types.SortDirection.ASCENDING;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.NAME;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.PUBLISHED;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.UUID;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserRole;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.EditRuleEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.PublishRuleEvent;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class OwnRulesListGrid extends ListGrid {
    
    private static final String EDIT_RULE_FIELD = "ownRules_editRuleField";

    private static final String PUBLISHED_RULE_FIELD = "ownRules_publishedRuleField";

    private static final String DELETE_RULE_FIELD = "ownRules_deleteRuleField";
    
    public OwnRulesListGrid() {
        setDataSource(new RuleDataSource());
        setShowRecordComponents(true);
        setShowRecordComponentsByCell(true);
        setWidth100();
        setHeight100();
        setCanGroupBy(false);
        setAutoFetchData(true);
        setShowFilterEditor(true);
        setFilterOnKeypress(true);
        setShowRollOver(false);
        sort(1, ASCENDING);
    }
    
    @Override
    protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
        if (record != null) {
            String fieldName = this.getFieldName(colNum);
            if (fieldName.equals(EDIT_RULE_FIELD)) {
                return createEditRuleButton(record);
            } else if (fieldName.equals(PUBLISHED_RULE_FIELD)) {
                return createPublishRuleButton(record);
            } else if (fieldName.equals(DELETE_RULE_FIELD)) {
                return createDeleteRuleButtonm(record);
            } else {
                return null;
            }
        }
        return null;
    }

    private Canvas createDeleteRuleButtonm(final ListGridRecord ruleRecord) {
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
                            String uuid = ruleRecord.getAttribute(UUID);
                            String userRole = getLoggedInUserRole();
                            EventBus.getMainEventBus().fireEvent(new DeleteRuleEvent(currentSession(), uuid, userRole));
                        }
                    }
                });
            }
        });
        return deleteButton;
    }

    private Canvas createPublishRuleButton(final ListGridRecord ruleRecord) {
        IButton publishButton = new IButton(i18n.publishButton());
        publishButton.setShowDown(false);
        publishButton.setShowRollOver(false);
        publishButton.setLayoutAlign(Alignment.CENTER);
        publishButton.setHeight(16);
        publishButton.setAutoFit(true);

        final boolean published = ruleRecord.getAttributeAsBoolean(PUBLISHED);
        if (published) {
            publishButton.setTitle(i18n.unpublishButton());
            publishButton.setPrompt(i18n.cancelPublication());
        } else {
            publishButton.setTitle(i18n.publishButton());
            publishButton.setPrompt(i18n.publishThisRule());
        }

        publishButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String ruleName = ruleRecord.getAttribute(NAME);
                EventBus.getMainEventBus().fireEvent(new PublishRuleEvent(currentSession(), ruleName, !published, "USER"));
            }
        });

        return publishButton;
    }

    private Canvas createEditRuleButton(final ListGridRecord ruleRecord) {
        // subscribe button
        IButton editButton = new IButton(i18n.edit());
        editButton.setShowDown(false);
        editButton.setShowRollOver(false);
        editButton.setLayoutAlign(Alignment.CENTER);
        editButton.setPrompt(i18n.editThisRule());
        editButton.setHeight(16);
        editButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String name = ruleRecord.getAttribute(NAME);
                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(currentSession(), 1));
                EventBus.getMainEventBus().fireEvent(new EditRuleEvent(name));
            }
        });

        return editButton;
    }
}