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
import static org.n52.shared.responses.SesClientResponse.types.RULE_NAME_NOT_EXISTS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.RequestManager;
import org.n52.client.ctrl.ServerCallback;
import org.n52.client.ses.ctrl.callbacks.CreateSimpleRuleCallback;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.EditSimpleRuleEvent;
import org.n52.client.ses.event.GetAllOtherRulesEvent;
import org.n52.client.ses.event.GetAllOwnRulesEvent;
import org.n52.client.ses.event.GetAllRulesEvent;
import org.n52.client.ses.event.GetAllUsersEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.RuleCreatedEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.ShowAllUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.ui.Layout;
import org.n52.client.ses.ui.Layout.Layouts;
import org.n52.client.ui.Toaster;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponse.types;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.service.rpc.RpcSesRuleService;
import org.n52.shared.service.rpc.RpcSesRuleServiceAsync;
import org.n52.shared.service.rpc.RpcSesTimeseriesToFeedService;
import org.n52.shared.service.rpc.RpcSesTimeseriesToFeedServiceAsync;
import org.n52.shared.service.rpc.RpcSesUserService;
import org.n52.shared.service.rpc.RpcSesUserServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

public class SesRequestManager extends RequestManager {

    private RpcSesUserServiceAsync sesUserService;

    private RpcSesRuleServiceAsync sesRulesService;

    private RpcSesTimeseriesToFeedServiceAsync sesTimeseriesService;

    public static final String COOKIE_USER_ID = "SES_Client_UserID";

    public static final String COOKIE_USER_ROLE = "SES_Client_UserRole";
    
    public static final String COOKIE_USER_NAME = "SES_Client_UserName";

    public SesRequestManager() {
        this.sesUserService = GWT.create(RpcSesUserService.class);
        this.sesRulesService = GWT.create(RpcSesRuleService.class);
        this.sesTimeseriesService = GWT.create(RpcSesTimeseriesToFeedService.class);
    }

