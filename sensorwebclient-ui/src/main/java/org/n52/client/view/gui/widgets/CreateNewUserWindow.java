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
package org.n52.client.view.gui.widgets;

import static org.n52.client.ses.i18n.I18NStringsAccessor.i18n;

import java.util.Date;

import org.n52.client.eventBus.EventBus;
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

/**
 * The Class CreateNewUserWindow.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class CreateNewUserWindow {

    /**
     * Inits the.
     */
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

        // password
//        final PasswordItem passwordItem = new PasswordItem();
//        passwordItem.setTitle(i18nManager.i18nSESClient.password());
//        passwordItem.setName("password");

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

        // handy
        final TextItem handyItem = new TextItem();
        handyItem.setName("handy");
        handyItem.setTitle(i18n.handy());
        handyItem.setKeyPressFilter("[0-9+]");
        // this.handyItem.setHint("Numeric only<br>[0-9]");
        handyItem.setLength(length);

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
                    String handy = (String) handyItem.getValue();
                    UserRole role;
                    
                    if (name == null || name.equals("")) {
                        name = "";
                    }
                    
                    if (handy == null || handy.equals("")) {
                        handy = "";
                    }

                    String roleString = (String) roleItem.getValue();
                    if (roleString.equals(UserRole.ADMIN.toString())) {
                        role = UserRole.ADMIN;
                    } else {
                        role = UserRole.NOT_REGISTERED_USER;
                    }

                    // create new User
                    UserDTO user = new UserDTO(userName, name, password, email, handy, role, false, new Date());
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
        form.setFields(spacer, userNameItem, nameItem, emailItem, handyItem, roleItem, createItem);

        // add form to window
        window.addItem(form);
        window.draw();
    }

}
