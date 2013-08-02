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

import static org.n52.client.ses.ui.layout.LoginLayout.createUserLoginLayout;

import org.n52.client.ctrl.DataControls;
import org.n52.client.ses.ctrl.SesTabController;
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ses.ui.layout.LoginLayout;
import org.n52.client.ses.ui.layout.RegisterLayout;
import org.n52.client.ses.ui.layout.ResetPasswordLayout;
import org.n52.client.ses.ui.layout.SearchLayout;
import org.n52.client.ses.ui.layout.ShowUserLayout;
import org.n52.client.ses.ui.layout.WelcomeLayout;
import org.n52.client.ses.ui.rules.AllRulesLayout;
import org.n52.client.ses.ui.rules.CreateComplexRuleLayout;
import org.n52.client.ses.ui.rules.EditRulesLayout;
import org.n52.client.ses.ui.rules.UserRuleLayout;
import org.n52.client.ui.DataPanelTab;

import com.smartgwt.client.widgets.layout.VLayout;

public class SesTab extends DataPanelTab {

    private String title;

    private String id;

    private VLayout layout;

    private RegisterLayout registerLayout;

    private LoginLayout loginLayout;

    private ResetPasswordLayout forgorPasswordLayout;

    private ShowUserLayout allUserLayout;

    private UserRuleLayout ruleLayout;

    private CreateComplexRuleLayout complexLayout;

//    private EditProfileLayout editProfileLayout;

    private AllRulesLayout allRulesLayout;

    private EditRulesLayout editRulesLayout;
    
//    private SubscriptionsLayout userSubscriptionsLayout;
    
    private WelcomeLayout welcomeLayout;
    
    private SearchLayout searchLayout;
    
    private SesTabController controller;

    public SesTab(String id, String title) {
        super("SesTab");
        this.controller = new SesTabController(this);

        this.id = id;
        this.title = title;
        init();
        
        
    }

    private void init() {
        setID(this.id);
        setTitle(this.title);

        this.layout = new VLayout();
        this.layout.setTabIndex(-1);
        this.layout.setCanAcceptDrop(true);

        this.loginLayout = createUserLoginLayout();
        this.loginLayout.setCanAcceptDrop(true);

        this.registerLayout = new RegisterLayout();
        this.registerLayout.setCanAcceptDrop(true);

        this.forgorPasswordLayout = new ResetPasswordLayout();
        this.forgorPasswordLayout.setCanAcceptDrop(true);

        this.allUserLayout = new ShowUserLayout();
        this.allUserLayout.setCanAcceptDrop(true);

        this.ruleLayout = new UserRuleLayout();
        this.ruleLayout.setCanAcceptDrop(true);

        this.complexLayout = new CreateComplexRuleLayout();
        this.complexLayout.setCanAcceptDrop(true);

//        this.editProfileLayout = new EditProfileLayout();
//        this.editProfileLayout.setCanAcceptDrop(true);

//        this.simpleRuleLayout = new CreateSimpleRuleLayout();
//        this.simpleRuleLayout.setCanAcceptDrop(true);

        this.allRulesLayout = new AllRulesLayout();
        this.allRulesLayout.setCanAcceptDrop(true);

        this.editRulesLayout = new EditRulesLayout();
        this.editRulesLayout.setCanAcceptDrop(true);
        
//        this.userSubscriptionsLayout = new SubscriptionsLayout();
//        this.userSubscriptionsLayout.setCanAcceptDrop(true);
        
        this.welcomeLayout = new WelcomeLayout();
        this.welcomeLayout.setCanAcceptDrop(true);
        
        this.searchLayout = new SearchLayout();
        this.searchLayout.setCanAcceptDrop(true);

        setIcon("../img/icons/table.png");
        setPane(this.layout);
        this.layout.setMembers(this.loginLayout);
        controller.getDataControls().highlightSelectedButton(controller.getDataControls().getLoginButton());
    }

