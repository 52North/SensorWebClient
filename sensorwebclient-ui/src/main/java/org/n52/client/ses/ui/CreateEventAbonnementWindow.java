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

import static com.smartgwt.client.types.Alignment.RIGHT;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.ses.i18n.SesStringsAccessor;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.ui.ApplyCancelButtonLayout;

import com.google.gwt.core.shared.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CreateEventAbonnementWindow extends Window {

    private static final String COMPONENT_ID = "sesCommunicator";

    private static int WIDTH = 950;

    private static int HEIGHT = 550;

    private static CreateEventAbonnementWindow instance;

    private static CreateEventAbonnementController controller;

    private Layout content;

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
                hide();
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
        setTitle(i18n.createAboWindowTitle());
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
        initializeContent();
        super.show();
    }

    private void initializeContent() {
        if (content == null) {
            content = new HLayout();
            content.setStyleName("n52_sensorweb_client_create_abo_window_content");
            content.addMember(createNewEventAbonnementCanvas());
            content.addMember(createContextWindowHelp());
            content.addMember(createApplyCancelCanvas());
            addItem(content);
        }
    }

    private Canvas createNewEventAbonnementCanvas() {
        Layout content = new VLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_form_content");
        content.addMember(new CreateAbonnementForm(controller));
        content.addMember(new SelectPredefinedAboForm(controller));
        content.addMember(new TimeSeriesMetadataTable(controller));
        return content;
    }

    private Canvas createContextWindowHelp() {
        Layout content = new VLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_context_help");
        
        // TODO Auto-generated method stub
        return content;
        
    }


    private Canvas createApplyCancelCanvas() {
        ApplyCancelButtonLayout applyCancel = new ApplyCancelButtonLayout();
        applyCancel.setAlign(RIGHT);
        String apply = i18n.create();
        String applyLong = i18n.create();
        ClickHandler applyHandler = new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                GWT.log("applied");
                // TODO Auto-generated method stub
                
            }
        };
        
        String cancel = i18n.create();
        String cancelLong = i18n.create();
        ClickHandler cancelHandler = new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                GWT.log("cancelled");
                // TODO Auto-generated method stub
                
            }
        };
        
        applyCancel.createApplyButton(apply, applyLong, applyHandler);
        applyCancel.createCancelButton(cancel, cancelLong, cancelHandler);
        
        // TODO Auto-generated method stub
        return applyCancel;
        
    }
    

    public String getId() {
        return COMPONENT_ID;
    }

    public void setTimeseries(TimeSeries timeseries) {
        controller.setTimeseries(timeseries);
    }

}