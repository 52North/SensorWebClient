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

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.ABOS;
import static org.n52.client.ses.ui.FormLayout.LayoutType.EDIT_RULES;
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.client.ses.ui.FormLayout.LayoutType.RULELIST;
import static org.n52.client.ui.Toaster.getToasterInstance;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.isAdminLogin;
import static org.n52.client.util.ClientSessionManager.isUserLogin;
import static org.n52.client.util.ClientSessionManager.setSessionInfo;
import static org.n52.client.util.ClientSessionManager.setUserInfo;
import static org.n52.shared.responses.SesClientResponseType.DELETE_RULE_SUBSCRIBED;
import static org.n52.shared.responses.SesClientResponseType.EDIT_COMPLEX_RULE;
import static org.n52.shared.responses.SesClientResponseType.ERROR;
import static org.n52.shared.responses.SesClientResponseType.LAST_ADMIN;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_ADMIN;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_OK;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_USER;
import static org.n52.shared.responses.SesClientResponseType.LOGOUT;
import static org.n52.shared.responses.SesClientResponseType.MAIL;
import static org.n52.shared.responses.SesClientResponseType.NEW_PASSWORD_OK;
import static org.n52.shared.responses.SesClientResponseType.OK;
import static org.n52.shared.responses.SesClientResponseType.PUBLISH_RULE_USER;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_HANDY;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_NAME;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_OK;
import static org.n52.shared.responses.SesClientResponseType.REGSITER_EMAIL;
import static org.n52.shared.responses.SesClientResponseType.REQUIRES_LOGIN;
import static org.n52.shared.responses.SesClientResponseType.RULE_NAME_EXISTS;
import static org.n52.shared.responses.SesClientResponseType.RULE_NAME_NOT_EXISTS;
import static org.n52.shared.responses.SesClientResponseType.USER_INFO;

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
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ui.Toaster;
import org.n52.client.util.ClientSessionManager;
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
import org.n52.shared.session.SessionInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

