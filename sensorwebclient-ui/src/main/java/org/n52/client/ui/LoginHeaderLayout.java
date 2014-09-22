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
package org.n52.client.ui;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.getLoggedInUser;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserRole;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;
import static org.n52.shared.serializable.pojos.UserRole.USER;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.SessionExpiredEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.handler.SessionExpiredEventHandler;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.shared.serializable.pojos.UserRole;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class LoginHeaderLayout extends HLayout {
	
	private Label user;
	private HLayout admin;

	public LoginHeaderLayout() {
		initializeLayout();
		new LoginHeaderEventBroker(this);
		showUserAsLoggedIn();
	}

	private void initializeLayout() {
		setStyleName("n52_sensorweb_client_loginBlock");
		setAlign(Alignment.RIGHT);
		
		user = new Label();
		user.setStyleName("n52_sensorweb_client_headerLoggedInAs");
		user.setAutoWidth();
		user.setWrap(false);
		addMember(user);
		
		// logout link
		Label logout = getHeaderLinkLabel("(" + i18n.logout() + ")");
		logout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getMainEventBus().fireEvent(new LogoutEvent(currentSession()));
			}
		});
		addMember(logout);
		
		// admin link
		admin = new HLayout();
		admin.setStyleName("n52_sensorweb_client_headerLoggedInAs");
		admin.setAutoWidth();
		Label link = getHeaderLinkLabel(i18n.admin());		
		admin.addMember(getSeparator());
		admin.addMember(link);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DataPanel dataPanel = View.getView().getDataPanel();
				DataPanelTab newTab;
				if (dataPanel.getCurrentTab().equals(View.getView().getSesTab())) {
					newTab = View.getView().getDiagramTab();
				} else {
					newTab = View.getView().getSesTab();
				}
				dataPanel.getPanel().selectTab(newTab);
				dataPanel.setCurrentTab(newTab);
				dataPanel.update();
			}
		});
		addMember(admin);
	}
	
	private void showUserAsLoggedIn() {
		String userName = getLoggedInUser();
		if(userName != null) {
			show();
			user.setContents(i18n.loggedInAs() + " " + userName);
			String roleType = getLoggedInUserRole();
			if (roleType.equals(ADMIN.name())) {
				admin.show();
			} else {
				admin.hide();
			}
		} else {
			hide();
		}
	}
	
	private void showLoggedOutHeader() {
	    admin.hide();
	    hide();
	}
	
    private Label getHeaderLinkLabel(String labelText) {
    	Label label = new Label(labelText);
        label.setStyleName("n52_sensorweb_client_headerlink");
        label.setAutoWidth();
        label.setWrap(false);
		return label;
	}
    
    private Label getSeparator(){
        Label pipe = new Label("|");
        pipe.setStyleName("n52_sensorweb_client_pipe");
        pipe.setAutoWidth();
        return pipe;
    }
    
    private static class LoginHeaderEventBroker implements SetRoleEventHandler, SessionExpiredEventHandler {

    	private final LoginHeaderLayout loginHeaderLayout;
    	
		public LoginHeaderEventBroker(LoginHeaderLayout loginHeaderLayout) {
			EventBus.getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(SessionExpiredEvent.TYPE, this);
			this.loginHeaderLayout = loginHeaderLayout;
		}

		@Override
		public void onChangeRole(SetRoleEvent evt) {
		    UserRole role = evt.getRole();
		    if (role == LOGOUT) {
                loginHeaderLayout.showLoggedOutHeader();
                SC.say(i18n.logoutSuccessful());
            } else if (role == USER || role == ADMIN) {
                loginHeaderLayout.showUserAsLoggedIn();
            }
		}

		@Override
		public void onSessionExpired(SessionExpiredEvent evt) {
			loginHeaderLayout.showLoggedOutHeader();
		}
    }

}
