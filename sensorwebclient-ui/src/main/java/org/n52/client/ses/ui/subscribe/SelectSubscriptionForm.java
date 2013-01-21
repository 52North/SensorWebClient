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
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.NONE;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import java.util.LinkedHashMap;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.google.gwt.core.shared.GWT;
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
    }

    private RadioGroupItem createPredefinedEventSelectionItem() {
        RadioGroupItem radioGroupItem = new RadioGroupItem();
        radioGroupItem.setTitle(i18n.selectPredefinedEventForSubscription());
        radioGroupItem.addChangedHandler(createSelectionChangeHandler());
        radioGroupItem.setValueMap(createSelectionValueMap());
        
        // TODO set first value so that event is triggered.
        radioGroupItem.setDefaultValue(OVER_UNDERSHOOT.name());
        return radioGroupItem;
    }

    private ChangedHandler createSelectionChangeHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                RadioGroupItem item = (RadioGroupItem) event.getItem();
                String value = (String) item.getValue();
                SimpleRuleType template = SimpleRuleType.getTypeFor(value);
                GWT.log("Rule template selected: " + template);
                if (template == NONE) {
                    GWT.log("Unknown template selected!");
                } else if (template == OVER_UNDERSHOOT) {
                    controller.setSelectedRuleTemplate(createOverUndershootRuleTemplate());
                } else if (template == SENSOR_LOSS) {
                    controller.setSelectedRuleTemplate(createSensorLossRuleTemplate());
                } else {
                    GWT.log("Unsupported template selected!");
                }
            }
        };
    }

    private LinkedHashMap<String, String> createSelectionValueMap() {
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put(OVER_UNDERSHOOT.name(), i18n.overUnderShoot());
        valueMap.put(SENSOR_LOSS.name(), i18n.sensorFailure());
        return valueMap;
    }
    
    private RuleTemplate createOverUndershootRuleTemplate() {
        return new OverUndershootRuleTemplate(controller);
    }

    private RuleTemplate createSensorLossRuleTemplate() {
        return new SensorLossRuleTemplate(controller);
    }

}