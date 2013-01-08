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

import static org.n52.client.ses.i18n.I18NStringsAccessor.i18n;

import org.n52.client.view.gui.elements.interfaces.Layout;
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
public class WelcomeLayout extends Layout {
    
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