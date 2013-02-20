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

import static com.google.gwt.user.client.Cookies.getCookie;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ui.Toaster.getToasterInstance;
import static org.n52.client.util.CookieManager.destroyCurrentLoginSession;
import static org.n52.client.util.CookieManager.getCurrentLoginSession;
import static org.n52.shared.responses.SesClientResponseType.ERROR;
import static org.n52.shared.responses.SesClientResponseType.LAST_ADMIN;
import static org.n52.shared.responses.SesClientResponseType.LOGOUT;
import static org.n52.shared.responses.SesClientResponseType.MAIL;
import static org.n52.shared.responses.SesClientResponseType.NEW_PASSWORD_OK;
import static org.n52.shared.responses.SesClientResponseType.OK;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_HANDY;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_NAME;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_OK;
import static org.n52.shared.responses.SesClientResponseType.REGSITER_EMAIL;
import static org.n52.shared.responses.SesClientResponseType.RULE_NAME_NOT_EXISTS;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ROLE;

import java.util.ArrayList;
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
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.RuleCreatedEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.ShowAllUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ui.Toaster;
import org.n52.client.util.CookieManager;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponseType;
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

    public SesRequestManager() {
        this.sesUserService = GWT.create(RpcSesUserService.class);
        this.sesRulesService = GWT.create(RpcSesRuleService.class);
        this.sesTimeseriesService = GWT.create(RpcSesTimeseriesToFeedService.class);
    }

    public void registerUser(UserDTO user) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedRegistration());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(REGISTER_OK)) {

                    String userRole = Cookies.getCookie(COOKIE_USER_ROLE);

                    if (userRole == null || !userRole.equals(UserRole.ADMIN.toString())) {
                        SC.say(i18n.emailSent());
                        // link to loginpage
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(FormLayout.LayoutType.LOGIN));
                    }
                    else {
                        SC.say(i18n.createUserSuccessful());
                    }
                }
                else if (response.getType().equals(REGISTER_HANDY)) {
                    SC.say(i18n.registerHandy());
                }
                else if (response.getType().equals(REGISTER_NAME)) {
                    SC.say(i18n.registerName());
                }
                else if (response.getType().equals(REGSITER_EMAIL)) {
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
     *        the name
     * @param password
     *        the password
     */
    public void login(String name, String password) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedLogin());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == SesClientResponseType.LOGIN_OK) {
                    UserDTO user = response.getUser();

                    CookieManager.setCookiesFrom(response.getLoginSession());

                    if ( !user.isEmailVerified()) {
                        SC.say(i18n.validateEMail());
                    }
                    if (user.isPasswordChanged()) {
                        SC.say(i18n.passwordChanged());
                    }
                    if (user.getRole().equals(UserRole.ADMIN)) {
                        String names = null;
                        ArrayList<String> list = response.getComplexRules();

                        for (int i = 0; i < list.size(); i++) {
                            names = names + list.get(i) + ", ";
                        }
                        if (names != null) {
                            SC.say(i18n.deletedUser() + ": " + names);
                        }
                    }
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
                    EventBus.getMainEventBus().fireEvent(new SetRoleEvent(user.getRole()));
                }
                else if (response.getType() == SesClientResponseType.LOGIN_USER) {
                    SC.say(i18n.onlyAdminsAllowedToLogin());
                }
                else {
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }

        };
        this.sesUserService.login(name, password, callback);
    }

    /**
     * Sends a new password to ther user's email if it matches the registered one.
     * 
     * @param name
     *        the username
     * @param email
     *        the email
     */
    public void newPassword(String name, String email) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGeneratePassword());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                if ( ! (response.getType() == NEW_PASSWORD_OK)) {
                    SC.say(i18n.invalidNewPasswordInputs());
                }
                else {
                    if ( !Cookies.getCookie(COOKIE_USER_ROLE).equals(ADMIN.toString())) {
                        SC.say(i18n.passwordSended());
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.LOGIN));
                    }
                }
            }
        };
        sesUserService.resetPassword(name, email, callback);
    }

    public void logout() {
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedLogout());
            }

            @Override
            public void onSuccess(Void result) {
                destroyCurrentLoginSession();
                getMainEventBus().fireEvent(new SetRoleEvent(UserRole.LOGOUT));
            }
        };
        sesUserService.logout(getCurrentLoginSession(), callback);
    }

    public void getUser(String id) {
        AsyncCallback<UserDTO> callback = new AsyncCallback<UserDTO>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGetUser());
            }

            @Override
            public void onSuccess(UserDTO user) {
                getMainEventBus().fireEvent(new UpdateProfileEvent(user));
            }
        };
        sesUserService.getUser(getCurrentLoginSession(), callback);
    }

    public void deleteUser(String id) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(LAST_ADMIN)) {
                    SC.say(i18n.lastAdmin());
                }
                if ( !Cookies.getCookie(COOKIE_USER_ROLE).equals(ADMIN.name())) {
                    getMainEventBus().fireEvent(new LogoutEvent());
                }
                if (Cookies.getCookie(COOKIE_USER_ROLE).equals(ADMIN.name())) {
                    getMainEventBus().fireEvent(new GetAllUsersEvent());
                }
            }
        };
        sesUserService.deleteUser(getCurrentLoginSession(), id, callback);
    }

    public void updateUser(UserDTO user, String userID) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedUpdateUser());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(OK)) {
                    SC.say(i18n.updateSuccessful());
                    if (Cookies.getCookie(COOKIE_USER_ROLE).equals(ADMIN.toString())) {
                        getMainEventBus().fireEvent(new GetAllUsersEvent());
                    }
                }
                else if (response.getType().equals(REGISTER_HANDY)) {
                    SC.say(i18n.registerHandy());
                }
                else if (response.getType().equals(REGISTER_NAME)) {
                    SC.say(i18n.registerName());
                }
                else if (response.getType().equals(REGSITER_EMAIL)) {
                    SC.say(i18n.registerEMail());
                }
                else if (response.getType().equals(LAST_ADMIN)) {
                    SC.say(i18n.lastAdmin());
                }
                else if (response.getType().equals(LOGOUT)) {
                    getMainEventBus().fireEvent(new LogoutEvent());
                }
                else if (response.getType().equals(MAIL)) {
                    SC.say(i18n.mailSent());
                }
                else if (response.getType().equals(ERROR)) {
                    SC.say(i18n.passwordDoNotMatch());
                }
            }
        };
        sesUserService.updateUser(getCurrentLoginSession(), user, callback);
    }

    public void subscribe(final String userID, final String uuid, final String medium, final String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                SesClientResponseType type = result.getType();

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

                    String finalMessage = message + "\n" + i18n.subscribeSuccessful2();
                    SC.say(finalMessage);

                    getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, false));
                    getMainEventBus().fireEvent(new GetAllOtherRulesEvent(userID, false));
                    break;
                case RULE_NAME_EXISTS:
                    SC.say(i18n.copyExistsSubscribe());
                    break;
                case ERROR_SUBSCRIBE_SES:
                    Toaster.getToasterInstance().addErrorMessage(i18n.errorSubscribeSES());
                    break;
                case ERROR_SUBSCRIBE_FEEDER:
                    Toaster.getToasterInstance().addErrorMessage(i18n.errorSubscribeFeeder());
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
        this.sesRulesService.subscribe(userID, uuid, medium, format, callback);
    }

    public void createBasicRule(Rule rule, boolean edit, String oldRuleName) {

        ServerCallback<SesClientResponse> callback = new CreateSimpleRuleCallback(this, "Could not create rule.") {
            @Override
            public void onSuccess(SesClientResponse result) {

                if (result.getType() == RULE_NAME_NOT_EXISTS) {
                    Rule basicRule = result.getBasicRule();
                    EventBus.getMainEventBus().fireEvent(new RuleCreatedEvent(basicRule));
                }
                else {
                    // TODO handle more result types
                    SC.say(i18n.creatingRuleWasUnsuccessful());
                }
            }
        };

        // TODO remove dead code
        // AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
        // public void onFailure(Throwable arg0) {
        // Toaster.getInstance().addErrorMessage(i18n.failedCreateBR());
        // }
        //
        // public void onSuccess(SesClientResponse result) {
        // if (result.getType().equals(types.RULE_NAME_NOT_EXISTS)) {
        // SC.say(i18n.creationSuccessful());
        // if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
        // EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.ABOS));
        // } else {
        // EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
        // }
        // } else if (result.getType().equals(types.RULE_NAME_EXISTS)) {
        // SC.say(i18n.ruleExists());
        // } else if (result.getType().equals(types.EDIT_SIMPLE_RULE)) {
        // if ((Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE)).equals("USER")) {
        // EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.EDIT_RULES));
        // } else {
        // EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(Layouts.RULELIST));
        // }
        // }
        // }
        // };
        this.sesRulesService.createBasicRule(rule, edit, oldRuleName, callback);
    }

    public void getAllUsers() {
        AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGetAllUser());
            }

            @Override
            public void onSuccess(List<UserDTO> result) {
                getMainEventBus().fireEvent(new ShowAllUserEvent(result));
            }
        };
        sesUserService.getAllUsers(getCurrentLoginSession(), callback);
    }

    public void getStations() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGetStations());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        sesTimeseriesService.getStations(callback);
    }

    public void getPhenomena(String sensor) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGetPhenomena());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        sesTimeseriesService.getPhenomena(sensor, callback);
    }

    public void getAllOwnRules(String id, boolean edit) {
        // TODO use session cookie
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        sesRulesService.getAllOwnRules(id, edit, callback);
    }

    public void getAllOtherRules(String id, boolean edit) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                Toaster.getToasterInstance().addErrorMessage(i18n.failedGetAllRegisteredSensors());
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
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                Toaster.getToasterInstance().addErrorMessage(i18n.failedPublishRule());
            }

            public void onSuccess(SesClientResponse response) {
                // update list
                if (response.getType().equals(SesClientResponseType.PUBLISH_RULE_USER)) {
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(Cookies.getCookie(COOKIE_USER_ID),
                                                                                 true));
                }
                else {
                    EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent());
                }

            }
        };
        this.sesRulesService.publishRule(ruleName, published, role, callback);
    }

    public void getAllRules() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedGetAllRules());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllRules(callback);
    }

    public void deleteRule(String uuid, final String role) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType().equals(SesClientResponseType.DELETE_RULE_SUBSCRIBED)) {
                    SC.say(i18n.ruleSubscribed());
                }
                else {
                    // update list
                    if (role.equals("ADMIN")) {
                        EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent());
                    }
                    else {
                        EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(getCookie(COOKIE_USER_ID), true));
                    }
                }
            }
        };
        this.sesRulesService.deleteRule(uuid, callback);
    }

    public void deleteTimeseriesFeed(String timeseriesFeed) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                if (result.getType().equals(SesClientResponseType.EDIT_SIMPLE_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_SIMPLE));
                    EventBus.getMainEventBus().fireEvent(new EditSimpleRuleEvent(result.getBasicRule()));
                }
                else if (result.getType().equals(SesClientResponseType.EDIT_COMPLEX_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_COMPLEX));
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
                }
            }

            public void onFailure(Throwable caught) {
                Toaster.getToasterInstance().addErrorMessage(caught.getMessage());
            }
        };
        this.sesRulesService.getRuleForEditing(ruleName, callback);

    }

    public void getAllPublishedRules(int operator) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllPublishedRules(Cookies.getCookie(COOKIE_USER_ID), operator, callback);
    }

    public void unsubscribe(String uuid, final String userID, String medium, String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                SesClientResponseType type = response.getType();

                switch (type) {
                case OK:
                    SC.say(i18n.unsubscribeSuccessful());
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(userID, false));
                    break;
                case ERROR_UNSUBSCRIBE_SES:
                    Toaster.getToasterInstance().addErrorMessage(i18n.errorUnsubscribeSES());
                    break;
                default:
                    break;
                }
            }
        };
        this.sesRulesService.unSubscribe(uuid, userID, medium, format, callback);
    }

    /**
     * @param ruleName
     */
    public void ruleNameExists(String ruleName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(SesClientResponseType.OK)) {
                    SC.say(i18n.creationSuccessful());
                    if ( (Cookies.getCookie(COOKIE_USER_ROLE)).equals("USER")) {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.ABOS));
                    }
                    else {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.RULELIST));
                    }
                }
                else if (result.getType().equals(SesClientResponseType.RULE_NAME_EXISTS)) {
                    SC.say(i18n.ruleExists());
                }
                else if (result.getType().equals(SesClientResponseType.EDIT_COMPLEX_RULE)) {
                    if ( (Cookies.getCookie(COOKIE_USER_ROLE)).equals("USER")) {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.EDIT_RULES));
                    }
                    else {
                        EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.RULELIST));
                    }
                }
            }
        };
        this.sesRulesService.createComplexRule(rule, edit, oldName, callback);
    }

    public void getUserSubscriptions(String userID) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        this.sesRulesService.getUserSubscriptions(userID, callback);
    }

    public void deleteProfile(String id) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                SC.say(i18n.profileDelete());
            }
        };
        sesUserService.requestToDeleteProfile(getCurrentLoginSession(), callback);
    }

    public void getTermsOfUse(String language) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
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
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType().equals(SesClientResponseType.OK)) {
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(userID, true));
                }
                else if (result.getType().equals(SesClientResponseType.RULE_NAME_EXISTS)) {
                    SC.say(i18n.copyExists());
                }
            }
        };
        this.sesRulesService.copy(userID, ruleName, callback);
    }

    public void getData() {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
            }
        };
        sesUserService.getData(callback);
    }
}