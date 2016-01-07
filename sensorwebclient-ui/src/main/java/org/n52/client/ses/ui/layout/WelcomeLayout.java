/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ses.ui.layout;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.ses.ui.FormLayout;
import org.n52.shared.serializable.pojos.UserDTO;

import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;

/**
 * The Class AllRulesLayout.
 * 
 * The welcome layout is the default layout on startup.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class WelcomeLayout extends FormLayout {
    
    private StaticTextItem welcomeText;
    
    private StaticTextItem roleText;

    /**
     * Instantiates a welcome layout.
     */
    public WelcomeLayout() {
        super(i18n.welcomeText());
        this.scClassName = "VLayout";
        
        SpacerItem spacerItem = new SpacerItem();
        spacerItem.setHeight(20);

        this.welcomeText = new StaticTextItem("welcomeText");
        this.welcomeText.setShowTitle(false);
        this.welcomeText.setValue("");
        
        this.roleText = new StaticTextItem("roleText");
        this.roleText.setShowTitle(false);
        this.roleText.setValue("");
        
        this.form.setFields(this.headerItem, spacerItem, spacerItem, this.welcomeText, spacerItem, this.roleText);
        // add to mainLayout
        addMember(this.form);
    }
    
    /**
     * Set the welcome text to the layout
     * 
     * @param userDTO
     */
    public void setData(UserDTO userDTO){

        this.welcomeText.setValue(i18n.welcome() + " " + userDTO.getName());
        this.roleText.setValue(i18n.welcomeUserRole() + ": " + userDTO.getRole());
    }
}