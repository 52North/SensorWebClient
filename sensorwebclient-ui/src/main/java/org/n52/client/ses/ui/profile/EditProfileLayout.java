/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ses.ui.profile;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserId;

import java.util.Date;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.DataControlsSes;
import org.n52.client.ses.event.DeleteProfileEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.UpdateUserEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.util.ClientSessionManager;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.SessionInfo;

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

public class EditProfileLayout extends FormLayout {

    private PasswordItem newPasswordItem;
    
    private PasswordItem currentPasswordItem;

    private TextItem emailItem;

    private TextItem userNameItem;
    
    private TextItem nameItem;
    
    private int fieldWidth = 200;

    public EditProfileLayout() {
        super(i18n.editProfile());

        DataSource dataSource = new DataSource();
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
        
        // save button
        ButtonItem saveButton = new ButtonItem();
        saveButton.setTitle(i18n.saveChanges());
        saveButton.setWidth(135);
        saveButton.setAutoFit(true);
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (EditProfileLayout.this.form.validate(false)) {
                    UserRole userRole = null;
                    if (ClientSessionManager.isAdminLogin()) {
                        userRole = UserRole.ADMIN;
                    } 
                    else if (ClientSessionManager.isUserLogin()) {
                        userRole = UserRole.USER;
                    }
                    
                    String password = DataControlsSes.createMD5(EditProfileLayout.this.form.getValueAsString("oldPassword"));

                    UserDTO u =
                        new UserDTO(Integer.parseInt(getLoggedInUserId()),
                                EditProfileLayout.this.form.getValueAsString("userName"),
                                EditProfileLayout.this.form.getValueAsString("name"), 
                                password, 
                                EditProfileLayout.this.form.getValueAsString("email"), 
                                userRole, new Date());
                    u.setActivated(true);

                    if (EditProfileLayout.this.newPasswordItem.getValue() != null) {
                        u.setNewPassword(DataControlsSes.createMD5(EditProfileLayout.this.newPasswordItem.getValueAsString()));
                    }

                    EventBus.getMainEventBus().fireEvent(new UpdateUserEvent(currentSession(), u));
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
                            final SessionInfo sessionInfo = currentSession();
                            getMainEventBus().fireEvent(new DeleteProfileEvent(sessionInfo, getLoggedInUserId()));
                            getMainEventBus().fireEvent(new LogoutEvent(sessionInfo));
                        }
                    }
                });
            }
        });

        this.form.setFields(this.headerItem, this.userNameItem, this.nameItem, this.newPasswordItem, newPasswordAgainItem, this.currentPasswordItem, this.emailItem,
                emailItem2, saveButton, deleteButton);

        addMember(this.form);
    }

    public void clearValues() {
        form.clearValues();
    }
    
    public void update(UserDTO user) {
        this.form.setValue("userName", user.getUserName());
        this.form.setValue("name", user.getName());
        this.form.setValue("email", user.geteMail());
        this.form.setValue("email2", user.geteMail());
        this.form.clearValue("newPassword");
        this.form.clearValue("password2");
        this.form.clearValue("oldPassword");
    }
}
