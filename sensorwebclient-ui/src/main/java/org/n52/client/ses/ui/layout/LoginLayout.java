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

package org.n52.client.ses.ui.layout;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.ctrl.DataControlsSes.createMD5;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.REGISTER;
import static org.n52.client.ui.View.getView;

import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.LoginEvent;
import org.n52.client.ses.ui.FormLayout;

import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

/**
 * The login view. With a valid user name and password the user can login to the client.
 */
public class LoginLayout extends FormLayout {

    private TextItem userNameItem;

    private PasswordItem passwordItem;
    
    public static LoginLayout createUserLoginLayout() {
        LoginLayout loginLayout = new LoginLayout(i18n.userLogin());
        loginLayout.initUserLogin();
        return loginLayout;
    }
    
    public static LoginLayout createAdminLoginLayout() {
        LoginLayout loginLayout = new LoginLayout(i18n.adminLogin());
        loginLayout.initAdminLogin();
        return loginLayout;
    }

    private LoginLayout(String loginTitle) {
        super(loginTitle);
        setStyleName("n52_sensorweb_client_form_content");
    }
    
    private void initUserLogin() {
        userNameItem = createUserNameItem();
        passwordItem = createPasswordItem();
        ButtonItem loginButton = createLoginButton();
        LinkItem registerLink = createRegisterLink();
        form.setFields(headerItem, userNameItem, passwordItem, loginButton, registerLink);
        addMember(form);
    }
    
    private void initAdminLogin() {
        addStyleName("n52_sensorweb_client_form_content n52_sensorweb_client_admin_login");
        userNameItem = createUserNameItem();
        passwordItem = createPasswordItem();
        ButtonItem loginButton = createLoginButton();
        LinkItem backToDiagramLink = createBackToDiagramLink();
        form.setFields(headerItem, userNameItem, passwordItem, loginButton, backToDiagramLink);
        addMember(form);
    }

    private TextItem createUserNameItem() {
        if (userNameItem == null) {
            userNameItem = new TextItem();
            userNameItem.setName("userName");
            userNameItem.setTitle(i18n.userName());
            userNameItem.setRequired(true);
            userNameItem.setSelectOnFocus(true);
            userNameItem.setLength(100);
            userNameItem.addKeyPressHandler(new KeyPressHandler() {
                public void onKeyPress(KeyPressEvent event) {
                    if ( (event.getKeyName().equals("Enter")) && (LoginLayout.this.form.validate(false))) {
                        login();
                    }
                }
            });
        }
        return userNameItem;
    }

    private PasswordItem createPasswordItem() {
        if (passwordItem == null) {
            passwordItem = new PasswordItem();
            passwordItem.setName("password");
            passwordItem.setTitle(i18n.password());
            passwordItem.setRequired(true);
            passwordItem.setLength(20);
            passwordItem.addKeyPressHandler(new KeyPressHandler() {
                public void onKeyPress(KeyPressEvent event) {
                    if ( (event.getKeyName().equals("Enter")) && (LoginLayout.this.form.validate(false))) {
                        login();
                    }
                }
            });
        }
        return passwordItem;
    }

    private ButtonItem createLoginButton() {
        ButtonItem loginButton = new ButtonItem();
        loginButton.setTitle(i18n.login());
        loginButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (LoginLayout.this.form.validate(false)) {
                    login();
                }
            }
        });
        return loginButton;
    }

    private void login() {
        String name = LoginLayout.this.userNameItem.getValueAsString();
        String pwd = LoginLayout.this.passwordItem.getValueAsString();
        getMainEventBus().fireEvent(new LoginEvent(name, createMD5(pwd)));
        clearFields();
    }

    private LinkItem createRegisterLink() {
        LinkItem registerLink = new LinkItem();
        registerLink.setShowTitle(false); // only link
        registerLink.setDefaultValue(i18n.register());
        registerLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getMainEventBus().fireEvent(new ChangeLayoutEvent(REGISTER));
            }
        });
        return registerLink;
    }

    private LinkItem createBackToDiagramLink() {
        LinkItem backToDiagramLink = new LinkItem();
        backToDiagramLink.setShowTitle(false); // only link
        backToDiagramLink.setDefaultValue(i18n.back());
        backToDiagramLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getView().getLegend().switchToDiagramTab();
            }
        });
        return backToDiagramLink;
    }
        
    public TextItem getNameItem() {
        return userNameItem;
    }

    public PasswordItem getPasswordItem() {
        return passwordItem;
    }

    public void update() {
        LoginLayout.this.form.validate();
    }

    public void clearFields() {
        userNameItem.clearValue();
        passwordItem.clearValue();
    }
}