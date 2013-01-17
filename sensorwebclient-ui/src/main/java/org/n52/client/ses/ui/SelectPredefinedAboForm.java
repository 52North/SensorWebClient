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
package org.n52.client.ses.ui;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.ses.i18n.SesStringsAccessor;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;

public class SelectPredefinedAboForm extends DynamicForm {

    private CreateEventAbonnementController controller;
    
    public SelectPredefinedAboForm(CreateEventAbonnementController controller) {
        this.setStylePrimaryName("n52_sensorweb_client_create_abo_selection");
        this.controller = controller;
        setFields(createPredefinedEventSelectionItem());
    }

    private RadioGroupItem createPredefinedEventSelectionItem() {
        RadioGroupItem radioGroupItem = new RadioGroupItem();  
        radioGroupItem.setTitle("Radio Group");
        radioGroupItem.setValueMap("Event 1", "Event 2");  
        // TODO Auto-generated method stub
        return radioGroupItem;
        
    }

}