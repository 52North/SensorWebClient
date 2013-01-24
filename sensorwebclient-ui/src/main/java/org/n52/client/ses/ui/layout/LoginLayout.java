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

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.DataControlsSes;
import org.n52.client.ses.event.LoginEvent;
import org.n52.client.ses.ui.Layout;

import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

/**
 * The Class LoginLayout.
 * 
 * The login view. With a valid user name and password the user can
 * login to the client.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class LoginLayout extends Layout {

    /** The name item. */
    private TextItem nameItem;

    /** The password item. */
    private PasswordItem passwordItem;

    /**
     * Instantiates a new login layout.
     */
    public LoginLayout() {
        super(i18n.login());
        setStyleName("n52_sensorweb_client_login_content");
        init();
    }

    /**
     * Inits the layout.
     */
    private void init() {

        // NameItem
        this.nameItem = new TextItem();
        this.nameItem.setName("userName");
        this.nameItem.setTitle(i18n.userName());
        this.nameItem.setRequired(true);
        this.nameItem.setSelectOnFocus(true);
        this.nameItem.setLength(100);
        this.nameItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if((event.getKeyName().equals("Enter"))&&(LoginLayout.this.form.validate(false))){
                    login();
                }
            }
        });

        // PasswordItem
        this.passwordItem = new PasswordItem();
        this.passwordItem.setName("password");
        this.passwordItem.setTitle(i18n.password());
        this.passwordItem.setRequired(true);
        this.passwordItem.setLength(20);
        this.passwordItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if((event.getKeyName().equals("Enter"))&&(LoginLayout.this.form.validate(false))){
                    login();
                }
            }
        });

        // Login button
        ButtonItem validateItem = new ButtonItem();
        validateItem.setTitle(i18n.login());
        validateItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (LoginLayout.this.form.validate(false)) {
                    login();
                }
            }
        });

        this.form.setFields(this.headerItem, this.nameItem, this.passwordItem, validateItem);

        addMember(this.form);
    }

    private void login(){
        String name = LoginLayout.this.nameItem.getValue().toString();
        Object o = LoginLayout.this.passwordItem.getValue();

        EventBus.getMainEventBus().fireEvent(new LoginEvent(name, DataControlsSes.createMD5(o.toString())));
        
        clearFields();
    }

    /**
     * @return {@link TextItem}
     */
    public TextItem getNameItem() {
        return this.nameItem;
    }

    /**
     * @return {@link PasswordItem}
     */
    public PasswordItem getPasswordItem() {
        return this.passwordItem;
    }

    /**
     * 
     */
    public void update(){
        LoginLayout.this.form.validate();
    }
    
    /**
     * clear all fields
     */
    public void clearFields(){
        this.nameItem.clearValue();
        this.passwordItem.clearValue();
    }
}