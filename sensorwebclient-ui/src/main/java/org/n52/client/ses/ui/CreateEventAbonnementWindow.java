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

import org.n52.client.sos.legend.TimeSeries;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CreateEventAbonnementWindow extends Window {

    private static final String COMPONENT_ID = "sesCommunicator";

    private static int WIDTH = 950;

    private static int HEIGHT = 550;

    private static CreateEventAbonnementWindow instance;

    private static CreateEventAbonnementController controller;

    public static CreateEventAbonnementWindow getInst() {
        if (instance == null) {
            controller = new CreateEventAbonnementController();
            instance = new CreateEventAbonnementWindow(controller);
        }
        return instance;
    }

    private CreateEventAbonnementWindow(CreateEventAbonnementController controller) {
        controller.setSesCommunicator(this);
        initializeWindow();
        addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                closeSesCommunicator();
            }
        });
    }

    private void initializeWindow() {
        setID(COMPONENT_ID);
        setShowModalMask(true);
        setIsModal(true);
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowMinimizeButton(false);
        setMargin(10);
        setTitle(i18n.sesCommunicatorTitle());
        setWidth(WIDTH);
        setHeight(HEIGHT);
        centerInPage();
        addResizedHandler(new ResizedHandler() {
            @Override
            public void onResized(ResizedEvent event) {
                WIDTH = CreateEventAbonnementWindow.this.getWidth();
                HEIGHT = CreateEventAbonnementWindow.this.getHeight();
            }
        });
    }

    @Override
    public void show() {
        clear();
        super.show();
        
        // TODO add login functionality
        
        setTitle(i18n.sesCommunicatorTitle());
        initializeContent();
    }

    private void initializeContent() {
        Layout content = new HLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_window_content");
        content.addMember(createNewEventAbonnementCanvas());
//        content.addMember(createContextWindowHelp());
        addItem(content);
    }

    private Canvas createNewEventAbonnementCanvas() {
        Layout content = new VLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_form_content");
        content.addMember(new CreateAbonnementForm(controller));
        content.addMember(new TimeSeriesMetadataTable(controller));
        content.addMember(new SelectPredefinedAboCanvas(controller));
        return content;
    }

    private Canvas createContextWindowHelp() {
        Layout content = new VLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_context_help");
        
        // TODO Auto-generated method stub
        return content;
        
    }

    

    private void closeSesCommunicator() {
        hide();
    }

    public String getId() {
        return COMPONENT_ID;
    }

    public void setTimeseries(TimeSeries timeseries) {
        controller.setTimeseries(timeseries);
    }

}