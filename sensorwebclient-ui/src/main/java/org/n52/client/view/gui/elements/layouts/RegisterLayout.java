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
package org.n52.client.view.gui.elements.layouts;

import java.util.Date;

import org.n52.client.control.PropertiesManager;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.ses.GetTermsOfUseEvent;
import org.n52.client.eventBus.events.ses.RegisterUserEvent;
import org.n52.client.i18n.I18N;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.view.gui.elements.controlsImpl.DataControlsSes;
import org.n52.client.view.gui.elements.interfaces.Layout;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;

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
 * The Class RegisterLayout.
 * 
 * This view is shown if a new user wants to register to the client.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class RegisterLayout extends Layout {

    /** The user name item. */
    private TextItem userNameItem;

    /** The name item. */
    private TextItem nameItem;

    /** The password item. */
    private PasswordItem passwordItem;

    /** The password item 2. */
    private PasswordItem passwordItem2;

    /** The email item. */
    private TextItem emailItem;

    /** The email item 2. */
    private TextItem emailItem2;

    /** The handy item. */
    private TextItem handyItem;

    /** The accept box. */
    private CheckboxItem acceptBox;

    /** The link terms. */
    private LinkItem linkTerms;

    /**
     * Instantiates a new register layout.
     */
    public RegisterLayout() {
        super(I18N.sesClient.registration());
        this.scClassName = "VLayout";

        DataSource dataSource = new DataSource();

        DataSourceTextField userNameField = new DataSourceTextField("userName", I18N.sesClient.userName(), 50, true);
        DataSourceTextField nameField = new DataSourceTextField("name", I18N.sesClient.name(), 50, false);
        DataSourcePasswordField passwordField = new DataSourcePasswordField("password", I18N.sesClient.password(), 20, true);
        DataSourceTextField emailField = new DataSourceTextField("email", I18N.sesClient.email(), 100, true);
        DataSourceTextField handyField = new DataSourceTextField("handy", I18N.sesClient.handy(), 20, false);

        RegExpValidator emailValidator = new RegExpValidator();
        emailValidator.setErrorMessage(I18N.sesClient.invalidEmail());
        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
        emailField.setValidators(emailValidator);

        dataSource.setFields(userNameField, nameField, passwordField, emailField, handyField);

        this.form.setDataSource(dataSource);

        // user name
        this.userNameItem = new TextItem();
        this.userNameItem.setName("userName");
        this.userNameItem.setLength(250);

        // name
        this.nameItem = new TextItem();
        this.nameItem.setName("name");
        this.nameItem.setLength(250);

        // password
        this.passwordItem = new PasswordItem();
        this.passwordItem.setName("password");
        this.passwordItem.setHint("<nobr>" + I18N.sesClient.possibleChars() + " [0-9 a-z A-Z _ -]" + "</nobr>");
        this.passwordItem.setKeyPressFilter("[0-9 a-z A-Z _ -]");
        
        // repeat password
        this.passwordItem2 = new PasswordItem();
        this.passwordItem2.setName("password2");
        this.passwordItem2.setTitle(I18N.sesClient.passwordAgain());
        this.passwordItem2.setRequired(true);
        this.passwordItem2.setHint("<nobr>" + I18N.sesClient.possibleChars() + " [0-9 a-z A-Z _ -]" + "</nobr>");
        this.passwordItem2.setKeyPressFilter("[0-9 a-z A-Z _ -]");
        this.passwordItem2.setLength(250);

        // email
        this.emailItem = new TextItem();
        this.emailItem.setName("email");
        this.emailItem.setLength(250);

        // repeat email
        this.emailItem2 = new TextItem();
        this.emailItem2.setName("email2");
        this.emailItem2.setTitle(I18N.sesClient.emailAgain());
        this.emailItem2.setRequired(true);
        this.emailItem2.setLength(250);

        // email validator
        MatchesFieldValidator matchesValidatorEmail = new MatchesFieldValidator();
        matchesValidatorEmail.setOtherField("email");
        matchesValidatorEmail.setErrorMessage(I18N.sesClient.emailDoNotMatch());
        this.emailItem2.setValidators(matchesValidatorEmail);

        // password validator
        MatchesFieldValidator matchesValidator = new MatchesFieldValidator();
        matchesValidator.setOtherField("password");
        matchesValidator.setErrorMessage(I18N.sesClient.passwordDoNotMatch());
        this.passwordItem2.setValidators(matchesValidator);

        // handy
        this.handyItem = new TextItem();
        this.handyItem.setName("handy");
        this.handyItem.setTitle(I18N.sesClient.handy());
        this.handyItem.setKeyPressFilter("[0-9+]");
        // this.handyItem.setHint("Numeric only<br>[0-9]");
        this.handyItem.setLength(250);

        // linkItem for terms of use
        this.linkTerms = new LinkItem("");
        this.linkTerms.setLinkTitle(I18N.sesClient.termsOfUse());
        this.linkTerms.setShouldSaveValue(false);
        this.linkTerms.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new GetTermsOfUseEvent(PropertiesManager.language));
            }
        });

        // acceptBox
        this.acceptBox = new CheckboxItem();
        this.acceptBox.setName("acceptTerms");
        this.acceptBox.setTitle(I18N.sesClient.acceptTermsOfUse());
        // this.acceptBox.setRequired(true);
        this.acceptBox.setValue(false);
        this.acceptBox.setWidth(120);
        this.acceptBox.setRequired(true);

        ButtonItem validateItem = new ButtonItem();
        validateItem.setTitle(I18N.sesClient.register());
        validateItem.addClickHandler(new ClickHandler() {
            @SuppressWarnings("synthetic-access")
            public void onClick(ClickEvent event) {
                RegisterLayout.this.form.validate(true);

                if (RegisterLayout.this.form.validate() && (Boolean) RegisterLayout.this.acceptBox.getValue()) {
                    String userName = (String) RegisterLayout.this.userNameItem.getValue();
                    String name = (String) RegisterLayout.this.nameItem.getValue();
                    String password = DataControlsSes.createMD5((String) RegisterLayout.this.passwordItem.getValue());
                    String eMail = (String) RegisterLayout.this.emailItem.getValue();
                    String handyNr = (String) RegisterLayout.this.handyItem.getValue();
                    UserRole role = UserRole.NOT_REGISTERED_USER;
                    boolean activated = false;
                    
                    if (handyNr == null || handyNr.equals("")) {
                        handyNr = "";
                    }
                    if (name == null || name.equals("")) {
                        name = "";
                    }
                    
                    // delete cookie
                    Cookies.removeCookie(SesRequestManager.COOKIE_USER_ID);
                    Cookies.removeCookie(SesRequestManager.COOKIE_USER_ROLE);
                    Cookies.removeCookie(SesRequestManager.COOKIE_USER_NAME);

                    // create user without parameterId and register
                    UserDTO u = new UserDTO(userName, name, password, eMail, handyNr, role, activated, new Date());
                    EventBus.getMainEventBus().fireEvent(new RegisterUserEvent(u));
                } else if (RegisterLayout.this.form.validate() && !(Boolean) RegisterLayout.this.acceptBox.getValue()) {
                    SC.say(I18N.sesClient.acceptTermsOfUseInfo());
                }
            }
        });

        this.form.setFields(this.headerItem, this.userNameItem, this.nameItem, this.passwordItem, this.passwordItem2,
                this.emailItem, this.emailItem2, this.handyItem, this.linkTerms, this.acceptBox, validateItem);

        addMember(this.form);
    }

    /**
     *  clear all values
     */
    public void clearFields() {
        this.userNameItem.clearValue();
        this.nameItem.clearValue();
        this.passwordItem.clearValue();
        this.passwordItem2.clearValue();
        this.emailItem.clearValue();
        this.emailItem2.clearValue();
        this.handyItem.clearValue();
        this.acceptBox.setValue(false);
    }

    /**
     * @param termsOfUse
     */
    public void setTermsOfUse(String termsOfUse) {
        SC.say(termsOfUse);
    }
}