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
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.NONE;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import java.util.LinkedHashMap;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class SelectSubscriptionForm extends DynamicForm {

    private final EventSubscriptionController controller;
    
    public SelectSubscriptionForm(final EventSubscriptionController controller) {
        this.setStyleName("n52_sensorweb_client_create_abo_selection");
        this.controller = controller;
        setFields(createPredefinedEventSelectionItem());
        controller.setSelectedSubscriptionTemplate(getDefaultSubscriptionTemplate());
    }

    private RadioGroupItem createPredefinedEventSelectionItem() {
        RadioGroupItem radioGroupItem = new RadioGroupItem();
        radioGroupItem.setTitle(i18n.selectPredefinedEventForSubscription());
        radioGroupItem.addChangedHandler(createSelectionChangedHandler());
        radioGroupItem.setValueMap(createSelectionValueMap());
        radioGroupItem.setDefaultValue(controller.getDefaultTemplate().name());
        return radioGroupItem;
    }

    private ChangedHandler createSelectionChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                RadioGroupItem item = (RadioGroupItem) event.getItem();
                String value = item.getValueAsString();
                final SimpleRuleType template = SimpleRuleType.getTypeFor(value);
                SelectSubscriptionForm.this.handleRuleTemplateSelection(template);
            }
        };
    }
    
    private void handleRuleTemplateSelection(final SimpleRuleType templateType) {
        GWT.log("Rule template selected: " + templateType);
        controller.updateSelectedRuleTemplate(createRuleTemplateFor(templateType));
    }

    private LinkedHashMap<String, String> createSelectionValueMap() {
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put(OVER_UNDERSHOOT.name(), i18n.overUnderShoot());
        valueMap.put(SENSOR_LOSS.name(), i18n.sensorFailure());
        return valueMap;
    }
    
    SubscriptionTemplate getDefaultSubscriptionTemplate() {
        return createRuleTemplateFor(controller.getDefaultTemplate());
    }
    
    private SubscriptionTemplate createRuleTemplateFor(SimpleRuleType template) {
        if (template == NONE) {
            GWT.log("Unknown template selected!");
            return null;
        } else if (template == OVER_UNDERSHOOT) {
            return createOverUndershootRuleTemplate();
        } else if (template == SENSOR_LOSS) {
            return createSensorLossRuleTemplate();
        } else {
            GWT.log("Unsupported template selected!");
            return null;
        }
    }

    private SubscriptionTemplate createOverUndershootRuleTemplate() {
        return new OverUndershootRuleTemplate(controller);
    }

    private SubscriptionTemplate createSensorLossRuleTemplate() {
        return new SensorLossRuleTemplate(controller);
    }

}