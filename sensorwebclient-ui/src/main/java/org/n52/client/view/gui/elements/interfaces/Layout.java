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
package org.n52.client.view.gui.elements.interfaces;

import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The Class Layout.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public abstract class Layout extends VLayout {

    /**
     * The Enum Layouts.
     */
    public static enum Layouts {

        /** The LOGIN. */
        LOGIN,
        /** The REGISTER. */
        REGISTER,
        /** The PASSWORD. */
        PASSWORD,
        /** The USERLIST. */
        USERLIST,
        /** The SENSORLIST. */
        SENSORLIST,
        /** The RULELIST. */
        RULELIST,
        /** The EDI t_ profile. */
        EDIT_PROFILE,
        /** The ABOS. */
        ABOS,
        /** The CREAT e_ simple. */
        CREATE_SIMPLE,
        /** The EDI t_ simple. */
        EDIT_SIMPLE,
        /** The CREAT e_ complex. */
        CREATE_COMPLEX,
        /** The EDI t_ complex. */
        EDIT_COMPLEX,
        /** The EDI t_ rules. */
        EDIT_RULES,
        /** The EDI t_ rules. */
        USER_SUBSCRIPTIONS,
        /** The welcome page */
        WELCOME,
        /** The search */
        SEARCH
    }

    /** The header item. */
    protected HeaderItem headerItem;

    /** The form. */
    protected DynamicForm form;

    /** The form2. */
    protected DynamicForm form2;

    /** The spacer. */
    protected LayoutSpacer spacer;
    
    private HLayout loggedInAsLayout;
    
    protected Label userNameLabel;
    
    /**
     * Instantiates a new layout.
     * 
     * @param h
     *            the h
     */
    public Layout(String h) {
        this.form = new DynamicForm();
        this.form.setUseAllDataSourceFields(true);

        this.headerItem = new HeaderItem();
        this.headerItem.setDefaultValue(h);

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
//        addMember(this.spacer);
    }

    /**
     * @return Label
     */
    public Label getUserNameLabel() {
        return this.userNameLabel;
    }
}