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
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.client.ses.ui.FormLayout.LayoutType.USERLIST;
import static org.n52.client.ses.ui.FormLayout.LayoutType.WELCOME;
import static org.n52.client.ui.View.getView;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;

import java.util.ArrayList;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ATabEventBroker;
import org.n52.client.ctrl.Controller;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.ctrl.GUIException;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.GetAllUsersEvent;
import org.n52.client.ses.event.GetDataEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.ShowAllUserEvent;
import org.n52.client.ses.event.handler.ChangeLayoutEventHandler;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.client.ses.event.handler.ShowAllUserEventHandler;
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ses.ui.SesTab;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.legend.Timeseries;
import org.n52.client.ui.View;
import org.n52.client.ui.legend.LegendElement;
import org.n52.shared.serializable.pojos.UserRole;

public class SesTabController extends Controller<SesTab> {
    
    public SesTabController(SesTab sesTab) {
        super(sesTab);
        new SesTabEventBroker();
        new SesRequestManager();

        try {
            dataControls = new DataControlsSes(this);
        } catch (Exception e) {
            ExceptionHandler.handleException(new GUIException(i18n.failedLoadControls(), e));
        }
        EventBus.getMainEventBus().fireEvent(new GetDataEvent());
    }

    public DataControlsSes getDataControls() {
        return (DataControlsSes) dataControls;
    }

    private class SesTabEventBroker extends ATabEventBroker implements ChangeLayoutEventHandler, SetRoleEventHandler,
            ShowAllUserEventHandler,/* InformUserEventHandler,*/ TimeSeriesChangedEventHandler, 
            TabSelectedEventHandler {

        public SesTabEventBroker() {
            EventBus.getMainEventBus().addHandler(ChangeLayoutEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ShowAllUserEvent.TYPE, this);
//            EventBus.getMainEventBus().addHandler(InformUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TabSelectedEvent.TYPE, this);
        }

        public void onChange(ChangeLayoutEvent evt) {
            LayoutType layout = evt.getLayout();
            UserRole role = getDataControls().getRole();
            if (role != ADMIN) {
                return; // only admin shall use old UI
            }
            
            SesTabController.this.getTab().setLayout(layout);
            if (layout == USERLIST) {
                getMainEventBus().fireEvent(new GetAllUsersEvent(currentSession()));
                getDataControls().highlightSelectedButton(getDataControls().getManageUserButton());
            } 
            
//            else if (layout == CREATE_COMPLEX) {
//                getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(currentSession(), 1));
//                getTab().getComplexLayout().clearFields();
//                getTab().getComplexLayout().setEditCR(false);
//                getDataControls().highlightSelectedButton(getDataControls().getCreateComplexRuleButton());
//            } else if (layout == EDIT_PROFILE) {
//                getDataControls().highlightSelectedButton(getDataControls().getEditProfileButton());
//                getMainEventBus().fireEvent(new GetSingleUserEvent(currentSession()));
//            } else if (layout == ABOS) {
//                getDataControls().highlightSelectedButton(getDataControls().getAboRuleButton());
//                getMainEventBus().fireEvent(new GetAllOwnRulesEvent(currentSession(), false));
//                getMainEventBus().fireEvent(new GetAllOtherRulesEvent(currentSession(), false));
//            } else if (layout == EDIT_RULES) {
//                getDataControls().highlightSelectedButton(getDataControls().getEditRulesButton());
//                getMainEventBus().fireEvent(new GetAllOwnRulesEvent(currentSession(), true));
//                getMainEventBus().fireEvent(new GetAllOtherRulesEvent(currentSession(), true));
//            } else if (layout == RULELIST) {
//                getDataControls().highlightSelectedButton(getDataControls().getManageRulesButton());
//                getMainEventBus().fireEvent(new GetAllRulesEvent(currentSession()));
//            } else if (layout == PASSWORD) {
//                getDataControls().highlightSelectedButton(getDataControls().getGetPasswordButton());
//                getTab().getForgorPasswordLayout().clearFields();
//            } else if (layout == REGISTER) {
//                getDataControls().highlightSelectedButton(getDataControls().getRegisterButton());
//                getTab().getRegisterLayout().clearFields();
//            } else if (layout == LOGIN) {
//                getDataControls().highlightSelectedButton(getDataControls().getLoginButton());
//                getTab().getLoginLayout().clearFields();
//            } 
        }

        public void onChangeRole(SetRoleEvent evt) {
            UserRole role = evt.getRole();
            switch (role) {
            case ADMIN:
            	getDataControls().setRole(role);
                getMainEventBus().fireEvent(new ChangeLayoutEvent(WELCOME));
                break;
            case NOT_REGISTERED_USER:
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                break;
            case LOGOUT: 
                getView().getLegend().switchToDiagramTab();
                break;
            default:
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
            }
        }

        public void onShow(ShowAllUserEvent evt) {
            getTab().getShowUserLayout().setData(evt.getAllUser());
        }

//        public void onInform(InformUserEvent evt) {
//            SesClientResponseType response = evt.getResponse().getType();
//            switch (response) {
//
//            case TERMS_OF_USE:
//                getTab().getRegisterLayout().setTermsOfUse(evt.getResponse().getMessage());
//                break;
//            case LOGIN_ACTIVATED:
//                SC.say(i18n.accountNotActivated());
//                break;
//            case LOGIN_LOCKED:
//                SC.say(i18n.accountLocked());
//                break;
//            case OWN_RULES:
//                getTab().getRuleLayout().setDataOwnRules(evt.getResponse().getBasicRules(),
//                        evt.getResponse().getComplexRules());
//                break;
//            case OTHER_RULES:
//                getTab().getRuleLayout().setDataOtherRules(evt.getResponse().getBasicRules(),
//                        evt.getResponse().getComplexRules());
//                break;
//            case EDIT_OWN_RULES:
//                getTab().getEditRulesLayout().setOwnData(evt.getResponse().getBasicRules(),
//                        evt.getResponse().getComplexRules());
//                break;
//            case EDIT_OTHER_RULES:
//                getTab().getEditRulesLayout().setOtherData(evt.getResponse().getBasicRules(),
//                        evt.getResponse().getComplexRules());
//                break;
//            case All_RULES:
//                getTab().getAllRulesLayout().setData(evt.getResponse().getBasicRules(), evt.getResponse().getComplexRules());
//                break;
//            case ALL_PUBLISHED_RULES:
//                getTab().getComplexLayout().setRules(evt.getResponse().getBasicRules());
//                break;
//            case EDIT_COMPLEX_RULE:
//                getTab().getComplexLayout().editCR(evt.getResponse());
//                break;
//            case SEARCH_RESULT:
//                getTab().getSearchLayout().setData(evt.getResponse().getBasicRules(), evt.getResponse().getComplexRules());
//                break;
//            }
//        }


        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
            contributeToLegend();
        }

        /**
         * FIXME extract "contributeToLegend"
         */
        private void contributeToLegend() {
            if (isSelfSelectedTab()) {
                ArrayList<LegendElement> legendElems = new ArrayList<LegendElement>();
                Timeseries[] ts = TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted();
                for (int i = 0; i < ts.length; i++) {
                    legendElems.add(ts[i].getLegendElement());
                }
                fillLegend(legendElems);
            }
        }
        
        public void onSelected(TabSelectedEvent evt) {
            if (isSelfSelectedTab()) {
                contributeToLegend();
            }
        }
        
        @Override
        protected boolean isSelfSelectedTab() {
            return View.getView().getCurrentTab().equals(SesTabController.this.getTab());
        }
    }
}