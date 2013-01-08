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
package org.n52.client.ses.ctrl;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.n52.client.control.PropertiesManager;
import org.n52.client.eventBus.EventBus;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.GetStationsEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.ui.SesTab;
import org.n52.client.ses.ui.Layout.Layouts;
import org.n52.client.view.gui.elements.ctrl.DataControls;
import org.n52.shared.serializable.pojos.UserRole;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The Class DataControlsSes.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class DataControlsSes extends DataControls {

    /** The role. */
    private UserRole role = UserRole.NOT_REGISTERED_USER;

    /** The button width. */
    private final int buttonWidth = 190;

    /** The inner layout. */
    private VLayout innerLayout;

    /** The top layout. */
    private HLayout topLayout;

    /** The bottom layout. */
    private HLayout bottomLayout;

    // buttons
    // user
    /** The login button. */
    private IButton loginButton;

    /** The logout button. */
    private IButton logoutButton;

    /** The register button. */
    private IButton registerButton;

    /** The get password button. */
    private IButton getPasswordButton;

    // registered user
    /** The edit profile button. */
    private IButton editProfileButton;

    /** The abo rule button. */
    private IButton aboRuleButton;

    /** The create simple rule button. */
    private IButton createSimpleRuleButton;

    /** The create complex rule button. */
    private IButton createComplexRuleButton;

    /** The edit rules button. */
    private IButton editRulesButton;

    /** The subscriptions button. */
    private IButton subscriptionsButton;

    // admin
    /** The manage user button. */
    private IButton manageUserButton;

    /** The manage sensor button. */
    private IButton manageSensorButton;

    /** The manage rules button. */
    private IButton manageRulesButton;

    /** The search rules button */
    private IButton searchRulesButton;

    /** The help button */
    private IButton helpButton;

    /** The tab. */
    public static SesTab tab;

    private static String webAppPath = "";

    public static boolean warnUserLongNotification;

    public static int minimumPasswordLength = 4;

    public static String[] availableWNSMedia;

    public static String defaultMedium;

    public static String[] availableFormats;

    public static String defaultFormat;

    /**
     * Instantiates a new data controls ses.
     * 
     * @param sesTabController
     *            the ses tab controller
     */
    public DataControlsSes(SesTabController sesTabController) {
        generateControls();
    }

    /**
     * Generate controls.
     */
    private void generateControls() {
        setAlign(Alignment.CENTER);
        setHeight(52);
        setOverflow(Overflow.AUTO);
        // default is "normal"
        setStyleName("sensorweb_client_dataControls");

        this.innerLayout = new VLayout();
        this.innerLayout.setTabIndex(-1);
        this.topLayout = new HLayout();
        this.topLayout.setTabIndex(-1);
        this.bottomLayout = new HLayout();
        this.bottomLayout.setTabIndex(-1);

        // loginButton
        this.loginButton = new IButton(i18n.login());
        this.loginButton.setWidth(this.buttonWidth);
        this.loginButton.setShowRollOver(true);
        this.loginButton.setShowDown(true);
        this.loginButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.LOGIN));
                highlightSelectedButton(loginButton);
            }
        });

        // registerButton
        this.registerButton = new IButton(i18n.register());
        this.registerButton.setWidth(this.buttonWidth);
        this.registerButton.setShowRollOver(true);
        this.registerButton.setShowDown(true);
        this.registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.REGISTER));
                highlightSelectedButton(registerButton);
            }
        });
        // FIXME: hide registerButton to avoid user registration to SES service
        this.registerButton.hide();

        // getPasswordButton
        this.getPasswordButton = new IButton(i18n.forgotPassword());
        this.getPasswordButton.setWidth(this.buttonWidth);
        this.getPasswordButton.setShowRollOver(true);
        this.getPasswordButton.setShowDown(true);
        this.getPasswordButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.PASSWORD));
                highlightSelectedButton(getPasswordButton);
            }
        });

        // editProfileButton
        this.editProfileButton = new IButton(i18n.editProfile());
        this.editProfileButton.setWidth(this.buttonWidth);
        this.editProfileButton.setShowRollOver(true);
        this.editProfileButton.setShowDown(true);
        this.editProfileButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_PROFILE));
                highlightSelectedButton(editProfileButton);
            }
        });

        // aboRuleButton
        this.aboRuleButton = new IButton(i18n.subscribeRules());
        this.aboRuleButton.setWidth(this.buttonWidth);
        this.aboRuleButton.setShowRollOver(true);
        this.aboRuleButton.setShowDown(true);
        this.aboRuleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.ABOS));
                highlightSelectedButton(aboRuleButton);
            }
        });

        // createSimpleRuleButton
        this.createSimpleRuleButton = new IButton(i18n.createBasicRule());
        this.createSimpleRuleButton.setWidth(this.buttonWidth);
        this.createSimpleRuleButton.setShowRollOver(true);
        this.createSimpleRuleButton.setShowDown(true);
        this.createSimpleRuleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.CREATE_SIMPLE));
                EventBus.getMainEventBus().fireEvent(new GetStationsEvent());
                highlightSelectedButton(createSimpleRuleButton);
            }
        });

        // createComplexRuleButton
        this.createComplexRuleButton = new IButton(i18n.createComplexRule());
        this.createComplexRuleButton.setWidth(this.buttonWidth);
        this.createComplexRuleButton.setShowRollOver(true);
        this.createComplexRuleButton.setShowDown(true);
        this.createComplexRuleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.CREATE_COMPLEX));
                highlightSelectedButton(createComplexRuleButton);
            }
        });

        // editRulesButton
        this.editRulesButton = new IButton(i18n.editRules());
        this.editRulesButton.setWidth(this.buttonWidth);
        this.editRulesButton.setShowRollOver(true);
        this.editRulesButton.setShowDown(true);
        this.editRulesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(1));
                highlightSelectedButton(editRulesButton);
            }
        });

        // subscriptionsButton
        this.subscriptionsButton = new IButton(i18n.subscriptions());
        this.subscriptionsButton.setWidth(this.buttonWidth);
        this.subscriptionsButton.setShowRollOver(true);
        this.subscriptionsButton.setShowDown(true);
        this.subscriptionsButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.USER_SUBSCRIPTIONS));
                highlightSelectedButton(subscriptionsButton);
            }
        });

        // manageUserButton
        this.manageUserButton = new IButton(i18n.userManagement());
        this.manageUserButton.setWidth(this.buttonWidth);
        this.manageUserButton.setShowRollOver(true);
        this.manageUserButton.setShowDown(true);
        this.manageUserButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.USERLIST));
                highlightSelectedButton(manageUserButton);
            }
        });

        // manageSensorButton
        this.manageSensorButton = new IButton(i18n.sensorManagement());
        this.manageSensorButton.setWidth(this.buttonWidth);
        this.manageSensorButton.setShowRollOver(true);
        this.manageSensorButton.setShowDown(true);
        this.manageSensorButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.SENSORLIST));
                highlightSelectedButton(manageSensorButton);
            }
        });

        // manageRulesButton
        this.manageRulesButton = new IButton(i18n.showAllRules());
        this.manageRulesButton.setWidth(this.buttonWidth);
        this.manageRulesButton.setShowRollOver(true);
        this.manageRulesButton.setShowDown(true);
        this.manageRulesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
                highlightSelectedButton(manageRulesButton);
            }
        });

        // logoutButton
        this.logoutButton = new IButton(i18n.logout());
        this.logoutButton.setWidth(this.buttonWidth);
        this.logoutButton.setShowRollOver(true);
        this.logoutButton.setShowDown(true);
        this.logoutButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new LogoutEvent());
            }
        });

        // manageRulesButton
        this.searchRulesButton = new IButton(i18n.search());
        this.searchRulesButton.setWidth(this.buttonWidth);
        this.searchRulesButton.setShowRollOver(true);
        this.searchRulesButton.setShowDown(true);
        this.searchRulesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.SEARCH));
                highlightSelectedButton(searchRulesButton);
            }
        });

        // helpButton
        this.helpButton = new IButton(i18n.help());
        this.helpButton.setWidth(this.buttonWidth);
        this.helpButton.setShowRollOver(true);
        this.helpButton.setShowDown(true);
        this.helpButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String helpUrl = GWT.getHostPageBaseURL();
                highlightSelectedButton(helpButton);
                if (PropertiesManager.language.equals("en")) {
                    Window.open(helpUrl + "help_en.html", "help", "");
                } else {
                    Window.open(helpUrl + "help_de.html", "help", "");
                }
            }
        });
        addMemberToLayouts();
    }

    /**
     * Adds the member to layouts.
     */
    public void addMemberToLayouts() {

        // remove all members from layouts
        boolean firstTime = true;

        this.topLayout.removeMembers(this.topLayout.getMembers());
        this.bottomLayout.removeMembers(this.bottomLayout.getMembers());
        this.innerLayout.removeMembers(this.innerLayout.getMembers());

        if (!firstTime) {
            removeMember(this.innerLayout);
            firstTime = false;
        }

        // Distinctions between different roles
        switch (this.role) {
        case ADMIN:
            this.topLayout.addMember(this.manageUserButton);
            this.topLayout.addMember(this.createSimpleRuleButton);
            this.topLayout.addMember(this.aboRuleButton);
            this.topLayout.addMember(this.manageRulesButton);
            this.topLayout.addMember(this.searchRulesButton);

            this.bottomLayout.addMember(this.manageSensorButton);
            this.bottomLayout.addMember(this.createComplexRuleButton);
            this.bottomLayout.addMember(this.subscriptionsButton);
            this.bottomLayout.addMember(this.helpButton);
            this.bottomLayout.addMember(this.logoutButton);
            break;

        case USER:
            this.topLayout.addMember(this.aboRuleButton);
            this.topLayout.addMember(this.createSimpleRuleButton);
            this.topLayout.addMember(this.subscriptionsButton);
            this.topLayout.addMember(this.helpButton);

            this.bottomLayout.addMember(this.editRulesButton);
            this.bottomLayout.addMember(this.createComplexRuleButton);
            this.bottomLayout.addMember(this.editProfileButton);
            this.bottomLayout.addMember(this.searchRulesButton);
            this.bottomLayout.addMember(this.logoutButton);
            break;

        default:
            this.topLayout.addMember(this.loginButton);
            this.topLayout.addMember(this.helpButton);
            this.bottomLayout.addMember(this.registerButton);
            this.bottomLayout.addMember(this.getPasswordButton);
            break;
        }

        this.innerLayout.addMember(this.topLayout);
        this.innerLayout.addMember(this.bottomLayout);
        addMember(this.innerLayout);
    }

    /**
     * Sets the role.
     * 
     * @param role
     *            the new role
     */
    public void setRole(UserRole role) {
        if (role == UserRole.LOGOUT) {
            this.role = UserRole.NOT_REGISTERED_USER;
        } else {
            this.role = role;
        }
        addMemberToLayouts();
    }

    /**
     * 
     * @param selectedButton
     */
    public void highlightSelectedButton(IButton selectedButton) {
        this.loginButton.setSelected(false);
        this.logoutButton.setSelected(false);
        this.registerButton.setSelected(false);
        this.getPasswordButton.setSelected(false);
        this.editProfileButton.setSelected(false);
        this.aboRuleButton.setSelected(false);
        this.createSimpleRuleButton.setSelected(false);
        this.createComplexRuleButton.setSelected(false);
        this.editRulesButton.setSelected(false);
        this.subscriptionsButton.setSelected(false);
        this.manageUserButton.setSelected(false);
        this.manageSensorButton.setSelected(false);
        this.manageRulesButton.setSelected(false);
        this.searchRulesButton.setSelected(false);
        this.helpButton.setSelected(false);

        selectedButton.setSelected(true);
        this.helpButton.setSelected(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.elements.controlsImpl.DataControls#getControls()
     */
    @Override
    public Canvas getControls() {
        return this;
    }

    /**
     * Update.
     */
    public void update() {
        generateControls();
    }

    /**
     * Gets the tab.
     * 
     * @return the tab
     */
    public SesTab getTab() {
        return DataControlsSes.tab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.elements.controlsImpl.DataControls#getControlHeight
     * ()
     */
    @Override
    public int getControlHeight() {
        return this.getHeight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.elements.controlsImpl.DataControls#getControlWidth
     * ()
     */
    @Override
    public int getControlWidth() {
        return this.getWidth();
    }

    public IButton getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(IButton loginButton) {
        this.loginButton = loginButton;
    }

    public IButton getLogoutButton() {
        return logoutButton;
    }

    public void setLogoutButton(IButton logoutButton) {
        this.logoutButton = logoutButton;
    }

    public IButton getRegisterButton() {
        return registerButton;
    }

    public void setRegisterButton(IButton registerButton) {
        this.registerButton = registerButton;
    }

    public IButton getGetPasswordButton() {
        return getPasswordButton;
    }

    public void setGetPasswordButton(IButton getPasswordButton) {
        this.getPasswordButton = getPasswordButton;
    }

    public IButton getEditProfileButton() {
        return editProfileButton;
    }

    public void setEditProfileButton(IButton editProfileButton) {
        this.editProfileButton = editProfileButton;
    }

    public IButton getAboRuleButton() {
        return aboRuleButton;
    }

    public void setAboRuleButton(IButton aboRuleButton) {
        this.aboRuleButton = aboRuleButton;
    }

    public IButton getCreateSimpleRuleButton() {
        return createSimpleRuleButton;
    }

    public void setCreateSimpleRuleButton(IButton createSimpleRuleButton) {
        this.createSimpleRuleButton = createSimpleRuleButton;
    }

    public IButton getCreateComplexRuleButton() {
        return createComplexRuleButton;
    }

    public void setCreateComplexRuleButton(IButton createComplexRuleButton) {
        this.createComplexRuleButton = createComplexRuleButton;
    }

    public IButton getEditRulesButton() {
        return editRulesButton;
    }

    public void setEditRulesButton(IButton editRulesButton) {
        this.editRulesButton = editRulesButton;
    }

    public IButton getSubscriptionsButton() {
        return subscriptionsButton;
    }

    public void setSubscriptionsButton(IButton subscriptionsButton) {
        this.subscriptionsButton = subscriptionsButton;
    }

    public IButton getManageUserButton() {
        return manageUserButton;
    }

    public void setManageUserButton(IButton manageUserButton) {
        this.manageUserButton = manageUserButton;
    }

    public IButton getManageSensorButton() {
        return manageSensorButton;
    }

    public void setManageSensorButton(IButton manageSensorButton) {
        this.manageSensorButton = manageSensorButton;
    }

    public IButton getManageRulesButton() {
        return manageRulesButton;
    }

    public void setManageRulesButton(IButton manageRulesButton) {
        this.manageRulesButton = manageRulesButton;
    }

    public IButton getSearchRulesButton() {
        return searchRulesButton;
    }

    public void setSearchRulesButton(IButton searchRulesButton) {
        this.searchRulesButton = searchRulesButton;
    }

    public IButton getHelpButton() {
        return helpButton;
    }

    public void setHelpButton(IButton helpButton) {
        this.helpButton = helpButton;
    }

    public static String getWebAppPath() {
        return webAppPath;
    }

    public static void setWebAppPath(String webAppPath) {
        DataControlsSes.webAppPath = webAppPath;
    }

    public static void setWarnUserLongNotification(boolean warnUserLongNotification) {
        DataControlsSes.warnUserLongNotification = warnUserLongNotification;
    }

    public static void setMinimumPasswordLength(int minimumPasswordLength) {
        DataControlsSes.minimumPasswordLength = minimumPasswordLength;
    }

    public static String[] getAvailableWNSMedia() {
        return availableWNSMedia;
    }

    public static void setAvailableWNSMedia(String[] availableWNSMedia) {
        DataControlsSes.availableWNSMedia = availableWNSMedia;
    }

    public static String getDefaultMedium() {
        return defaultMedium;
    }

    public static void setDefaultMedium(String defaultMedium) {
        DataControlsSes.defaultMedium = defaultMedium;
    }

    public static String getDefaultFormat() {
        return defaultFormat;
    }

    public static void setDefaultFormat(String defaultFormat) {
        DataControlsSes.defaultFormat = defaultFormat;
    }

    public static String[] getAvailableFormats() {
        return availableFormats;
    }

    public static void setAvailableFormats(String[] availableFormats) {
        DataControlsSes.availableFormats = availableFormats;
    }

    public static String createMD5(String password) {
        StringBuffer buffer = new StringBuffer();

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes());

            byte[] result = md5.digest();

            for (int i = 0; i < result.length; i++) {
                buffer.append(Integer.toHexString(0xFF & result[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            if (!GWT.isProdMode()) {
                GWT.log("Error hashing password", e);
            }
        }
        return buffer.toString();
    }
}