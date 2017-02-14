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
package org.n52.client.ses.ui;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.Date;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.NewPasswordEvent;
import org.n52.client.ses.event.RegisterUserEvent;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;

public class CreateNewUserWindow {

    public static void init() {
        int length = 250;

        // init window
        Window window = new Window();
        window.setWidth(300);
        window.setHeight(250);
        window.setAutoSize(true);
        window.setTitle(i18n.createNewUser());
        window.setCanDragReposition(true);
        window.setCanDragResize(true);
        window.centerInPage();

        // init form
        final DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setAlign(Alignment.CENTER);

        // init widgets

        // label
        SpacerItem spacer = new SpacerItem();

        // user name
        final TextItem userNameItem = new TextItem();
        userNameItem.setTitle(i18n.userName());
        userNameItem.setName("userName");
        userNameItem.setLength(length);
        userNameItem.setRequired(true);

        // name
        final TextItem nameItem = new TextItem();
        nameItem.setTitle(i18n.name());
        nameItem.setName("name");
        nameItem.setLength(length);

        // email
        DataSource dataSource = new DataSource();
        DataSourceTextField emailField = new DataSourceTextField("email", i18n.email(), 100, true);
        
        RegExpValidator emailValidator = new RegExpValidator();
        emailValidator.setErrorMessage(i18n.invalidEmail());
        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
        emailField.setValidators(emailValidator);
        
        dataSource.setFields(emailField);
        form.setDataSource(dataSource);
        
        final TextItem emailItem = new TextItem();
        emailItem.setTitle(i18n.email());
        emailItem.setName("email");
        emailItem.setLength(length);
        emailItem.setRequired(true);

        // role
        final SelectItem roleItem = new SelectItem();
        roleItem.setName("role");
        roleItem.setTitle(i18n.role());
        roleItem.setValueMap(UserRole.NOT_REGISTERED_USER.toString(), UserRole.ADMIN.toString());
        roleItem.setRequired(true);

        ButtonItem createItem = new ButtonItem();
        createItem.setTitle(i18n.create());
        createItem.setAlign(Alignment.CENTER);
        createItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (form.validate(false)) {
                    final String userName = (String) userNameItem.getValue();
                    String name = (String) nameItem.getValue();
                    String password = "";
                    final String email = (String) emailItem.getValue();
                    UserRole role;
                    
                    if (name == null || name.equals("")) {
                        name = "";
                    }
                    
                    String roleString = (String) roleItem.getValue();
                    if (roleString.equals(UserRole.ADMIN.toString())) {
                        role = UserRole.ADMIN;
                    } else {
                        role = UserRole.NOT_REGISTERED_USER;
                    }

                    // create new User
                    UserDTO user = new UserDTO(userName, name, password, email, role, false, new Date());
                    EventBus.getMainEventBus().fireEvent(new RegisterUserEvent(user));
                    
                    Timer timer = new Timer() {
                        public void run() {
                            EventBus.getMainEventBus().fireEvent(new NewPasswordEvent(userName, email));
                        }
                    };
                    timer.schedule(5000);
                }
            }
        });

        // set Fields to form
        form.setFields(spacer, userNameItem, nameItem, emailItem, roleItem, createItem);

        // add form to window
        window.addItem(form);
        window.draw();
    }

}
