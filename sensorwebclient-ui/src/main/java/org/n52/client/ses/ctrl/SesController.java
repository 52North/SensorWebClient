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
package org.n52.client.ses.ctrl;

import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.isPresentSessionInfo;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ServiceController;
import org.n52.client.ses.event.CopyEvent;
import org.n52.client.ses.event.CreateComplexRuleEvent;
import org.n52.client.ses.event.CreateSimpleRuleEvent;
import org.n52.client.ses.event.DeleteProfileEvent;
import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.DeleteSensorEvent;
import org.n52.client.ses.event.DeleteUserEvent;
import org.n52.client.ses.event.EditRuleEvent;
import org.n52.client.ses.event.GetAllOtherRulesEvent;
import org.n52.client.ses.event.GetAllOwnRulesEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.GetAllRulesEvent;
import org.n52.client.ses.event.GetAllUsersEvent;
import org.n52.client.ses.event.GetDataEvent;
import org.n52.client.ses.event.GetPhenomenaEvent;
import org.n52.client.ses.event.GetRegisteredSensorsEvent;
import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.GetStationsEvent;
import org.n52.client.ses.event.GetTermsOfUseEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.LoginEvent;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.NewPasswordEvent;
import org.n52.client.ses.event.PublishRuleEvent;
import org.n52.client.ses.event.RegisterUserEvent;
import org.n52.client.ses.event.RuleNameExistsEvent;
import org.n52.client.ses.event.SearchEvent;
import org.n52.client.ses.event.SubscribeEvent;
import org.n52.client.ses.event.UnsubscribeEvent;
import org.n52.client.ses.event.UpdateSensorEvent;
import org.n52.client.ses.event.UpdateUserEvent;
import org.n52.client.ses.event.handler.CopyEventHandler;
import org.n52.client.ses.event.handler.CreateComplexRuleEventHandler;
import org.n52.client.ses.event.handler.CreateSimpleRuleEventHandler;
import org.n52.client.ses.event.handler.DeleteProfileEventHandler;
import org.n52.client.ses.event.handler.DeleteRuleEventHandler;
import org.n52.client.ses.event.handler.DeleteSensorEventHandler;
import org.n52.client.ses.event.handler.DeleteUserEventHandler;
import org.n52.client.ses.event.handler.EditRuleEventHandler;
import org.n52.client.ses.event.handler.GetAllOtherRulesEventHandler;
import org.n52.client.ses.event.handler.GetAllOwnRulesEventHandler;
import org.n52.client.ses.event.handler.GetAllPublishedRulesEventHandler;
import org.n52.client.ses.event.handler.GetAllRulesEventHandler;
import org.n52.client.ses.event.handler.GetAllUsersEventHandler;
import org.n52.client.ses.event.handler.GetDataEventHandler;
import org.n52.client.ses.event.handler.GetPhenomenaEventHandler;
import org.n52.client.ses.event.handler.GetRegisteredSensorsEventHandler;
import org.n52.client.ses.event.handler.GetSingleUserEventHandler;
import org.n52.client.ses.event.handler.GetStationsEventHandler;
import org.n52.client.ses.event.handler.GetTermsofUseEventHandler;
import org.n52.client.ses.event.handler.GetUserSubscriptionsEventHandler;
import org.n52.client.ses.event.handler.LoginEventHandler;
import org.n52.client.ses.event.handler.LogoutEventHandler;
import org.n52.client.ses.event.handler.NewPasswordEventHandler;
import org.n52.client.ses.event.handler.PublishRuleEventHandler;
import org.n52.client.ses.event.handler.RegisterUserEventHandler;
import org.n52.client.ses.event.handler.RuleNameExistsEventHandler;
import org.n52.client.ses.event.handler.SearchEventHandler;
import org.n52.client.ses.event.handler.SubscribeEventHandler;
import org.n52.client.ses.event.handler.UnsubscribeEventHandler;
import org.n52.client.ses.event.handler.UpdateSensorEventHandler;
import org.n52.client.ses.event.handler.UpdateUserEventHandler;

public class SesController extends ServiceController {

    private SesRequestManager rm;

    public SesController() {
        this.setRm(new SesRequestManager());
        new SesControllerEventBroker();
        if (isPresentSessionInfo()) {
            rm.validate(currentSession());
        } else {
//            getMainEventBus().fireEvent(new SetRoleEvent(LOGOUT));
            rm.createSessionInfo();
        }
    }

    public void setRm(SesRequestManager rm) {
        this.rm = rm;
    }

    public SesRequestManager getRm() {
        return this.rm;
    }

