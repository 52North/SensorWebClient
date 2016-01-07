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
package org.n52.client.ses.ui;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.client.ses.ui.FormLayout.LayoutType.PASSWORD;
import static org.n52.client.ses.ui.FormLayout.LayoutType.REGISTER;
import static org.n52.client.ses.ui.layout.LoginLayout.createUserLoginLayout;
import static org.n52.client.util.ClientSessionManager.isNotLoggedIn;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;

import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.handler.ChangeLayoutEventHandler;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.client.ses.ui.layout.RegisterLayout;
import org.n52.client.ses.ui.layout.ResetPasswordLayout;
import org.n52.client.ui.DataPanel;
import org.n52.client.ui.DataPanelTab;
import org.n52.client.ui.View;

import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class LoginWindow extends Window {

    private static String COMPONENT_ID;

    private static int WIDTH = 960;

    private static int HEIGHT = 552;

    protected Layout content;

    public LoginWindow(String ID) {
        COMPONENT_ID = ID;
        initializeWindow();
        new LoginWindowEventBroker(this);
        addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
//                clearContent();
//                markForDestroy(); // re-create window once it is closed
                hide();
            }
        });
    }

    protected void initializeWindow() {
        setShowModalMask(true);
        setIsModal(true);
        setTitle(i18n.login());
        setWidth(WIDTH);
        setHeight(HEIGHT);
        centerInPage();
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowMinimizeButton(false);
//        setMargin(10);
        addResizedHandler(new ResizedHandler() {
            @Override
            public void onResized(ResizedEvent event) {
                WIDTH = LoginWindow.this.getWidth();
                HEIGHT = LoginWindow.this.getHeight();
            }
        });
    }

    /**
     * Must be called from the subclass instance constructor (after this {@link LoginWindow} parent has
     * created) to render either login or the actual window content.
     */
    protected void initializeContent() {
        if (isNotLoggedIn()) {
            loadLoginContent();
        }
        else {
            loadWindowContent();
        }
    }
    
    private void clearAndLoadWindowContent() {
        clearContent();
        loadWindowContent();
    }

    /**
     * Loads the actual window content after successful login. Make sure you have called
     * {@link #clearContent()} beforehand.
     */
    protected abstract void loadWindowContent();

    public void loadLoginContent() {
        clearContent();
        setTitle(i18n.login());
        content = new VLayout();
        content.addMember(createUserLoginLayout());
        addItem(content);
        markForRedraw();
    }

    private void loadRegistrationContent() {
        clearContent();
        content = new RegisterLayout();
        addItem(content);
        markForRedraw();
    }

    protected void clearContent() {
        if (content != null) {
            removeItem(content);
        } 
    }

    private void loadResetPasswordContent() {
        clearContent();
        content = new ResetPasswordLayout();
        addItem(content);
        markForRedraw();
    }

    protected void updateWindowTitle(String newTitle) {
        setTitle(newTitle);
    }

    public String getId() {
        return COMPONENT_ID;
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
                window.loadLoginContent();
                return;
            }
            else if (evt.getRole() == ADMIN) {
                window.clearAndLoadWindowContent();
                window.hide(); // switch to admin UI
                DataPanel dataPanel = View.getView().getDataPanel();
                DataPanelTab sesTab = View.getView().getSesTab();
                dataPanel.getPanel().selectTab(sesTab);
                dataPanel.setCurrentTab(sesTab);
                dataPanel.update();
            }
            else {
                // user stays in modal window
                window.clearAndLoadWindowContent();
            }
        }

        @Override
        public void onChange(ChangeLayoutEvent evt) {
            if (evt.getLayout() == REGISTER) {
                window.loadRegistrationContent();
                window.updateWindowTitle(i18n.registration());
            }
            else if (evt.getLayout() == PASSWORD) {
                window.loadResetPasswordContent();
                window.updateWindowTitle(i18n.forgotPassword());
            }
            else if (evt.getLayout() == LOGIN) {
                window.loadLoginContent();
            }
        }
    }
}