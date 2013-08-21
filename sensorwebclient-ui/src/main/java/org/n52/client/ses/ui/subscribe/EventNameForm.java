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