// TODO try to transfer LoginRequiredException through the wire

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
                
                setSessionInfo(response.getSessionInfo());
                
                if (response.getType().equals(REGISTER_OK)) {

                    if ( !isAdminLogin()) {
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                        SC.say(i18n.emailSent());
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

    public void login(String name, String password, final SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedLogin());
            }

            public void onSuccess(SesClientResponse response) {
                
                if (response.getType() == LOGIN_OK) {
                    UserDTO user = response.getUser();

                    if ( !user.isEmailVerified()) {
                        SC.say(i18n.validateEMail());
                    }
                    if (user.isPasswordChanged()) {
                        SC.say(i18n.passwordChanged());
                    }

                    SesRequestManager.this.performLogin(response);
                    
                }
                else if (response.getType() == LOGIN_USER) {
                    SC.say(i18n.onlyAdminsAllowedToLogin());
                }
                else {
                    getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }

        };
        this.sesUserService.login(name, password, sessionInfo, callback);
    }

    /**
     * Validates if given session is known or has become invalid on server side. This can be the case after a
     * server restart, or when cookie has been expired on server side.
     * 
     * @param sessionInfo
     *        the session info to check.
     */
    public void validate(SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                getMainEventBus().fireEvent(new SetRoleEvent(UserRole.LOGOUT));
                getToasterInstance().addErrorMessage(i18n.loginIsOrHasBecomeInvalid());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                if (response.getType() == SesClientResponseType.LOGOUT) {
                    getMainEventBus().fireEvent(new SetRoleEvent(UserRole.LOGOUT));
                    createSessionInfo(); // get fresh session info
                } else if (response.getType() == LOGIN_OK || response.getType() == LOGIN_ADMIN) {
                    SesRequestManager.this.performLogin(response);
                }
            }
        };
        sesUserService.validateLoginSession(sessionInfo, callback);
    }

    private void performLogin(SesClientResponse response) {
        setUserInfo(response.getSessionInfo());
        setSessionInfo(response.getSessionInfo());
        getMainEventBus().fireEvent(new InformUserEvent(response));
        getMainEventBus().fireEvent(new SetRoleEvent(response.getUser().getRole()));
    }

    /**
     * Asks the server to create a new session and sets it at the {@link ClientSessionManager}.
     */
    public void createSessionInfo() {
        AsyncCallback<SessionInfo> callback = new AsyncCallback<SessionInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                getToasterInstance().addErrorMessage(i18n.failedSessionCreation());
            }

            @Override
            public void onSuccess(SessionInfo sessionInfo) {
                setSessionInfo(sessionInfo);
            }
        };
        sesUserService.createNotLoggedInSession(callback);
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
                if (response.getType() != NEW_PASSWORD_OK) {
                    SC.say(i18n.invalidNewPasswordInputs());
                }
                else {
                    SC.say(i18n.passwordSended());
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.LOGIN));
                }
            }
        };
        sesUserService.resetPassword(name, email, callback);
    }

    public void logout(final SessionInfo sessionInfo) {
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedLogout());
            }

            @Override
            public void onSuccess(Void result) {
                createSessionInfo();
                getMainEventBus().fireEvent(new SetRoleEvent(UserRole.LOGOUT));
            }
        };
        sesUserService.logout(sessionInfo, callback);
    }

    public void getUser(final SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(i18n.failedGetUser());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (result.getType() == USER_INFO) {
                    UserDTO user = result.getUser();
                    getMainEventBus().fireEvent(new UpdateProfileEvent(user));
                }
            }
        };
        sesUserService.getUser(sessionInfo, callback);
    }

    public void deleteUser(final SessionInfo sessionInfo, String userIdToDelete) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                final SessionInfo currentSession = currentSession();
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (result.getType().equals(LAST_ADMIN)) {
                    SC.say(i18n.lastAdmin());
                }
                if ( !ClientSessionManager.isAdminLogin()) {
                    getMainEventBus().fireEvent(new LogoutEvent(currentSession));
                }
                if (ClientSessionManager.isAdminLogin()) {
                    getMainEventBus().fireEvent(new GetAllUsersEvent(currentSession));
                }
            }
        };
        sesUserService.deleteUser(sessionInfo, userIdToDelete, callback);
    }

    public void updateUser(final SessionInfo sessionInfo, UserDTO user) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedUpdateUser());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (response.getType().equals(OK)) {
                    SC.say(i18n.updateSuccessful());
                    if (isAdminLogin()) {
                        getMainEventBus().fireEvent(new GetAllUsersEvent(sessionInfo));
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
                    getMainEventBus().fireEvent(new LogoutEvent(sessionInfo));
                }
                else if (response.getType().equals(MAIL)) {
                    SC.say(i18n.mailSent());
                }
                else if (response.getType().equals(ERROR)) {
                    SC.say(i18n.passwordDoNotMatch());
                }
            }
        };
        sesUserService.updateUser(sessionInfo, user, callback);
    }

    public void subscribe(final SessionInfo sessionInfo, final String uuid, final String medium, final String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                SesClientResponseType type = result.getType();

                switch (type) {
                case REQUIRES_LOGIN:
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                    break;
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

                    String finalMessage = message + i18n.subscribeSuccessful2() + "<br/><br/>"
                            + i18n.subscriptionInfo();
                    SC.say(finalMessage);

                    getMainEventBus().fireEvent(new GetAllOwnRulesEvent(sessionInfo, false));
                    getMainEventBus().fireEvent(new GetAllOtherRulesEvent(sessionInfo, false));
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
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(sessionInfo, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(sessionInfo, false));
                    break;
                default:
                    break;
                }
            }
        };
        this.sesRulesService.subscribe(sessionInfo, uuid, medium, format, callback);
    }

    public void createBasicRule(final SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName) {

        ServerCallback<SesClientResponse> callback = new CreateSimpleRuleCallback(this, "Could not create rule.") {
            @Override
            public void onSuccess(SesClientResponse result) {
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (result.getType() == RULE_NAME_NOT_EXISTS) {
                    Rule basicRule = result.getBasicRule();
                    EventBus.getMainEventBus().fireEvent(new RuleCreatedEvent(basicRule));
                }
                else {
                    // TODO handle more result types
                    SC.say(i18n.creatingRuleWasUnsuccessful());
                }
            }
        };
        this.sesRulesService.createBasicRule(sessionInfo, rule, edit, oldRuleName, callback);
    }

    public void getAllUsers(final SessionInfo sessionInfo) {
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
        sesUserService.getAllUsers(sessionInfo, callback);
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

    public void getAllOwnRules(final SessionInfo sessionInfo, boolean edit) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else {
                    getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }
        };
        sesRulesService.getAllOwnRules(sessionInfo, edit, callback);
    }

    public void getAllOtherRules(final SessionInfo sessionInfo, boolean edit) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(response));
            }
        };
        this.sesRulesService.getAllOtherRules(sessionInfo, edit, callback);
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

    public void publishRule(final SessionInfo sessionInfo, String ruleName, boolean published, String role) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedPublishRule());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                if (response.getType().equals(PUBLISH_RULE_USER)) {
                    GetAllOwnRulesEvent event = new GetAllOwnRulesEvent(sessionInfo, true);
                    EventBus.getMainEventBus().fireEvent(event);
                }
                else {
                    EventBus.getMainEventBus().fireEvent(new GetAllRulesEvent(sessionInfo));
                }

            }
        };
        this.sesRulesService.publishRule(sessionInfo, ruleName, published, callback);
    }

    public void getAllRules(final SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(i18n.failedGetAllRules());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else {
                    getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }
        };
        this.sesRulesService.getAllRules(sessionInfo, callback);
    }

    public void deleteRule(final SessionInfo sessionInfo, String uuid, final String role) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (response.getType().equals(DELETE_RULE_SUBSCRIBED)) {
                    SC.say(i18n.ruleSubscribed());
                }
                else {
                    // update list
                    if (role.equals("ADMIN")) {
                        getMainEventBus().fireEvent(new GetAllRulesEvent(sessionInfo));
                    }
                    else {
                        getMainEventBus().fireEvent(new GetAllOwnRulesEvent(sessionInfo, true));
                    }
                }
            }
        };
        this.sesRulesService.deleteRule(sessionInfo, uuid, callback);
    }

    public void deleteTimeseriesFeed(String timeseriesFeed) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else {
                    getMainEventBus().fireEvent(new GetAllRulesEvent(currentSession()));
                }
            }
        };
        this.sesTimeseriesService.deleteTimeseriesFeed(timeseriesFeed, callback);
    }

    public void getEditRule(String ruleName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {

            public void onSuccess(SesClientResponse result) {
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (result.getType().equals(SesClientResponseType.EDIT_SIMPLE_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_SIMPLE));
                    EventBus.getMainEventBus().fireEvent(new EditSimpleRuleEvent(result.getBasicRule()));
                }
                else if (result.getType().equals(SesClientResponseType.EDIT_COMPLEX_RULE)) {
                    EventBus.getMainEventBus().fireEvent(new ChangeLayoutEvent(LayoutType.CREATE_COMPLEX));
                    EventBus.getMainEventBus().fireEvent(new InformUserEvent(result));
                }
            }

            public void onFailure(Throwable caught) {
                getToasterInstance().addErrorMessage(caught.getMessage());
            }
        };
        this.sesRulesService.getRuleForEditing(ruleName, callback);

    }

    public void getAllPublishedRules(final SessionInfo sessionInfo, int operator) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                if (response.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else {
                    getMainEventBus().fireEvent(new InformUserEvent(response));
                }
            }
        };
        this.sesRulesService.getAllPublishedRules(sessionInfo, operator, callback);
    }

    public void unsubscribe(final SessionInfo sessionInfo, String uuid, String medium, String format) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse response) {
                SesClientResponseType type = response.getType();

                switch (type) {
                case REQUIRES_LOGIN:
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                    break;
                case OK:
                    SC.say(i18n.unsubscribeSuccessful());
                    final SessionInfo currentSession = currentSession();
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(currentSession, false));
                    EventBus.getMainEventBus().fireEvent(new GetAllOtherRulesEvent(currentSession, false));
                    break;
                case ERROR_UNSUBSCRIBE_SES:
                    Toaster.getToasterInstance().addErrorMessage(i18n.errorUnsubscribeSES());
                    break;
                default:
                    break;
                }
            }
        };
        this.sesRulesService.unSubscribe(sessionInfo, uuid, medium, format, callback);
    }

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

    public void createComplexRule(final SessionInfo sessionInfo, ComplexRuleData rule, boolean edit, String oldName) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {

                if (REQUIRES_LOGIN == result.getType()) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else if (result.getType().equals(RULE_NAME_EXISTS)) {
                    SC.say(i18n.ruleExists());
                }
                else if (result.getType().equals(OK)) {
                    SC.say(i18n.creationSuccessful());
                    if (isUserLogin()) {
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(ABOS));
                    }
                    else if (isAdminLogin()) {
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(RULELIST));
                    }
                }
                else if (result.getType().equals(EDIT_COMPLEX_RULE)) {
                    if (isUserLogin()) {
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(EDIT_RULES));
                    }
                    else if (isAdminLogin()) {
                        getMainEventBus().fireEvent(new ChangeLayoutEvent(RULELIST));
                    }
                }
            }
        };
        this.sesRulesService.createComplexRule(sessionInfo, rule, edit, oldName, callback);
    }

    public void getUserSubscriptions(final SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            public void onSuccess(SesClientResponse result) {
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                else {
                    getMainEventBus().fireEvent(new InformUserEvent(result));
                }
            }
        };
        this.sesRulesService.getUserSubscriptions(sessionInfo, callback);
    }

    public void deleteProfile(final SessionInfo sessionInfo) {
        AsyncCallback<SesClientResponse> callback = new AsyncCallback<SesClientResponse>() {
            @Override
            public void onFailure(Throwable arg0) {
                Toaster.getToasterInstance().addErrorMessage(arg0.getMessage());
            }

            @Override
            public void onSuccess(SesClientResponse result) {
                if (result.getType() == REQUIRES_LOGIN) {
                    getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                }
                SC.say(i18n.profileDelete());
            }
        };
        sesUserService.requestToDeleteProfile(sessionInfo, callback);
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
                    EventBus.getMainEventBus().fireEvent(new GetAllOwnRulesEvent(currentSession(), true));
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