    /**
     * Sets the layout.
     * 
     * @param newLayout
     *            the new layout
     */
    public void setLayout(LayoutType newLayout) {
        switch (newLayout) {
        case LOGIN:
            this.layout.setMembers(this.loginLayout);
            break;

        case REGISTER:
            this.layout.setMembers(this.registerLayout);
            break;

        case PASSWORD:
            this.layout.setMembers(this.forgorPasswordLayout);
            break;
            
        case WELCOME:
            this.layout.setMembers(this.welcomeLayout);
            break;

        case USERLIST:
            this.layout.setMembers(this.allUserLayout);
            break;

        case RULELIST:
            this.layout.setMembers(this.allRulesLayout);
            break;

        case ABOS:
            this.layout.setMembers(this.ruleLayout);
            break;

//        case EDIT_PROFILE:
//            this.layout.setMembers(this.editProfileLayout);
//            break;

            // TODO remove dead code
//        case CREATE_SIMPLE:
//            this.layout.setMembers(this.simpleRuleLayout);
//            break;

        case EDIT_SIMPLE:
            break;

        case CREATE_COMPLEX:
            this.layout.setMembers(this.complexLayout);
            break;

        case EDIT_COMPLEX:
            break;

        case EDIT_RULES:
            this.layout.setMembers(this.editRulesLayout);
            break;
            
//        case USER_SUBSCRIPTIONS:
//            this.layout.setMembers(this.userSubscriptionsLayout);
//            break;
            
        case SEARCH:
            this.layout.setMembers(this.searchLayout);
            break;
            
        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.elements.interfaces.DataPanelTab#getDataControls
     * ()
     */
    @Override
    public DataControls getDataControls() {
        return this.controller.getControls();
    }

    /**
     * @return {@link ShowUserLayout}
     */
    public ShowUserLayout getShowUserLayout() {
        return this.allUserLayout;
    }

    /**
     * @return {@link RegisterLayout}
     */
    public RegisterLayout getRegisterLayout() {
        return this.registerLayout;
    }

    /**
     * @return {@link LoginLayout}
     */
    public LoginLayout getLoginLayout() {
        return this.loginLayout;
    }

    /**
     * @return {@link ResetPasswordLayout}
     */
    public ResetPasswordLayout getForgorPasswordLayout() {
        return this.forgorPasswordLayout;
    }

    // TODO remove dead code
//    /**
//     * @return {@link CreateSimpleRuleLayout}
//     */
//    public CreateSimpleRuleLayout getSimpleRuleLayout() {
//        return this.simpleRuleLayout;
//    }

    /**
     * @return {@link UserRuleLayout}
     */
    public UserRuleLayout getRuleLayout() {
        return this.ruleLayout;
    }

    /**
     * @return {@link FormLayout}
     */
    public FormLayout getCurrentLayout(){
        return (FormLayout)this.layout.getMember(0);
    }

    /**
     * @return {@link EditRulesLayout}
     */
    public EditRulesLayout getEditRulesLayout() {
        return this.editRulesLayout;
    }

    /**
     * @return {@link CreateComplexRuleLayout}
     */
    public CreateComplexRuleLayout getComplexLayout() {
        return this.complexLayout;
    }

    /**
     * @return {@link AllRulesLayout}
     */
    public AllRulesLayout getAllRulesLayout() {
        return this.allRulesLayout;
    }

//    public SubscriptionsLayout getUserSubscriptionsLayout() {
//        return this.userSubscriptionsLayout;
//    }

    /**
     * @return {@link WelcomeLayout}
     */
    public WelcomeLayout getWelcomeLayout() {
        return this.welcomeLayout;
    }

    /**
     * @return {@link SearchLayout}
     */
    public SearchLayout getSearchLayout() {
        return this.searchLayout;
    }

    /**
     * @return The current layout
     */
    public VLayout getLayout() {
        return this.layout;
    }
}