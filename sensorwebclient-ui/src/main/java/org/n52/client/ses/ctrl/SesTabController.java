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
import static org.n52.client.ses.ctrl.SesRequestManager.COOKIE_USER_ID;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.FormLayout.LayoutType.ABOS;
import static org.n52.client.ses.ui.FormLayout.LayoutType.CREATE_COMPLEX;
import static org.n52.client.ses.ui.FormLayout.LayoutType.EDIT_PROFILE;
import static org.n52.client.ses.ui.FormLayout.LayoutType.EDIT_RULES;
import static org.n52.client.ses.ui.FormLayout.LayoutType.LOGIN;
import static org.n52.client.ses.ui.FormLayout.LayoutType.PASSWORD;
import static org.n52.client.ses.ui.FormLayout.LayoutType.REGISTER;
import static org.n52.client.ses.ui.FormLayout.LayoutType.RULELIST;
import static org.n52.client.ses.ui.FormLayout.LayoutType.USERLIST;
import static org.n52.client.ses.ui.FormLayout.LayoutType.USER_SUBSCRIPTIONS;
import static org.n52.client.ses.ui.FormLayout.LayoutType.WELCOME;
import static org.n52.client.ui.View.getView;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;

import java.util.ArrayList;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ATabEventBroker;
import org.n52.client.ctrl.Controller;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.ctrl.GUIException;
import org.n52.client.ses.event.ChangeLayoutEvent;
import org.n52.client.ses.event.GetAllOtherRulesEvent;
import org.n52.client.ses.event.GetAllOwnRulesEvent;
import org.n52.client.ses.event.GetAllPublishedRulesEvent;
import org.n52.client.ses.event.GetAllRulesEvent;
import org.n52.client.ses.event.GetAllUsersEvent;
import org.n52.client.ses.event.GetDataEvent;
import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.ShowAllUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.event.handler.ChangeLayoutEventHandler;
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.client.ses.event.handler.ShowAllUserEventHandler;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.client.ses.ui.FormLayout.LayoutType;
import org.n52.client.ses.ui.SesTab;
import org.n52.client.sos.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.ui.View;
import org.n52.client.ui.legend.LegendElement;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserRole;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.FormItemErrorFormatter;

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
            UpdateProfileEventHandler, ShowAllUserEventHandler, InformUserEventHandler, TimeSeriesChangedEventHandler, 
            TabSelectedEventHandler /*, EditSimpleRuleEventHandler*/ {

        public SesTabEventBroker() {
            EventBus.getMainEventBus().addHandler(ChangeLayoutEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UpdateProfileEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ShowAllUserEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(InformUserEvent.TYPE, this);
//            EventBus.getMainEventBus().addHandler(EditSimpleRuleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TabSelectedEvent.TYPE, this);
        }

        public void onChange(ChangeLayoutEvent evt) {
            // layout to show
            LayoutType layout = evt.getLayout();
            UserRole role = getDataControls().getRole();
            if (role != ADMIN) {
                return; // only admin shall use old UI
            }
            SesTabController.this.getTab().setLayout(layout);
            if (layout == USERLIST) {
                getMainEventBus().fireEvent(new GetAllUsersEvent());
                getDataControls().highlightSelectedButton(getDataControls().getManageUserButton());
            } else if (layout == CREATE_COMPLEX) {
                getMainEventBus().fireEvent(new GetAllPublishedRulesEvent(1));
                getTab().getComplexLayout().clearFields();
                getTab().getComplexLayout().setEditCR(false);
                getDataControls().highlightSelectedButton(getDataControls().getCreateComplexRuleButton());
            } else if (layout == EDIT_PROFILE) {
                getDataControls().highlightSelectedButton(getDataControls().getEditProfileButton());
                getMainEventBus().fireEvent(new GetSingleUserEvent(getCookie(COOKIE_USER_ID)));
            } else if (layout == ABOS) {
                getDataControls().highlightSelectedButton(getDataControls().getAboRuleButton());
                getMainEventBus().fireEvent(new GetAllOwnRulesEvent(getCookie(COOKIE_USER_ID), false));
                getMainEventBus().fireEvent(new GetAllOtherRulesEvent(getCookie(COOKIE_USER_ID), false));
            } else if (layout == EDIT_RULES) {
                getDataControls().highlightSelectedButton(getDataControls().getEditRulesButton());
                getMainEventBus().fireEvent(new GetAllOwnRulesEvent(getCookie(COOKIE_USER_ID), true));
                getMainEventBus().fireEvent(new GetAllOtherRulesEvent(getCookie(COOKIE_USER_ID), true));
            } else if (layout == RULELIST) {
                getDataControls().highlightSelectedButton(getDataControls().getManageRulesButton());
                getMainEventBus().fireEvent(new GetAllRulesEvent());
            } else if (layout == PASSWORD) {
                getDataControls().highlightSelectedButton(getDataControls().getGetPasswordButton());
                getTab().getForgorPasswordLayout().clearFields();
            } else if (layout == REGISTER) {
                getDataControls().highlightSelectedButton(getDataControls().getRegisterButton());
                getTab().getRegisterLayout().clearFields();
            } else if (layout == LOGIN) {
                getDataControls().highlightSelectedButton(getDataControls().getLoginButton());
                getTab().getLoginLayout().clearFields();
            } else if (layout == USER_SUBSCRIPTIONS) {
                getDataControls().highlightSelectedButton(getDataControls().getSubscriptionsButton());
                getMainEventBus().fireEvent(new GetUserSubscriptionsEvent(getCookie(COOKIE_USER_ID)));
            }
        }

        public void onChangeRole(SetRoleEvent evt) {
            UserRole role = evt.getRole();
            switch (role) {
            case ADMIN:
                getMainEventBus().fireEvent(new ChangeLayoutEvent(WELCOME));
                getDataControls().setRole(role);
                setUserLoggedInAsText();
                break;
//            case USER: // we only want admins to use old UI
//                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
//                getMainEventBus().fireEvent(new ChangeLayoutEvent(WELCOME));
//                break;
            case NOT_REGISTERED_USER:
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                break;
            case LOGOUT: 
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
                getView().getLegend().switchToDiagramTab();
                break;
            default:
                getMainEventBus().fireEvent(new ChangeLayoutEvent(LOGIN));
            }
        }

        public void onUpdate(UpdateProfileEvent evt) {
            getTab().getEditProfileLayout().update(evt.getUser());
        }

        public void onShow(ShowAllUserEvent evt) {
            getTab().getShowUserLayout().setData(evt.getAllUser());
        }

        public void onInform(InformUserEvent evt) {
            SesClientResponse.types response = evt.getResponse().getType();
            switch (response) {

            case DATA:
                getDataControls().setWebAppPath((String) evt.getResponse().getBasicRules().get(0));
                getDataControls().setWarnUserLongNotification((Boolean) evt.getResponse().getBasicRules().get(1));
                getDataControls().setMinimumPasswordLength((Integer) evt.getResponse().getBasicRules().get(2));
                getDataControls().setAvailableWNSMedia(((String)evt.getResponse().getBasicRules().get(3)).split(","));
                getDataControls().setDefaultMedium((String) evt.getResponse().getBasicRules().get(4));
                getDataControls().setAvailableFormats(((String)evt.getResponse().getBasicRules().get(5)).split(","));
                getDataControls().setDefaultFormat((String) evt.getResponse().getBasicRules().get(6));
                break;

            case TERMS_OF_USE:
                getTab().getRegisterLayout().setTermsOfUse(evt.getResponse().getMessage());
                break;
            case LOGIN_OK:
                getTab().getWelcomeLayout().setData(evt.getResponse().getUser());
                break;
            case LOGIN_ACTIVATED:
                SC.say(i18n.accountNotActivated());
                break;
            case LOGIN_NAME:
                getTab().getLoginLayout().getNameItem().setValue("");
                getTab().getLoginLayout().update();
                getTab().getLoginLayout().getNameItem().setErrorFormatter(new FormItemErrorFormatter() {
                    public String getErrorHTML(String[] errors) {
                        return "<img src='../img/icons/exclamation.png' alt='invalide name' title='"
                                + i18n.invalidName() + "'/>";
                    }
                });
                break;
            case LOGIN_PASSWORD:
                getTab().getLoginLayout().getPasswordItem().setValue("");
                getTab().getLoginLayout().update();
                getTab().getLoginLayout().getPasswordItem().setErrorFormatter(new FormItemErrorFormatter() {
                    public String getErrorHTML(String[] errors) {
                        return "<img src='../img/icons/exclamation.png' alt='invalide password' title='"
                                + i18n.invalidPassword() + "'/>";
                    }
                });
                break;
            case LOGIN_LOCKED:
                SC.say(i18n.accountLocked());
                break;
                // TODO remove dead code
            // case NEW_PASSWORD_NAME:
            // getTab().getForgorPasswordLayout().getNameItem().setErrorFormatter(new
            // FormItemErrorFormatter() {
            // public String getErrorHTML(String[] errors) {
            // return
            // "<img src='../img/icons/exclamation.png' alt='invalide name' title='"+
            // i18nManager.i18nSESClient.invalidName() +"'/>";
            // }
            // });
            // break;
            // case NEW_PASSWORD_EMAIL:
            // getTab().getForgorPasswordLayout().getEmailItem().setErrorFormatter(new
            // FormItemErrorFormatter() {
            // public String getErrorHTML(String[] errors) {
            // return
            // "<img src='../img/icons/exclamation.png' alt='invalide email' title='"+
            // i18nManager.i18nSESClient.invalidMail() +"'/>";
            // }
            // });
            // break;
                // TODO remove dead code
//            case STATIONS:
//                getTab().getSimpleRuleLayout().setStationsToList(evt.getResponse().getList());
//                break;
//            case PHENOMENA:
//                getTab().getSimpleRuleLayout().setPhenomenonToList(evt.getResponse().getList());
//                getTab().getSimpleRuleLayout().setUnit(evt.getResponse().getComplexList());
//                break;
            case OWN_RULES:
                getTab().getRuleLayout().setDataOwnRules(evt.getResponse().getBasicRules(),
                        evt.getResponse().getComplexRules());
                break;
            case OTHER_RULES:
                getTab().getRuleLayout().setDataOtherRules(evt.getResponse().getBasicRules(),
                        evt.getResponse().getComplexRules());
                break;
            case EDIT_OWN_RULES:
                getTab().getEditRulesLayout().setOwnData(evt.getResponse().getBasicRules(),
                        evt.getResponse().getComplexRules());
                break;
            case EDIT_OTHER_RULES:
                getTab().getEditRulesLayout().setOtherData(evt.getResponse().getBasicRules(),
                        evt.getResponse().getComplexRules());
                break;
            case All_RULES:
                getTab().getAllRulesLayout().setData(evt.getResponse().getBasicRules(), evt.getResponse().getComplexRules());
                break;
            case ALL_PUBLISHED_RULES:
                getTab().getComplexLayout().setRules(evt.getResponse().getBasicRules());
                break;
                // TODO remove dead code
//            case RULE_NAME_EXISTS:
//                getTab().getSimpleRuleLayout().getTitleItem().setErrorFormatter(new FormItemErrorFormatter() {
//                    public String getErrorHTML(String[] errors) {
//                        return "<img src='../img/icons/exclamation.png' alt='rule name allready exists' title='rule name allready exists'/>";
//                    }
//                });
//                break;
            case USER_SUBSCRIPTIONS:
                getTab().getUserSubscriptionsLayout().setData(evt.getResponse().getBasicRules(),
                        evt.getResponse().getComplexRules());
                break;
            case EDIT_COMPLEX_RULE:
                getTab().getComplexLayout().editCR(evt.getResponse());
                break;
            case SEARCH_RESULT:
                getTab().getSearchLayout().setData(evt.getResponse().getBasicRules(), evt.getResponse().getComplexRules());
                break;
            }
        }

        // TODO remove dead code
//        public void onUpdate(EditSimpleRuleEvent evt) {
//            getTab().getSimpleRuleLayout().setEditRule(evt.getBasicRule());
//        }

        private void setUserLoggedInAsText() {
            // get user name from coockie
            String userName = Cookies.getCookie(SesRequestManager.COOKIE_USER_NAME);

            if (userName != null) {
                // build text string
                String text = i18n.loggedinAs() + ": " + userName;

                // set string to all views
                getTab().getShowUserLayout().getUserNameLabel().setText(text);
                getTab().getRuleLayout().getUserNameLabel().setText(text);
                getTab().getComplexLayout().getUserNameLabel().setText(text);
                getTab().getEditProfileLayout().getUserNameLabel().setText(text);
                // TODO remove dead code
//                getTab().getSimpleRuleLayout().getUserNameLabel().setText(text);
                getTab().getAllRulesLayout().getUserNameLabel().setText(text);
                getTab().getEditRulesLayout().getUserNameLabel().setText(text);
                getTab().getUserSubscriptionsLayout().getUserNameLabel().setText(text);
                getTab().getWelcomeLayout().getUserNameLabel().setText(text);
                getTab().getSearchLayout().getUserNameLabel().setText(text);
            }
        }

        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
            contributeToLegend();
        }

        /**
         * FIXME extract "contributeToLegend"
         */
        private void contributeToLegend() {
            if (isSelfSelectedTab()) {
                ArrayList<LegendElement> legendElems = new ArrayList<LegendElement>();
                TimeSeries[] ts = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
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