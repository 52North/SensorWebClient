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
package org.n52.client.ses.ctrl;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;
import static org.n52.shared.serializable.pojos.UserRole.NOT_REGISTERED_USER;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.DataControls;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.GetStationsEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ses.ui.SesTab;
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

    private UserRole role = NOT_REGISTERED_USER;

    private final int buttonWidth = 140;

    private VLayout innerLayout;

    private HLayout topLayout;

    @Deprecated
    private HLayout bottomLayout;

    @Deprecated
    private IButton loginButton;

    @Deprecated
    private IButton logoutButton;

    @Deprecated
    private IButton registerButton;

    private IButton getPasswordButton;

    private IButton editProfileButton;

    @Deprecated
    private IButton aboRuleButton;

    @Deprecated
    private IButton createSimpleRuleButton;

    @Deprecated
    private IButton createComplexRuleButton;

    @Deprecated
    private IButton editRulesButton;

    private IButton manageUserButton;

    @Deprecated
    private IButton manageRulesButton;

    @Deprecated
    private IButton searchRulesButton;

    private IButton helpButton;

    public static SesTab tab;

    private static String webAppPath = "";

    public static boolean warnUserLongNotification;

    public static int minimumPasswordLength = 4;

    public static String[] availableWNSMedia;

    public static String defaultMedium;

    public static String[] availableFormats;

    public static String defaultFormat;

    public DataControlsSes(SesTabController sesTabController) {
        generateControls();
    }

    private void generateControls() {
        setAlign(Alignment.CENTER);
        setHeight(52);
        setOverflow(Overflow.AUTO);
        setStyleName("n52_sensorweb_client_dataControls");

        this.innerLayout = new VLayout();
        this.innerLayout.setTabIndex(-1);
        this.innerLayout.setTop(0);
        this.topLayout = new HLayout();
        this.topLayout.setTabIndex(-1);
//        this.bottomLayout = new HLayout();
//        this.bottomLayout.setTabIndex(-1);

        // loginButton
        this.loginButton = new IButton(i18n.userLogin());
        this.loginButton.setWidth(this.buttonWidth);
        this.loginButton.setShowRollOver(true);
        this.loginButton.setShowDown(true);
        this.loginButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.LOGIN));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.REGISTER));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.PASSWORD));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.EDIT_PROFILE));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.ABOS));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_SIMPLE));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_COMPLEX));
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
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.EDIT_RULES));
                EventBus.getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(currentSession(), 1));
                highlightSelectedButton(editRulesButton);
            }
        });

        // subscriptionsButton
//        this.subscriptionsButton = new IButton(i18n.subscriptions());
//        this.subscriptionsButton.setWidth(this.buttonWidth);
//        this.subscriptionsButton.setShowRollOver(true);
//        this.subscriptionsButton.setShowDown(true);
//        this.subscriptionsButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.USER_SUBSCRIPTIONS));
//                highlightSelectedButton(subscriptionsButton);
//            }
//        });

        // manageUserButton
        this.manageUserButton = new IButton(i18n.userManagement());
        this.manageUserButton.setWidth(this.buttonWidth);
        this.manageUserButton.setShowRollOver(true);
        this.manageUserButton.setShowDown(true);
        this.manageUserButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.USERLIST));
                highlightSelectedButton(manageUserButton);
            }
        });

        // manageRulesButton
        this.manageRulesButton = new IButton(i18n.showAllRules());
        this.manageRulesButton.setWidth(this.buttonWidth);
        this.manageRulesButton.setShowRollOver(true);
        this.manageRulesButton.setShowDown(true);
        this.manageRulesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.RULELIST));
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
                EventBus.getMainEventBus().fireEvent(new LogoutEvent(currentSession()));
            }
        });

        // manageRulesButton
//        this.searchRulesButton = new IButton(i18n.search());
//        this.searchRulesButton.setWidth(this.buttonWidth);
//        this.searchRulesButton.setShowRollOver(true);
//        this.searchRulesButton.setShowDown(true);
//        this.searchRulesButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.SEARCH));
//                highlightSelectedButton(searchRulesButton);
//            }
//        });

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
                    Window.open(helpUrl + "help_ses_en.html", "help", "");
                } else {
                    Window.open(helpUrl + "help_ses_de.html", "help", "");
                }
            }
        });
        addMemberToLayouts();
    }

    public void addMemberToLayouts() {

        // remove all members from layouts
        boolean firstTime = true;

        this.topLayout.removeMembers(this.topLayout.getMembers());
//        this.bottomLayout.removeMembers(this.bottomLayout.getMembers());
        this.innerLayout.removeMembers(this.innerLayout.getMembers());

        if (!firstTime) {
            removeMember(this.innerLayout);
            firstTime = false;
        }

        // Distinctions between different roles
        switch (this.role) {
        case ADMIN:
            this.topLayout.addMember(this.manageUserButton);
//            this.topLayout.addMember(this.createSimpleRuleButton);
//            this.topLayout.addMember(this.aboRuleButton);
//            this.topLayout.addMember(this.manageRulesButton);
//            this.topLayout.addMember(this.searchRulesButton);

//            this.bottomLayout.addMember(this.createComplexRuleButton);
            this.topLayout.addMember(this.helpButton);
//            this.bottomLayout.addMember(this.logoutButton);
            break;

//        case USER:
//            this.topLayout.addMember(this.aboRuleButton);
//            this.topLayout.addMember(this.createSimpleRuleButton);
//            this.topLayout.addMember(this.helpButton);
//
//            this.bottomLayout.addMember(this.editRulesButton);
//            this.bottomLayout.addMember(this.createComplexRuleButton);
//            this.bottomLayout.addMember(this.editProfileButton);
//            this.bottomLayout.addMember(this.searchRulesButton);
//            this.bottomLayout.addMember(this.logoutButton);
//            break;

//        default:
//            this.topLayout.addMember(this.loginButton);
//            this.topLayout.addMember(this.helpButton);
//            this.bottomLayout.addMember(this.registerButton);
//            this.bottomLayout.addMember(this.getPasswordButton);
//            break;
        }

        this.innerLayout.addMember(this.topLayout);
//        this.innerLayout.addMember(this.bottomLayout);
        addMember(this.innerLayout);
    }
    
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        if (role == LOGOUT) {
            this.role = NOT_REGISTERED_USER;
        } else {
            this.role = role;
        }
        addMemberToLayouts();
    }

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
        this.manageUserButton.setSelected(false);
        this.manageRulesButton.setSelected(false);
//        this.searchRulesButton.setSelected(false);
        this.helpButton.setSelected(false);

        selectedButton.setSelected(true);
        this.helpButton.setSelected(false);
    }

    @Override
    public Canvas getControls() {
        return this;
    }

    public void update() {
        generateControls();
    }

    public SesTab getTab() {
        return DataControlsSes.tab;
    }

    @Override
    public int getControlHeight() {
        return this.getHeight();
    }

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

    public IButton getManageUserButton() {
        return manageUserButton;
    }

    public void setManageUserButton(IButton manageUserButton) {
        this.manageUserButton = manageUserButton;
    }

    public IButton getManageRulesButton() {
        return manageRulesButton;
    }

    public void setManageRulesButton(IButton manageRulesButton) {
        this.manageRulesButton = manageRulesButton;
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