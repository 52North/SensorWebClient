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

import static com.google.gwt.user.client.Cookies.getCookie;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.ctrl.SesRequestManager.COOKIE_USER_ID;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.client.ses.ui.FormLayout.LayoutType.REGISTER;
import static org.n52.client.ses.ui.layout.LoginLayout.createUserLoginLayout;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;

import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.handler.ChangeLayoutEventHandler;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.client.ses.ui.layout.RegisterLayout;
import org.n52.client.ui.DataPanel;
import org.n52.client.ui.DataPanelTab;
import org.n52.client.ui.View;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class LoginWindow extends Window {

    private static String COMPONENT_ID;

    private static int WIDTH = 950;

    private static int HEIGHT = 550;

    protected Layout content;

    public LoginWindow(String ID) {
        COMPONENT_ID = ID;
        setStyleName("n52_sensorweb_client_login_window");
        initializeWindow();
        new LoginWindowEventBroker(this);
        addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
                hide();
            }
        });
    }

    protected void initializeWindow() {
        setID(COMPONENT_ID);
        setShowModalMask(true);
        setIsModal(true);
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowMinimizeButton(false);
        setMargin(10);
        setWidth(WIDTH);
        setHeight(HEIGHT);
        centerInPage();
        addResizedHandler(new ResizedHandler() {
            @Override
            public void onResized(ResizedEvent event) {
                WIDTH = LoginWindow.this.getWidth();
                HEIGHT = LoginWindow.this.getHeight();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (content != null) {
            removeItem(content);
        }
        if (notLoggedIn()) {
            content = new VLayout();
            content.addMember(createUserLoginLayout());
            addItem(content);
        }
        else {
            initializeContent();
        }
        redraw();
    }

    private void loadRegistration() {
        if (content != null) {
            removeItem(content);
        }
        content = new RegisterLayout();
        addItem(content);
        redraw();
    }

    protected boolean notLoggedIn() {
        return getUserCookie() == null;
    }
    
    protected void updateWindowTitle(String newTitle) {
        setTitle(newTitle);
    }

    protected abstract void initializeContent();

    public String getId() {
        return COMPONENT_ID;
    }

    public String getUserCookie() {
        return getCookie(COOKIE_USER_ID);
    }

    /**
     * Handling registered bus events fired by components within the application. See implementing handler
     * interfaces for more details.
     */
    private static class LoginWindowEventBroker implements SetRoleEventHandler, ChangeLayoutEventHandler {

        private final LoginWindow window;

        public LoginWindowEventBroker(LoginWindow window) {
            getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
            getMainEventBus().addHandler(ChangeLayoutEvent.TYPE, this);
            this.window = window;
        }

        @Override
        public void onChangeRole(SetRoleEvent evt) {
            if (evt.getRole() == LOGOUT) {
                return;
            }
            else if (evt.getRole() == ADMIN) {
                window.hide();
                DataPanel dataPanel = View.getView().getDataPanel();
                DataPanelTab sesTab = View.getView().getSesTab();
                dataPanel.getPanel().selectTab(sesTab);
                dataPanel.setCurrentTab(sesTab);
                dataPanel.update();
            }
            if (window.isVisible()) {
                reinitializeWindow();
            }
        }

        private void reinitializeWindow() {
            if (window.notLoggedIn()) {
                SC.say(i18n.failedLogin());
            }
            else {
                window.show();
            }
        }

        @Override
        public void onChange(ChangeLayoutEvent evt) {
            if (evt.getLayout() == REGISTER) {
                window.loadRegistration();
                window.updateWindowTitle(i18n.registration());
            } else if (evt.getLayout() == LOGIN) {
                window.updateWindowTitle(i18n.userLogin());
                if (window.isVisible()) {
                	window.show();
                }
            }
        }
    }

}