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

package org.n52.client.ses.ui;

import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Basic form layout for SES client UI components.
 */
public abstract class FormLayout extends VLayout {

    protected HeaderItem headerItem;

    protected DynamicForm form;

    protected DynamicForm form2;

    protected LayoutSpacer spacer;

    private HLayout loggedInAsLayout;

    protected Label userNameLabel;

    /**
     * @param formHeaderText
     *        the header text this form layout shall have
     */
    public FormLayout(String formHeaderText) {
        this.form = new DynamicForm();
        this.form.setUseAllDataSourceFields(true);

        this.headerItem = new HeaderItem();
        this.headerItem.setDefaultValue(formHeaderText);

        this.spacer = new LayoutSpacer();
        this.spacer.setHeight(20);

        this.loggedInAsLayout = new HLayout();
        this.loggedInAsLayout.setWidth100();
        this.loggedInAsLayout.setAlign(Alignment.RIGHT);
        this.loggedInAsLayout.setHeight(20);

        this.userNameLabel = new Label("");
        this.userNameLabel.setWordWrap(false);

        this.loggedInAsLayout.addMember(this.userNameLabel);
        addMember(this.loggedInAsLayout);
    }

    public Label getUserNameLabel() {
        return this.userNameLabel;
    }

    public static enum LayoutType {
        LOGIN, REGISTER, PASSWORD, USERLIST, SENSORLIST, RULELIST, EDIT_PROFILE, ABOS, CREATE_SIMPLE, 
        EDIT_SIMPLE, CREATE_COMPLEX, EDIT_COMPLEX, EDIT_RULES, USER_SUBSCRIPTIONS, WELCOME, SEARCH;
    }

}