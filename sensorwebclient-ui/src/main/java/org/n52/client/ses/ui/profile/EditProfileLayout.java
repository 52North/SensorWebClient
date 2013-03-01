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
package org.n52.client.ses.ui.profile;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
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

    @Deprecated
    private TextItem handyItem;

    private TextItem userNameItem;
    
    private TextItem nameItem;
    
    private int fieldWidth = 200;

    public EditProfileLayout() {
        super(i18n.editProfile());
        setStyleName("n52_sensorweb_client_form_content");

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

                    EventBus.getMainEventBus().fireEvent(new UpdateUserEvent(u));
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
                            getMainEventBus().fireEvent(new DeleteProfileEvent(getLoggedInUserId()));
                            getMainEventBus().fireEvent(new LogoutEvent());
                        }
                    }
                });
            }
        });

        this.form.setFields(this.headerItem, this.userNameItem, this.nameItem, this.newPasswordItem, newPasswordAgainItem, this.currentPasswordItem, this.emailItem,
                emailItem2, /* this.handyItem,*/ saveButton, deleteButton);

        addMember(this.form);
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
