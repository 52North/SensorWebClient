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
package org.n52.client.ses.layouts;

import static org.n52.client.ses.i18n.I18NStringsAccessor.i18n;

import java.util.Date;

import org.n52.client.eventBus.EventBus;
import org.n52.client.model.communication.requestManager.SesRequestManager;
import org.n52.client.ses.event.DeleteProfileEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.UpdateUserEvent;
import org.n52.client.view.gui.elements.controlsImpl.DataControlsSes;
import org.n52.client.view.gui.elements.interfaces.Layout;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;

/**
 * The Class EditProfileLayout.
 * 
 * Here the user can edit his personal user data
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class EditProfileLayout extends Layout {

    /** The password item. */
    private PasswordItem newPasswordItem;
    
    private PasswordItem currentPasswordItem;

    /** The email item. */
    private TextItem emailItem;

    /** The handy item. */
    private TextItem handyItem;

    /** The user name item. */
    private TextItem userNameItem;
    
    /** The name item. */
    private TextItem nameItem;
    
    private int fieldWidth = 200;

    /**
     * Instantiates a new edits the profile layout.
     */
    public EditProfileLayout() {
        super(i18n.editProfile());
        this.scClassName = "VLayout";

        DataSource dataSource = new DataSource();

//        DataSourceTextField usernameField = new DataSourceTextField("userName", i18nManager.i18nSESClient.userName(), 50, true);
//        DataSourceTextField nameField = new DataSourceTextField("name", i18nManager.i18nSESClient.name(), 50, true);
//        DataSourcePasswordField passwordField = new DataSourcePasswordField("password", i18nManager.i18nSESClient.password(), 20, true);
//        DataSourceTextField emailField = new DataSourceTextField("email", i18nManager.i18nSESClient.email(), 100, true);
//        DataSourceIntegerField handyField = new DataSourceIntegerField("handy", i18nManager.i18nSESClient.handy(), 20, true);

//        RegExpValidator emailValidator = new RegExpValidator();
//        emailValidator.setErrorMessage(i18nManager.i18nSESClient.invalidEmail());
//        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
//        emailField.setValidators(emailValidator);
//
//        dataSource.setFields(usernameField, nameField, passwordField, emailField, handyField);

        this.form.setDataSource(dataSource);

        this.userNameItem = new TextItem();
        this.userNameItem.setName("userName");
        this.userNameItem.setTitle(i18n.userName());
        this.userNameItem.setRequired(true);
        this.userNameItem.setWidth(this.fieldWidth);
        
        this.nameItem = new TextItem();
        this.nameItem.setName("name");
        this.nameItem.setTitle(i18n.name());
        this.nameItem.setWidth(this.fieldWidth);
        
        this.currentPasswordItem = new PasswordItem();
        this.currentPasswordItem.setName("oldPassword");
        this.currentPasswordItem.setTitle(i18n.currentPassword());
        this.currentPasswordItem.setRequired(true);
        this.currentPasswordItem.setWidth(this.fieldWidth);

        this.newPasswordItem = new PasswordItem();
        this.newPasswordItem.setName("newPassword");
        this.newPasswordItem.setTitle(i18n.newPassword());
        this.newPasswordItem.setWidth(this.fieldWidth);
        
        // repeat password
        PasswordItem newPasswordAgainItem = new PasswordItem();
        newPasswordAgainItem.setName("password2");
        newPasswordAgainItem.setTitle(i18n.passwordAgain());
        newPasswordAgainItem.setLength(250);
        newPasswordAgainItem.setWidth(this.fieldWidth);

        // email
        this.emailItem = new TextItem();
        this.emailItem.setName("email");
        this.emailItem.setTitle(i18n.email());
        this.emailItem.setRequired(true);
        this.emailItem.setWidth(this.fieldWidth);

        // repeat email
        TextItem emailItem2 = new TextItem();
        emailItem2.setName("email2");
        emailItem2.setTitle(i18n.emailAgain());
        emailItem2.setRequired(true);
        emailItem2.setLength(250);
        emailItem2.setWidth(this.fieldWidth);

        // email validator
        MatchesFieldValidator matchesValidatorEmail = new MatchesFieldValidator();
        matchesValidatorEmail.setOtherField("email");
        matchesValidatorEmail.setErrorMessage(i18n.emailDoNotMatch());
        emailItem2.setValidators(matchesValidatorEmail);

        // password validator
        MatchesFieldValidator matchesValidator = new MatchesFieldValidator();
        matchesValidator.setOtherField("newPassword");
        matchesValidator.setErrorMessage(i18n.passwordDoNotMatch());
        newPasswordAgainItem.setValidators(matchesValidator);

        RegExpValidator regExpValidator = new RegExpValidator();  
        regExpValidator.setExpression("^([+])+([0-9\\])");
        
        // handy item
        this.handyItem = new TextItem();
        this.handyItem.setName("handy");
        this.handyItem.setTitle(i18n.handy());
        this.handyItem.setKeyPressFilter("[0-9+]");
        this.handyItem.setLength(250);
        this.handyItem.setWidth(this.fieldWidth);
//        this.handyItem.setValidators(regExpValidator);

        // save button
        ButtonItem saveButton = new ButtonItem();
        saveButton.setTitle(i18n.saveChanges());
        saveButton.setWidth(135);
        saveButton.setAutoFit(true);
        saveButton.addClickHandler(new ClickHandler() {
            @SuppressWarnings("synthetic-access")
            public void onClick(ClickEvent event) {
                if (EditProfileLayout.this.form.validate(false)) {
                    UserRole userRole;
                    String role = Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE);
                    if (role.equals(UserRole.ADMIN.toString())) {
                        userRole = UserRole.ADMIN;
                    } else {
                        userRole = UserRole.USER;
                    }
                    
                    String password = DataControlsSes.createMD5(EditProfileLayout.this.form.getValueAsString("oldPassword"));

                    UserDTO u =
                        new UserDTO(Integer.parseInt(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID)),
                                EditProfileLayout.this.form.getValueAsString("userName"),
                                EditProfileLayout.this.form.getValueAsString("name"), 
                                password, 
                                EditProfileLayout.this.form.getValueAsString("email"), 
                                EditProfileLayout.this.form.getValueAsString("handy"), 
                                userRole, new Date());
                    u.setActivated(true);

                    if (EditProfileLayout.this.newPasswordItem.getValue() != null) {
                        u.setNewPassword(DataControlsSes.createMD5(EditProfileLayout.this.newPasswordItem.getValueAsString()));
                    }

                    EventBus.getMainEventBus().fireEvent(new UpdateUserEvent(u, Cookies.getCookie(SesRequestManager.COOKIE_USER_ID)));
                }
            }
        });

        ButtonItem deleteButton = new ButtonItem();
        deleteButton.setTitle(i18n.deleteProfile());
        deleteButton.setWidth(saveButton.getWidth());
        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                SC.ask(i18n.reallyDeleteProfile(), new BooleanCallback() {
                    public void execute(Boolean value) {
                        if (value) {
                            EventBus.getMainEventBus().fireEvent(
                                    new DeleteProfileEvent(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID)));
                            EventBus.getMainEventBus().fireEvent(new LogoutEvent());
                        }
                    }
                });
            }
        });

        this.form.setFields(this.headerItem, this.userNameItem, this.nameItem, this.newPasswordItem, newPasswordAgainItem, this.currentPasswordItem, this.emailItem,
                emailItem2, this.handyItem, saveButton, deleteButton);

        addMember(this.form);
    }

    /**
     * Update.
     * 
     * @param user
     *            the user
     */
    public void update(UserDTO user) {
        this.form.setValue("userName", user.getUserName());
        this.form.setValue("name", user.getName());
        this.form.setValue("email", user.geteMail());
        this.form.setValue("email2", user.geteMail());
        this.form.clearValue("newPassword");
        this.form.clearValue("password2");
        this.form.clearValue("oldPassword");
        this.form.setValue("handy", user.getHandyNr());
    }
}
