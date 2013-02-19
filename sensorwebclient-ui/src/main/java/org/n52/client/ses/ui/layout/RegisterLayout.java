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
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.shared.serializable.pojos.UserRole.NOT_REGISTERED_USER;

import java.util.Date;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ses.ctrl.DataControlsSes;
import org.n52.client.ses.ctrl.SesRequestManager;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.GetTermsOfUseEvent;
import org.n52.client.ses.event.RegisterUserEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.shared.serializable.pojos.UserDTO;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;

/**
 * This view is shown if a new user wants to register to the client.
 */
public class RegisterLayout extends FormLayout {

    private TextItem userNameItem;

    private TextItem nameItem;

    private PasswordItem passwordItem;

    private PasswordItem verifyPasswordItem;

    private TextItem emailItem;

    private TextItem verifyEmailItem;

    private CheckboxItem acceptBox;

    private LinkItem linkTerms;

    public RegisterLayout() {
        super(i18n.registration());
        setStyleName("n52_sensorweb_client_form_content");
        
        DataSourceTextField userNameField = new DataSourceTextField("userName", i18n.userName(), 50, true);
        DataSourceTextField nameField = new DataSourceTextField("name", i18n.name(), 50, false);
        DataSourcePasswordField passwordField = new DataSourcePasswordField("password", i18n.password(), 20, true);
        DataSourceTextField emailField = new DataSourceTextField("email", i18n.email(), 100, true);

        RegExpValidator emailValidator = new RegExpValidator();
        emailValidator.setErrorMessage(i18n.invalidEmail());
        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
        emailField.setValidators(emailValidator);

        DataSource dataSource = new DataSource();
        dataSource.setFields(userNameField, nameField, passwordField, emailField);
        this.form.setDataSource(dataSource);

        createNameTextInputs();
        createPasswordTextInputs();
        createEmailTextInputs();
        createAcceptTermsOfUse();
        ButtonItem registerButton = createRegisterButton();
        LinkItem backToLoginLink = createBackToLoginLink();
        this.form.setFields(this.headerItem, this.userNameItem, this.nameItem, this.passwordItem, this.verifyPasswordItem,
                this.emailItem, this.verifyEmailItem, this.linkTerms, this.acceptBox, registerButton, backToLoginLink);
        
        addMember(this.form);
        setAutoWidth();
    }

    

    private void createNameTextInputs() {
        this.userNameItem = new TextItem();
        this.userNameItem.setName("userName");
        this.userNameItem.setLength(250);
        
        this.nameItem = new TextItem();
        this.nameItem.setName("name");
        this.nameItem.setLength(250);
    }

    private void createPasswordTextInputs() {
        this.passwordItem = new PasswordItem();
        this.passwordItem.setName("password");
        this.passwordItem.setHint("<nobr>" + i18n.possibleChars() + " [0-9a-zA-Z_-]" + "</nobr>");
        this.passwordItem.setKeyPressFilter("[0-9a-zA-Z_-]");
        
        this.verifyPasswordItem = new PasswordItem();
        this.verifyPasswordItem.setName("verifyPassword");
        this.verifyPasswordItem.setTitle(i18n.passwordAgain());
        this.verifyPasswordItem.setRequired(true);
        this.verifyPasswordItem.setHint("<nobr>" + i18n.possibleChars() + " [0-9a-zA-Z_-]" + "</nobr>");
        this.verifyPasswordItem.setKeyPressFilter("[0-9a-zA-Z_-]");
        this.verifyPasswordItem.setLength(250);

        MatchesFieldValidator matchesValidator = new MatchesFieldValidator();
        matchesValidator.setOtherField("password");
        matchesValidator.setErrorMessage(i18n.passwordDoNotMatch());
        this.verifyPasswordItem.setValidators(matchesValidator);
    }

    private void createEmailTextInputs() {
        this.emailItem = new TextItem();
        this.emailItem.setName("email");
        this.emailItem.setLength(250);

        this.verifyEmailItem = new TextItem();
        this.verifyEmailItem.setName("verifyEmail");
        this.verifyEmailItem.setTitle(i18n.emailAgain());
        this.verifyEmailItem.setRequired(true);
        this.verifyEmailItem.setLength(250);

        MatchesFieldValidator matchesValidatorEmail = new MatchesFieldValidator();
        matchesValidatorEmail.setOtherField("email");
        matchesValidatorEmail.setErrorMessage(i18n.emailDoNotMatch());
        this.verifyEmailItem.setValidators(matchesValidatorEmail);
    }

    void createAcceptTermsOfUse() {
        // linkItem for terms of use
        this.linkTerms = new LinkItem("termsOfUse");
        this.linkTerms.setLinkTitle(i18n.termsOfUse());
        this.linkTerms.setShouldSaveValue(false);
        this.linkTerms.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new GetTermsOfUseEvent(PropertiesManager.language));
            }
        });

        this.acceptBox = new CheckboxItem();
        this.acceptBox.setName("acceptTerms");
        this.acceptBox.setTitle(i18n.acceptTermsOfUse());
        this.acceptBox.setValue(false);
        this.acceptBox.setRequired(true);
    }
    
    private ButtonItem createRegisterButton() {
        ButtonItem registerButton = new ButtonItem();
        registerButton.setTitle(i18n.register());
        registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                RegisterLayout.this.register();
            }
        });
        return registerButton;
    }

    protected void register() {
        form.validate(true);

        if (RegisterLayout.this.form.validate() && (Boolean) RegisterLayout.this.acceptBox.getValue()) {
            String userName = (String) RegisterLayout.this.userNameItem.getValue();
            String name = (String) RegisterLayout.this.nameItem.getValue();
            String password = DataControlsSes.createMD5((String) RegisterLayout.this.passwordItem.getValue());
            String eMail = (String) RegisterLayout.this.emailItem.getValue();
            boolean activated = false;
            
            if (name == null || name.equals("")) {
                name = "";
            }
            
            // delete cookie
            Cookies.removeCookie(SesRequestManager.COOKIE_USER_ID);
            Cookies.removeCookie(SesRequestManager.COOKIE_USER_ROLE);
            Cookies.removeCookie(SesRequestManager.COOKIE_USER_NAME);

            // create user without parameterId and register
            UserDTO u = new UserDTO(userName, name, password, eMail, "", NOT_REGISTERED_USER, activated, new Date());
            EventBus.getMainEventBus().fireEvent(new RegisterUserEvent(u));
        } else if (RegisterLayout.this.form.validate() && !(Boolean) RegisterLayout.this.acceptBox.getValue()) {
            SC.say(i18n.acceptTermsOfUseInfo());
        }
    }

    private LinkItem createBackToLoginLink() {
        LinkItem backToLoginLink = new LinkItem();
        backToLoginLink.setShowTitle(false); // only link
        backToLoginLink.setDefaultValue(i18n.userLogin());
        backToLoginLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
            }
        });
        return backToLoginLink;
    }

    public void clearFields() {
        this.userNameItem.clearValue();
        this.nameItem.clearValue();
        this.passwordItem.clearValue();
        this.verifyPasswordItem.clearValue();
        this.emailItem.clearValue();
        this.verifyEmailItem.clearValue();
        this.acceptBox.setValue(false);
    }

    public void setTermsOfUse(String termsOfUse) {
        SC.say(termsOfUse);
    }
}
