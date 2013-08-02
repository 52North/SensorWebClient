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
import org.n52.client.ses.event.NewPasswordEvent;
import org.n52.client.ses.ui.FormLayout;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;

/**
 * The Class ForgotPasswordLayout.
 * 
 * This view enables the function to send a new password to the client.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class ResetPasswordLayout extends FormLayout {

    /** The name item. */
    private TextItem nameItem;

    /** The email item. */
    private TextItem emailItem;

    /**
     * Instantiates a new forgot password layout.
     */
    public ResetPasswordLayout() {
        super(i18n.forgotPassword());
        
        setStyleName("n52_sensorweb_client_form_content");

        DataSource dataSource = new DataSource();

        DataSourceTextField nameField = new DataSourceTextField("name", i18n.userName(), 50, true);
        DataSourceTextField emailField = new DataSourceTextField("email", i18n.email(), 100, true);

        RegExpValidator emailValidator = new RegExpValidator();
        emailValidator.setErrorMessage("Invalid email address");
        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
        emailField.setValidators(emailValidator);

        dataSource.setFields(nameField, emailField);

        this.form.setDataSource(dataSource);

        this.nameItem = new TextItem();
        this.nameItem.setName("name");

        this.emailItem = new TextItem();
        this.emailItem.setName("email");

        // OK button
        ButtonItem validateItem = new ButtonItem();
        validateItem.setTitle(i18n.sendEmail());
        validateItem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (ResetPasswordLayout.this.form.validate()) {
                    String name = ResetPasswordLayout.this.nameItem.getValue().toString();
                    String email = ResetPasswordLayout.this.emailItem.getValue().toString();

                    EventBus.getMainEventBus().fireEvent(new NewPasswordEvent(name, email));
                }
            }
        });

        this.form.setFields(this.headerItem, this.nameItem, this.emailItem, validateItem);

        addMember(this.form);
    }

    /**
     * @return {@link TextItem}
     */
    public TextItem getNameItem() {
        return this.nameItem;
    }

    /**
     * @return {@link TextItem}
     */
    public TextItem getEmailItem() {
        return this.emailItem;
    }

    /**
     * update layout
     */
    public void update(){
        ResetPasswordLayout.this.form.validate();
    }

    /**
     *  clear all values
     */
    public void clearFields() {
        this.nameItem.clearValue();
        this.emailItem.clearValue();
    }
}