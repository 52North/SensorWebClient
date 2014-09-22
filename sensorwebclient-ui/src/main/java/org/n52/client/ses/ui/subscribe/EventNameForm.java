/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

public class EventNameForm extends DynamicForm {

    private TextItem aboNameItem;
    
    private final EventSubscriptionController controller;

    public EventNameForm(final EventSubscriptionController controller) {
        this.setStyleName("n52_sensorweb_client_create_abo_eventname");
        this.controller = controller;
        this.controller.setEventNameForm(this);
        setFields(createAbonnementNameItem());
    }
    
    private TextItem createAbonnementNameItem() {
        aboNameItem = new TextItem();
        aboNameItem.setRequired(true);
        aboNameItem.setTextBoxStyle("n52_sensorweb_client_abo_name_textbox");
        aboNameItem.setTitle(i18n.aboName());
        aboNameItem.setWidth("*"); // fill form column
        aboNameItem.setKeyPressFilter("[0-9a-zA-Z_]");
        aboNameItem.setValue(controller.createSuggestedAbonnementName());
        aboNameItem.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                TextItem nameItem = (TextItem) event.getSource();
                String currentAbonnementName = (String) nameItem.getValue();
                controller.setSelectedAbonnementName(currentAbonnementName);
            }
        });
        return aboNameItem;
    }

    public void updateSuggestedAbonnementName(String suggestedAboName) {
        if (aboNameItem != null) {
            aboNameItem.setValue(suggestedAboName);
        }
    }

}