    public void registerUser(UserDTO user) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedRegistration());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(types.REGISTER_OK)) {
                    
                    String userRole = Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE);
                    
                    if (userRole == null || !userRole.equals(UserRole.ADMIN.toString())) {
                        SC.say(i18n.emailSended());
                        // link to loginpage
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layout.Layouts.LOGIN));
                    } else {
                        SC.say(i18n.createUserSuccessful());
                    }
                } else if (response.getType().equals(types.REGISTER_HANDY)) {
                    SC.say(i18n.registerHandy());
                } else if (response.getType().equals(types.REGISTER_NAME)) {
                    SC.say(i18n.registerName());
                } else if (response.getType().equals(types.REGSITER_EMAIL)) {
                    SC.say(i18n.registerEMail());
                }
            }
        };
        this.sesUserService.registerUser(user, callback);
    }

    /**
     * Login and set cookie with user parameterId.
     * 
     * @param name
     *            the name
     * @param password
     *            the password
     */
    public void login(String name, String password) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedLogin());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == SesClientResponse.types.LOGIN_OK){
                    UserDTO user = response.getUser();
                    // set Cookie
                    long duration = 1000 * 60 * 60 * 24; // 24 hours
                    Date expires = new Date(System.currentTimeMillis() + duration);
                    Cookies.setCookie(COOKIE_USER_ID, String.valueOf(user.getId()), expires, null, "/", false);
                    Cookies.setCookie(COOKIE_USER_ROLE, user.getRole().toString(), expires, null, "/", false);
                    Cookies.setCookie(COOKIE_USER_NAME, user.getName(), expires, null, "/", false);
                    
                    
                    if (!user.isEmailVerified()) {
                        SC.say(i18n.validateEMail());
                    }
                    if (user.isPasswordChanged()) {
                        SC.say(i18n.passwordChanged());
                    }
                    if (user.getRole().equals(UserRole.ADMIN)) {
                        String names = null;
                        ArrayList<String> list = response.getComplexList();
                       
                        for (int i = 0; i < list.size(); i++) {
                            names = names + list.get(i) + ", ";
                        }
                        if (names != null) {
                            SC.say(i18n.deletedUser() + ": " + names);
                        }
                    }
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
                    EventBus.getMainEventBus().fireEvent(new SetRoleEvent(user.getRole()));
                } else {
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }
        };
        this.sesUserService.login(name, password, callback);
    }

    /**
     * Sens a new password to user.
     * 
     * @param name
     *            the name
     * @param email
     *            the email
     */
    public void newPassword(String name, String email) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGeneratePassword());
            }

            public void onSuccess(SesClientResponse response) {
                if (!(response.getType() == SesClientResponse.types.NEW_PASSWORD_OK)){
                    SC.say(i18n.invalidNewPasswordInputs());
                } else {
                    if (!Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE).equals(UserRole.ADMIN.toString())) {
                        SC.say(i18n.passwordSended());
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.LOGIN));
                    }
                }
            }
        };
        this.sesUserService.newPassword(name, email, callback);
    }

    /**
     * Logout and delete cookie.
     */
    public void logout() {
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedLogout());
            }

            public void onSuccess(Void result) {
                // delete cookie
                Cookies.removeCookie(COOKIE_USER_ID);
                Cookies.removeCookie(COOKIE_USER_ROLE);
                Cookies.removeCookie(COOKIE_USER_NAME);
                EventBus.getMainEventBus().fireEvent(new SetRoleEvent(UserRole.LOGOUT));
            }
        };
        this.sesUserService.logout(callback);
    }

    public void getUser(String id) {
        AsyncCallback<UserDTO> callback = new AsyncCallback<UserDTO>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetUser());
            }

            public void onSuccess(UserDTO user) {
                EventBus.getMainEventBus().fireEvent(new UpdateProfileEvent(user));
            }
        };
        this.sesUserService.getUser(id, callback);
    }

    public void deleteUser(String id) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(types.LAST_ADMIN)) {
                    SC.say(i18n.lastAdmin());
                }
                if (!Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE).equals(UserRole.ADMIN.toString())) {
                    EventBus.getMainEventBus().fireEvent(new LogoutEvent());
                }
                if (Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE).equals(UserRole.ADMIN.toString())) {
                    EventBus.getMainEventBus().fireEvent(new GetAllUsersEvent());
                }
            }
        };
        this.sesUserService.deleteUser(id, callback);
    }

    public void updateUser(UserDTO user, String userID) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedUpdateUser());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(types.OK)) {
                    SC.say(i18n.updateSuccessful());
                    if (Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE).equals(UserRole.ADMIN.toString())) {
                        EventBus.getMainEventBus().fireEvent(new GetAllUsersEvent());
                    }
                } else if (response.getType().equals(types.REGISTER_HANDY)) {
                    SC.say(i18n.registerHandy());
                } else if (response.getType().equals(types.REGISTER_NAME)) {
                    SC.say(i18n.registerName());
                } else if (response.getType().equals(types.REGSITER_EMAIL)) {
                    SC.say(i18n.registerEMail());
                } else if (response.getType().equals(types.LAST_ADMIN)) {
                    SC.say(i18n.lastAdmin());
                } else if (response.getType().equals(types.LOGOUT)){
                    EventBus.getMainEventBus().fireEvent(new LogoutEvent());
                } else if (response.getType().equals(types.MAIL)){
                    SC.say(i18n.mailSended());
                } else if (response.getType().equals(types.ERROR)) {
                    SC.say(i18n.passwordDoNotMatch());
                }
            }
        };
        this.sesUserService.updateUser(user, userID, callback);
    }

    public void subscribe(final String userID, final String ruleName, final String medium, final String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                SesClientResponse.types type = result.getType();
                
                switch (type) {
                case OK:
                    String message = i18n.subscribeSuccessful1();
                    String[] formats = format.split("_");
                    String[] media = medium.split("_");
                    String finalFormat = "";
                    String finalMedium = "";
                    
                    for (int i = 0; i < formats.length; i++) {
                        finalFormat = finalFormat + formats[i] + " ";
                    }
                    
                    for (int j = 0; j < media.length; j++) {
                        finalMedium = finalMedium + media[j] + " ";
                    }
                    finalFormat = finalFormat.trim();
                    finalMedium = finalMedium.trim();
                    
                    message = message.replace("_R_", ruleName).replace("_M_", finalMedium).replace("_F_", finalFormat);
                    String finalMessage = message + "\n" + i18n.subscribeSuccessful2();
                    SC.say(finalMessage);
                    
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(userID, false));
                    break;
                case RULE_NAME_EXISTS:
                    SC.say(i18n.copyExistsSubscribe());
                    break;
                case ERROR_SUBSCRIBE_SES:
                    Toaster.getInstance().addErrorMessage(i18n.errorSubscribeSES());
                    break;
                case ERROR_SUBSCRIBE_FEEDER:
                    Toaster.getInstance().addErrorMessage(i18n.errorSubscribeFeeder());
                    break;
                case SUBSCRIPTION_EXISTS:
                    SC.say(i18n.subscriptionExists());
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(userID, false));
                    break;
                default:
                    break;
                }
            }
        };
        this.sesRulesService.subscribe(userID, ruleName, medium, format, callback);
    }

    public void createBasicRule(Rule rule, boolean edit, String oldRuleName) {
        
        ServerCallback<SesClientResponse> callback = new CreateSimpleRuleCallback(this, "Could not create rule.") {
            @Override
            public void onSuccess(SesClientResponse result) {
                
                if (result.getType() == RULE_NAME_NOT_EXISTS) {
                    Rule basicRule = result.getBasicRule();
                    EventBus.getMainEventBus().fireEvent(new RuleCreatedEvent(basicRule));
                } else {
                    // TODO handle more result types
                    SC.say(i18n.creatingRuleWasUnsuccessful());
                }
            }
        };
        
        // TODO remove dead code
//        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
//            public void onFailure(Throwable arg0) {
//                Toaster.getInstance().addErrorMessage(i18n.failedCreateBR());
//            }
//
//            public void onSuccess(SesClientResponse result) {
//                if (result.getType().equals(types.RULE_NAME_NOT_EXISTS)) {
//                    SC.say(i18n.creationSuccessful());
//                    if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
//                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.ABOS));
//                    } else {
//                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
//                    }
//                } else if (result.getType().equals(types.RULE_NAME_EXISTS)) {
//                    SC.say(i18n.ruleExists());
//                } else if (result.getType().equals(types.EDIT_SIMPLE_RULE)) {
//                    if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
//                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
//                    } else {
//                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
//                    }
//                }
//            }
//        };
        this.sesRulesService.createBasicRule(rule, edit, oldRuleName, callback);
    }

    public void getAllUsers() {
        AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetAllUser());
            }

            public void onSuccess(List<UserDTO> result) {
                EventBus.getMainEventBus().fireEvent(new ShowAllUserEvent(result));
            }
        };
        this.sesUserService.getAllUsers(callback);
    }

    public void getStations() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetStations());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesTimeseriesService.getStations(callback);
    }

    public void getPhenomena(String sensor) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetPhenomena());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesTimeseriesService.getPhenomena(sensor, callback);
    }

    public void getAllOwnRules(String id, boolean edit) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllOwnRules(id, edit, callback);
    }

    public void getAllOtherRules(String id, boolean edit) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllOtherRules(id, edit, callback);
    }

    public void getRegisteredTimeseriesFeeds() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetAllRegisteredSensors());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesTimeseriesService.getTimeseriesFeeds(callback);
    }

    public void upateTimeseriesFeed(String timeseriesFeedId, boolean active) {
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(Void response) {
                SC.say(i18n.updateSuccessful());
            }
        };
        this.sesTimeseriesService.updateTimeseriesFeed(timeseriesFeedId, active, callback);
    }

    public void publishRule(String ruleName, boolean published, String role) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedPublishRule());
            }

            public void onSuccess(SesClientResponse response) {
                // update list
                if (response.getType().equals(types.PUBLISH_RULE_USER)) {
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID), true));
                } else {
                    EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent());
                }
                
            }
        };
        this.sesRulesService.publishRule(ruleName, published, role, callback);
    }

    public void getAllRules() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(i18n.failedGetAllRules());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllRules(callback);
    }

    public void deleteRule(String ruleName, final String role) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(types.DELETE_RULE_SUBSCRIBED)) {
                    SC.say(i18n.ruleSubscribed());
                } else {
                    // update list
                    if (role.equals("ADMIN")) {
                        EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent());
                    } else {
                        EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID), true));
                    }
                }
            }
        };
        this.sesRulesService.deleteRule(ruleName, callback);  
    }

    public void deleteTimeseriesFeed(String timeseriesFeed) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                // update list
                EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent());
            }
        };
        this.sesTimeseriesService.deleteTimeseriesFeed(timeseriesFeed, callback); 
    }

    public void getEditRule(String ruleName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            
            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(types.EDIT_SIMPLE_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.CREATE_SIMPLE));
                    EventBus.getMainEventBus().fireEvent(new EditSimpleRuleEvent(result.getBasicRule()));
                } else if (result.getType().equals(types.EDIT_COMPLEX_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.CREATE_COMPLEX));
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
                }
            }
            
            public void onFailure(Throwable caught) {
                Toaster.getInstance().addErrorMessage(caught.getMessage());
            }
        };
        this.sesRulesService.getRuleForEditing(ruleName, callback);
        
    }
    
    public void getAllPublishedRules(int operator) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllPublishedRules(Cookies.getCookie(SesRequestManager.COOKIE_USER_ID), operator, callback);
    }

    public void unsubscribe(String ruleName, final String userID, String medium, String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                SesClientResponse.types type = response.getType();
                
                switch (type) {
                case OK:
                    SC.say(i18n.unsubscribeSuccessful());
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(userID, false));
                    EventBus.getMainEventBus().fireEvent(new GetUserSubscriptionsEvent(userID));
                    break;
                case ERROR_UNSUBSCRIBE_SES:
                    Toaster.getInstance().addErrorMessage(i18n.errorUnsubscribeSES());
                    break;
                default:
                    break;
                }
            }
        };
        this.sesRulesService.unSubscribe(ruleName, userID, medium, format, callback); 
    }

    /**
     * @param ruleName
     */
    public void ruleNameExists(String ruleName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.ruleNameExists(ruleName, callback); 
    }

    public void createComplexRule(ComplexRuleData rule, boolean edit, String oldName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(types.OK)) {
                    SC.say(i18n.creationSuccessful());
                    if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.ABOS));
                    } else {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
                    }
                } else if (result.getType().equals(types.RULE_NAME_EXISTS)) {
                    SC.say(i18n.ruleExists());
                } else if (result.getType().equals(types.EDIT_COMPLEX_RULE)) {
                    if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
                    } else {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
                    }
                }
            }
        };
        this.sesRulesService.createComplexRule(rule, edit, oldName, callback);
    }

    public void getUserSubscriptions(String userID) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        this.sesRulesService.getUserSubscriptions(userID, callback);
    }

    public void deleteProfile(String id) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                SC.say(i18n.profileDelete());
            }
        };
        this.sesUserService.deleteProfile(id, callback);
    }

    public void getTermsOfUse(String language) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        this.sesUserService.getTermsOfUse(language, callback); 
    }

    public void search(String text, int criterion, String userID) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        this.sesRulesService.search(text, criterion, userID, callback);  
    }
    
    public void copy(final String userID, String ruleName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(types.OK)) {
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, true));
                } else if (result.getType().equals(types.RULE_NAME_EXISTS)) {
                    SC.say(i18n.copyExists());
                }
            }
        };
        this.sesRulesService.copy(userID, ruleName, callback);  
    }

    public void getData() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        this.sesUserService.getData(callback);
    }
}