/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ses.data;

import static com.smartgwt.client.types.Alignment.CENTER;
import static com.smartgwt.client.types.SortDirection.ASCENDING;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.FORMAT;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.MEDIUM;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.NAME;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.SUBSCRIBED;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.UUID;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserRole;

import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.SubscribeEvent;
import org.n52.client.ses.event.UnsubscribeEvent;
import org.n52.client.ses.ui.rules.RuleDataSource;
import org.n52.client.ui.btn.SmallButton;

import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class SubscriptionListGrid extends ListGrid {

    private static final String ACTIVATED_FIELD = "subscription_activatedField";
    
    private static final String DELETE_FIELD = "subscription_deleteField";
    
    private String delImg = "../img/icons/del.png";

    public SubscriptionListGrid() {
        setDefaultFields(createFields());
        setDataSource(new RuleDataSource());
        setShowRecordComponents(true);
        setShowRecordComponentsByCell(true);
        setWidth100();
        setHeight100();
        setCanGroupBy(false);
        setShowFilterEditor(true);
        setFilterOnKeypress(true);
        setShowRollOver(false);
        setCanResizeFields(false);
        sort(1, ASCENDING);
    }

    private ListGridField[] createFields() {
        ListGridField name = new ListGridField(NAME, i18n.name());
        ListGridField activated = new ListGridField(ACTIVATED_FIELD, i18n.active());
        ListGridField delete = new ListGridField(DELETE_FIELD, i18n.delete());
        return new ListGridField[] { name, activated, delete };
    }

    @Override
    protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
        if (record != null) {
            String fieldName = getFieldName(colNum);
            if (fieldName.equals(DELETE_FIELD)) {
                return createDeleteButton(record);
            } else if (fieldName.equals(ACTIVATED_FIELD)) {
                return createActivateForm(record);
            }
            return null;
        }
        return null;
    }

    private Canvas createActivateForm(final ListGridRecord ruleRecord) {
        DynamicForm form = new DynamicForm();
        form.setItems(createActivateCheckboxItem(ruleRecord));
        form.setAutoWidth();
        return form;
    }

    protected CheckboxItem createActivateCheckboxItem(final ListGridRecord ruleRecord) {
        CheckboxItem checkBox = new CheckboxItem(ACTIVATED_FIELD, " ");
        checkBox.setValue(ruleRecord.getAttributeAsBoolean(SUBSCRIBED));
        checkBox.addChangedHandler(createActivateChangedHandler(ruleRecord));
        return checkBox;
    }

    protected ChangedHandler createActivateChangedHandler(final ListGridRecord ruleRecord) {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                CheckboxItem checkbox = (CheckboxItem) event.getSource();
                boolean checked = checkbox.getValueAsBoolean().booleanValue();
                String uuid = ruleRecord.getAttribute(UUID);
                String medium = ruleRecord.getAttribute(MEDIUM);
                String format = ruleRecord.getAttribute(FORMAT);
                ruleRecord.setAttribute(SUBSCRIBED, checked);
                if(checked) {
                    getMainEventBus().fireEvent(new SubscribeEvent(currentSession(), uuid, medium, format));
                } else {
                    getMainEventBus().fireEvent(new UnsubscribeEvent(currentSession(), uuid, medium, format));
                }
            }
        };
    }

    private Canvas createDeleteButton(final ListGridRecord ruleRecord) {
        Canvas delButton = new SmallButton(new Img(delImg) , "", "");
        delButton.addClickHandler(createDeleteHandler(ruleRecord));
        delButton.setPrompt(i18n.unsubscribeThisRule());
        delButton.setLayoutAlign(CENTER);
        delButton.setMargin(1);
        return delButton;
    }

    protected ClickHandler createDeleteHandler(final ListGridRecord ruleRecord) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                boolean subscribed = ruleRecord.getAttributeAsBoolean(SUBSCRIBED).booleanValue();
                if (subscribed) {
                    SC.say(i18n.deleteOnlyWhenUnsubbscribed());
                } else {
                    SC.ask(i18n.deleteSubscriptionQuestion(), new BooleanCallback() {
                        @Override
                        public void execute(Boolean value) {
                            if (value) {
                                String role = getLoggedInUserRole();
                                String uuid = ruleRecord.getAttribute(UUID);
                                getMainEventBus().fireEvent(new DeleteRuleEvent(currentSession(), uuid, role));
                                removeData(ruleRecord);
                            }
                        }
                    });
                }
            }
        };
    }

    public ListGridField getNameField() {
        return getField(NAME);
    }

    public ListGridField getActivatedField() {
        return getField(ACTIVATED_FIELD);
    }

    public ListGridField getDeleteField() {
        return getField(DELETE_FIELD);
    }

}