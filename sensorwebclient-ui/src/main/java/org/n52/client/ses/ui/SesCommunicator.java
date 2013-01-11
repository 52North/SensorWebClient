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

import org.n52.client.ses.ui.layout.LoginLayout;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;

public class SesCommunicator extends Window {
	
    private static final String COMPONENT_ID = "sesCommunicator";
    
    private static int WIDTH = 950;
    
    private static int HEIGHT = 550; 

    private static SesCommunicator instance;

    private static SesCommunicatorController controller;
    
	private Layout loginLayout;
	
	private Layout createEventLayout;

	private String timeseriesID;

    public static SesCommunicator getInst() {
        if (instance == null) {
        	controller = new SesCommunicatorController();
            instance = new SesCommunicator(controller);
        }
        return instance;
    }

    private SesCommunicator(SesCommunicatorController controller) {
    	controller.setSesCommunicator(this);
        initializeWindow();
        initializeContent();
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
				WIDTH = SesCommunicator.this.getWidth();
				HEIGHT = SesCommunicator.this.getHeight();
			}
		});
    }
	
	@Override
	public void show() {
		super.show();
		// TODO check the login state of the user and show the corresponding layout
		boolean notLoggedIn = false;
		if (notLoggedIn) {
			loginLayout.show();
			createEventLayout.hide();
		} else {
			loginLayout.hide();
			createEventLayout.show();
		}
		setTitle(i18n.sesCommunicatorTitle() + this.timeseriesID);
	}

    private void initializeContent() {
    	// login layout
    	if (loginLayout == null) {
    		loginLayout = new LoginLayout(null);
    		addItem(loginLayout);
    	}
    	// create event layout
    	if (createEventLayout == null) {
    		createEventLayout = new HLayout();
    		createEventLayout.addMember(new Label("Logged in"));
	    	addItem(createEventLayout);
		}
	}
    
	private void closeSesCommunicator() {
		hide();
	}

    public String getId() {
        return COMPONENT_ID;
    }

	public void setTimeseriesID(String timeseriesID) {
		this.timeseriesID = timeseriesID;
	}

}