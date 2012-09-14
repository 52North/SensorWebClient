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
package org.n52.client.control.service;

import org.n52.client.control.ServiceController;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.ses.CopyEvent;
import org.n52.client.eventBus.events.ses.CreateComplexRuleEvent;
import org.n52.client.eventBus.events.ses.CreateSimpleRuleEvent;
import org.n52.client.eventBus.events.ses.DeleteProfileEvent;
import org.n52.client.eventBus.events.ses.DeleteRuleEvent;
import org.n52.client.eventBus.events.ses.DeleteSensorEvent;
import org.n52.client.eventBus.events.ses.DeleteUserEvent;
import org.n52.client.eventBus.events.ses.GetAllPublishedRulesEvent;
import org.n52.client.eventBus.events.ses.GetDataEvent;
import org.n52.client.eventBus.events.ses.EditRuleEvent;
import org.n52.client.eventBus.events.ses.GetAllOtherRulesEvent;
import org.n52.client.eventBus.events.ses.GetAllOwnRulesEvent;
import org.n52.client.eventBus.events.ses.GetAllRulesEvent;
import org.n52.client.eventBus.events.ses.GetAllUsersEvent;
import org.n52.client.eventBus.events.ses.GetPhenomenaEvent;
import org.n52.client.eventBus.events.ses.GetRegisteredSensorsEvent;
import org.n52.client.eventBus.events.ses.GetSingleUserEvent;
import org.n52.client.eventBus.events.ses.GetStationsEvent;
import org.n52.client.eventBus.events.ses.GetTermsOfUseEvent;
import org.n52.client.eventBus.events.ses.GetUserSubscriptionsEvent;
import org.n52.client.eventBus.events.ses.LoginEvent;
import org.n52.client.eventBus.events.ses.LogoutEvent;
import org.n52.client.eventBus.events.ses.NewPasswordEvent;
import org.n52.client.eventBus.events.ses.PublishRuleEvent;
import org.n52.client.eventBus.events.ses.RegisterUserEvent;
import org.n52.client.eventBus.events.ses.RuleNameExistsEvent;
import org.n52.client.eventBus.events.ses.SearchEvent;
import org.n52.client.eventBus.events.ses.SubscribeEvent;
import org.n52.client.eventBus.events.ses.UnsubscribeEvent;
import org.n52.client.eventBus.events.ses.UpdateSensorEvent;
import org.n52.client.eventBus.events.ses.UpdateUserEvent;
import org.n52.client.eventBus.events.ses.handler.CopyEventHandler;
import org.n52.client.eventBus.events.ses.handler.CreateComplexRuleEventHandler;
import org.n52.client.eventBus.events.ses.handler.CreateSimpleRuleEventHandler;
import org.n52.client.eventBus.events.ses.handler.DeleteProfileEventHandler;
import org.n52.client.eventBus.events.ses.handler.DeleteRuleEventHandler;
import org.n52.client.eventBus.events.ses.handler.DeleteSensorEventHandler;
import org.n52.client.eventBus.events.ses.handler.DeleteUserEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetAllPublishedRulesEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetDataEventHandler;
import org.n52.client.eventBus.events.ses.handler.EditRuleEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetAllOtherRulesEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetAllOwnRulesEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetAllRulesEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetAllUsersEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetPhenomenaEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetRegisteredSensorsEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetSingleUserEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetStationsEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetTermsofUseEventHandler;
import org.n52.client.eventBus.events.ses.handler.GetUserSubscriptionsEventHandler;
import org.n52.client.eventBus.events.ses.handler.LoginEventHandler;
import org.n52.client.eventBus.events.ses.handler.LogoutEventHandler;
import org.n52.client.eventBus.events.ses.handler.NewPasswordEventHandler;
import org.n52.client.eventBus.events.ses.handler.PublishRuleEventHandler;
import org.n52.client.eventBus.events.ses.handler.RegisterUserEventHandler;
import org.n52.client.eventBus.events.ses.handler.RuleNameExistsEventHandler;
import org.n52.client.eventBus.events.ses.handler.SearchEventHandler;
import org.n52.client.eventBus.events.ses.handler.SubscribeEventHandler;
import org.n52.client.eventBus.events.ses.handler.UnsubscribeEventHandler;
import org.n52.client.eventBus.events.ses.handler.UpdateSensorEventHandler;
import org.n52.client.eventBus.events.ses.handler.UpdateUserEventHandler;
import org.n52.client.model.communication.requestManager.SesRequestManager;

public class SesController extends ServiceController {

    private SesRequestManager rm;

    public SesController() {
        this.setRm(new SesRequestManager());
        new SesControllerEventBroker();
    }

    public void setRm(SesRequestManager rm) {
        this.rm = rm;
    }

    public SesRequestManager getRm() {
        return this.rm;
    }