    private class SesControllerEventBroker implements
            RegisterUserEventHandler,
            LoginEventHandler,
            NewPasswordEventHandler,
            LogoutEventHandler,
            GetSingleUserEventHandler,
            DeleteUserEventHandler,
            UpdateUserEventHandler,
            SubscribeEventHandler,
            CreateSimpleRuleEventHandler,
            GetAllUsersEventHandler,
            GetStationsEventHandler,
            GetPhenomenaEventHandler,
            GetAllOwnRulesEventHandler,
            GetAllOtherRulesEventHandler,
            GetRegisteredSensorsEventHandler,
            UpdateSensorEventHandler,
            PublishRuleEventHandler,
            GetAllRulesEventHandler,
            DeleteRuleEventHandler,
            DeleteSensorEventHandler,
            EditRuleEventHandler,
            GetAllPublishedRulesEventHandler,
            UnsubscribeEventHandler,
            RuleNameExistsEventHandler,
            CreateComplexRuleEventHandler,
            GetUserSubscriptionsEventHandler,
            DeleteProfileEventHandler,
            GetTermsofUseEventHandler,
            SearchEventHandler,
            CopyEventHandler,
            GetDataEventHandler {

        public SesControllerEventBroker() {
            EventBus.getMainEventBus().addHandler(RegisterUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(LoginEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(NewPasswordEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(LogoutEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetSingleUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UpdateUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SubscribeEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(CreateSimpleRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetAllUsersEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetStationsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetPhenomenaEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetAllOwnRulesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetAllOtherRulesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetRegisteredSensorsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UpdateSensorEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(PublishRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetAllRulesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteSensorEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(EditRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetAllPublishedRulesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UnsubscribeEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(RuleNameExistsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(CreateComplexRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetUserSubscriptionsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteProfileEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetTermsOfUseEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SearchEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(CopyEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetDataEvent.TYPE, this);
        }

        public void onRegisterUser(RegisterUserEvent evt) {
            SesController.this.getRm().registerUser(evt.getUser());
        }

        public void onLogin(LoginEvent evt) {
            SesController.this.getRm().login(evt.getName(), evt.getPassword(), evt.getSessionInfo());
        }

        public void onNewPassword(NewPasswordEvent evt) {
            SesController.this.getRm().newPassword(evt.getName(), evt.getEmail());
        }

        public void onLogout(LogoutEvent evt) {
            SesController.this.getRm().logout(evt.getSessionInfo());
        }

        public void onGetSingleUser(GetSingleUserEvent evt) {
            SesController.this.getRm().getUser(evt.getSessionInfo());
        }

        public void onDeleteUser(DeleteUserEvent evt) {
            SesController.this.getRm().deleteUser(evt.getSessionInfo(), evt.getUserId());
        }

        public void onUpdate(UpdateUserEvent evt) {
            SesController.this.getRm().updateUser(evt.getSessionInfo(), evt.getUser());
        }

        public void onSubscribe(SubscribeEvent evt) {
            SesController.this.getRm().subscribe(evt.getSessionInfo(), evt.getUuid(), evt.getMedium(), evt.getFormat());
        }

        public void onCreate(CreateSimpleRuleEvent evt) {
            SesController.this.getRm().createBasicRule(evt.getSessionInfo(), evt.getRule(), evt.isEdit(), evt.getOldRuleName());
        }

        public void onGetAllUser(GetAllUsersEvent evt) {
            SesController.this.getRm().getAllUsers(evt.getSessionInfo());
        }

        public void onGet(GetStationsEvent evt) {
            SesController.this.getRm().getStations();
        }

        public void onGet(GetPhenomenaEvent evt) {
            SesController.this.getRm().getPhenomena(evt.getSensor());
        }

        public void onGet(GetAllOwnRulesEvent evt) {
            SesController.this.getRm().getAllOwnRules(evt.getSessionInfo(), evt.isEdit());
        }

        public void onGet(GetAllOtherRulesEvent evt) {
            SesController.this.getRm().getAllOtherRules(evt.getSessionInfo(), evt.isEdit());
        }

        public void onGet(GetRegisteredSensorsEvent evt) {
            SesController.this.getRm().getRegisteredTimeseriesFeeds();
        }

        public void onUpdateSensor(UpdateSensorEvent evt) {
            SesController.this.getRm().upateTimeseriesFeed(evt.getId(), evt.isStatus());
        }

        public void onPublish(PublishRuleEvent evt) {
            SesController.this.getRm().publishRule(evt.getSessionInfo(), evt.getRuleName(), evt.isPublished(), evt.getRole());
        }

        public void onGet(GetAllRulesEvent evt) {
            SesController.this.getRm().getAllRules(evt.getSessionInfo());
        }

        public void onDeleteRule(DeleteRuleEvent evt) {
            SesController.this.getRm().deleteRule(evt.getSessionInfo(), evt.getUuid(), evt.getRole());
        }

        public void onDeleteSensor(DeleteSensorEvent evt) {
            SesController.this.getRm().deleteTimeseriesFeed(evt.getSensorID());
        }

        public void onGet(EditRuleEvent evt) {
            SesController.this.getRm().getEditRule(evt.getRuleName());
        }

        public void onGet(GetAllPublishedRulesEvent evt) {
            SesController.this.getRm().getAllPublishedRules(evt.getSessionInfo(), evt.getOperator());
        }

        public void onUnsubscribe(UnsubscribeEvent evt) {
            SesController.this.getRm().unsubscribe(evt.getSessionInfo(), evt.getUuid(), evt.getMedium(), evt.getFormat());
        }

        public void onExists(RuleNameExistsEvent evt) {
            SesController.this.getRm().ruleNameExists(evt.getRuleName());
        }

        public void onCreate(CreateComplexRuleEvent evt) {
            SesController.this.getRm().createComplexRule(evt.getSessionInfo(), evt.getRule(), evt.isEdit(), evt.getOldName());
        }

        public void onGet(GetUserSubscriptionsEvent evt) {
            SesController.this.getRm().getUserSubscriptions(evt.getSessionInfo());
        }

        public void onDeleteProfile(DeleteProfileEvent evt) {
            SesController.this.getRm().deleteProfile(evt.getSessionInfo());
        }

        public void onGet(GetTermsOfUseEvent evt) {
            SesController.this.getRm().getTermsOfUse(evt.getLanguage());

        }

        public void onSearch(SearchEvent evt) {
            SesController.this.getRm().search(evt.getText(), evt.getCriterion(), evt.getUserID());
        }

        public void onCopy(CopyEvent evt) {
            SesController.this.getRm().copy(evt.getUserID(), evt.getRuleName());
        }

        public void onGetData(GetDataEvent evt) {
            SesController.this.getRm().getData();
        }
    }
}