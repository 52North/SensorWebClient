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

package org.n52.shared.responses;

public enum SesClientResponseType {
    LOGIN_OK, LAST_ADMIN, LOGOUT, MAIL, TERMS_OF_USE, LOGIN_USER, LOGIN_ADMIN, LOGIN_NAME, LOGIN_PASSWORD, LOGIN_ACTIVATED, LOGIN_LOCKED, OK, EDIT_COMPLEX_RULE, NEW_PASSWORD_ERROR, NEW_PASSWORD_OK, REGISTER_NAME, REGISTER_OK, REGSITER_EMAIL, REGISTER_HANDY, ERROR, STATIONS, SENSORS, PHENOMENA, OWN_RULES, OTHER_RULES, REGISTERED_TIMESERIES_FEEDS, EDIT_OWN_RULES, EDIT_OTHER_RULES, PUBLISH_RULE_USER, PUBLISH_RULE_ADMIN, All_RULES, DELETE_RULE_OK, DELETE_SENSOR_OK, EDIT_SIMPLE_RULE, ALL_PUBLISHED_RULES, RULE_NAME_NOT_EXISTS, RULE_NAME_EXISTS, MESSAGE, USER_SUBSCRIPTIONS, DELETE_RULE_SUBSCRIBED, ERROR_SUBSCRIBE_SES, ERROR_SUBSCRIBE_FEEDER, ERROR_UNSUBSCRIBE_SES, SEARCH_RESULT, SUBSCRIPTION_EXISTS, SET_USERNAME, DATA, REQUIRES_LOGIN, USER_INFO, ALL_USERS, 
}