    private class SesControllerEventBroker implements RegisterUserEventHandler, LoginEventHandler, NewPasswordEventHandler,
    LogoutEventHandler, GetSingleUserEventHandler, DeleteUserEventHandler, UpdateUserEventHandler,
    SubscribeEventHandler, CreateSimpleRuleEventHandler, GetAllUsersEventHandler, GetStationsEventHandler,
    GetPhenomenaEventHandler, GetAllOwnRulesEventHandler, GetAllOtherRulesEventHandler,
    GetRegisteredSensorsEventHandler, UpdateSensorEventHandler, PublishRuleEventHandler, GetAllRulesEventHandler,
    DeleteRuleEventHandler, DeleteSensorEventHandler, EditRuleEventHandler, GetAllPublishedRulesEventHandler, 
    UnsubscribeEventHandler, RuleNameExistsEventHandler, CreateComplexRuleEventHandler, GetUserSubscriptionsEventHandler,
    DeleteProfileEventHandler, GetTermsofUseEventHandler, SearchEventHandler, CopyEventHandler, GetDataEventHandler{

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
            SesController.this.getRm().login(evt.getName(), evt.getPassword());
        }

        public void onNewPassword(NewPasswordEvent evt) {
            SesController.this.getRm().newPassword(evt.getName(), evt.getEmail());
        }

        public void onLogout(LogoutEvent evt) {
            SesController.this.getRm().logout();
        }

        public void onGetSingleUser(GetSingleUserEvent evt) {
            SesController.this.getRm().getUser(evt.getId());
        }

        public void onDeleteUser(DeleteUserEvent evt) {
            SesController.this.getRm().deleteUser(evt.getId());
        }

        public void onUpdate(UpdateUserEvent evt) {
            SesController.this.getRm().updateUser(evt.getUser(), evt.getUserID());
        }

        public void onSubscribe(SubscribeEvent evt) {
            SesController.this.getRm().subscribe(evt.getUserID(), evt.getRuleName(), evt.getMedium(), evt.getFormat());
        }

        public void onCreate(CreateSimpleRuleEvent evt) {
            SesController.this.getRm().createBasicRule(evt.getRule(), evt.isEdit(), evt.getOldRuleName());
        }

        public void onGetAllUser(GetAllUsersEvent evt) {
            SesController.this.getRm().getAllUsers();
        }

        public void onGet(GetStationsEvent evt) {
            SesController.this.getRm().getStations();
        }

        public void onGet(GetPhenomenaEvent evt) {
            SesController.this.getRm().getPhenomena(evt.getSensor());
        }

        public void onGet(GetAllOwnRulesEvent evt) {
            SesController.this.getRm().getAllOwnRules(evt.getId(), evt.isEdit());
        }

        public void onGet(GetAllOtherRulesEvent evt) {
            SesController.this.getRm().getAllOtherRules(evt.getId(), evt.isEdit());
        }

        public void onGet(GetRegisteredSensorsEvent evt) {
            SesController.this.getRm().getRegisteredSensors();
        }

        public void onUpdateSensor(UpdateSensorEvent evt) {
            SesController.this.getRm().upateSensor(evt.getId(), evt.isStatus());
        }

        public void onPublish(PublishRuleEvent evt) {
            SesController.this.getRm().publishRule(evt.getRuleName(), evt.isValue(), evt.getRole());
        }

        public void onGet(GetAllRulesEvent evt) {
            SesController.this.getRm().getAllRules(); 
        }

        public void onDeleteRule(DeleteRuleEvent evt) {
            SesController.this.getRm().deleteRule(evt.getRuleName(), evt.getRole());
        }
        
        public void onDeleteSensor(DeleteSensorEvent evt) {
            SesController.this.getRm().deleteSensor(evt.getSensorID());
        }
        
        public void onGet(EditRuleEvent evt) {
            SesController.this.getRm().getEditRule(evt.getRuleName());
        }
        
        public void onGet(GetAllPublishedRulesEvent evt) {
            SesController.this.getRm().getAllPublishedRules(evt.getOperator());
        }

        public void onUnsubscribe(UnsubscribeEvent evt) {
            SesController.this.getRm().unsubscribe(evt.getRuleName(), evt.getUserID(), evt.getMedium(), evt.getFormat());
        }

        public void onExists(RuleNameExistsEvent evt) {
            SesController.this.getRm().ruleNameExists(evt.getRuleName());
        }

        public void onCreate(CreateComplexRuleEvent evt) {
            SesController.this.getRm().createComplexRule(evt.getRule(), evt.isEdit(), evt.getOldName());
        }

        public void onGet(GetUserSubscriptionsEvent evt) {
            SesController.this.getRm().getUserSubscriptions(evt.getUserID());
        }

        public void onDeleteProfile(DeleteProfileEvent evt) {
            SesController.this.getRm().deleteProfile(evt.getId());
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