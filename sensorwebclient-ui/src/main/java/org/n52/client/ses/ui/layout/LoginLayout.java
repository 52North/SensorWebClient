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
import static org.n52.client.ses.ui.FormLayout.LayoutType.PASSWORD;
import static org.n52.client.ses.ui.FormLayout.LayoutType.REGISTER;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_NAME;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_PASSWORD;

import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.LoginEvent;
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.util.ClientSessionManager;
import org.n52.shared.responses.SesClientResponse;

import com.smartgwt.client.widgets.form.FormItemErrorFormatter;
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

    private static final String USER_NAME_FIELD = "userName";
    
    private static final String PASSWORD_FIELD = "password";

    public static LoginLayout createUserLoginLayout() {
        LoginLayout loginLayout = new LoginLayout(i18n.userLogin());
        loginLayout.initUserLogin();
        return loginLayout;
    }
    
    private LoginLayout(String loginTitle) {
        super(loginTitle);
        setStyleName("n52_sensorweb_client_form_content");
    }
    
    private void initUserLogin() {
        new LoginLayoutEventBroker(this);
        TextItem userNameItem = createUserNameItem();
        PasswordItem passwordItem = createPasswordItem();
        ButtonItem loginButton = createLoginButton();
        LinkItem registerLink = createRegisterLink();
        LinkItem forgotPasswordLink = createForgotPasswordLink();
        form.setFields(headerItem, userNameItem, passwordItem, loginButton, registerLink, forgotPasswordLink);
        addMember(form);
    }
    
    private TextItem createUserNameItem() {
        TextItem userNameItem = new TextItem(USER_NAME_FIELD);
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
        return userNameItem;
    }

    private PasswordItem createPasswordItem() {
        PasswordItem passwordItem = new PasswordItem(PASSWORD_FIELD);
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
        String name = getUserNameField().getValueAsString();
        String pwd = getPasswordField().getValueAsString();
    	getMainEventBus().fireEvent(new LoginEvent(name, createMD5(pwd), currentSession()));
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
    
    private LinkItem createForgotPasswordLink() {
        LinkItem forgotPasswordLink = new LinkItem();
        forgotPasswordLink.setShowTitle(false); // only link
        forgotPasswordLink.setDefaultValue(i18n.forgotPassword());
        forgotPasswordLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                getMainEventBus().fireEvent(new ChangeLayoutEvent(PASSWORD));
            }
        });
        return forgotPasswordLink;
    }

    public void update() {
        LoginLayout.this.form.validate();
    }

    public void clearFields() {
        getUserNameField().clearValue();
        getPasswordField().clearValue();
    }

    private TextItem getUserNameField() {
        return (TextItem) form.getField(USER_NAME_FIELD);
    }

    private PasswordItem getPasswordField() {
        return (PasswordItem) form.getField(PASSWORD_FIELD);
    }

    private static class LoginLayoutEventBroker implements InformUserEventHandler {

        private LoginLayout loginLayout;

        LoginLayoutEventBroker(LoginLayout loginLayout) {
            this.loginLayout = loginLayout;
            getMainEventBus().addHandler(InformUserEvent.TYPE, this);
        }
        
        @Override
        public void onInform(InformUserEvent evt) {
            SesClientResponse reponse = evt.getResponse();
            if (reponse.getType() == LOGIN_NAME) {
                setErrorFormatter(loginLayout);
                loginLayout.update();
            } else if(reponse.getType() == LOGIN_PASSWORD) {
                loginLayout.getPasswordField().setValue("");
                setErrorFormatter(loginLayout);
                loginLayout.update();
            }
        }

        private void setErrorFormatter(final LoginLayout loginLayout) {
            setErrorFormatter(loginLayout.getUserNameField(), i18n.invalidName());
            setErrorFormatter(loginLayout.getPasswordField(), i18n.invalidPassword());
        }

        private void setErrorFormatter(final TextItem field, final String msg) {
            field.setErrorFormatter(new FormItemErrorFormatter() {
                public String getErrorHTML(String[] errors) {
                    String imgUrl = "../img/icons/exclamation.png";
                    return "<img src='" + imgUrl + "' alt='invalide name' title='" + msg + "'/>";
                }
            });
        }
    }
}