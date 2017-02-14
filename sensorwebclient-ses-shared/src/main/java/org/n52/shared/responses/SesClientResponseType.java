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
package org.n52.shared.responses;

public enum SesClientResponseType {
    LOGIN_OK, LAST_ADMIN, LOGOUT, MAIL, TERMS_OF_USE, LOGIN_USER, LOGIN_ADMIN, LOGIN_NAME, LOGIN_PASSWORD, LOGIN_ACTIVATED, LOGIN_LOCKED, OK, EDIT_COMPLEX_RULE, NEW_PASSWORD_ERROR, NEW_PASSWORD_OK, REGISTER_NAME, REGISTER_OK, REGSITER_EMAIL, REGISTER_HANDY, ERROR, STATIONS, SENSORS, PHENOMENA, OWN_RULES, OTHER_RULES, REGISTERED_TIMESERIES_FEEDS, EDIT_OWN_RULES, EDIT_OTHER_RULES, PUBLISH_RULE_USER, PUBLISH_RULE_ADMIN, All_RULES, DELETE_RULE_OK, DELETE_SENSOR_OK, EDIT_SIMPLE_RULE, ALL_PUBLISHED_RULES, RULE_NAME_NOT_EXISTS, RULE_NAME_EXISTS, MESSAGE, USER_SUBSCRIPTIONS, DELETE_RULE_SUBSCRIBED, ERROR_SUBSCRIBE_SES, ERROR_SUBSCRIBE_FEEDER, ERROR_UNSUBSCRIBE_SES, SEARCH_RESULT, SUBSCRIPTION_EXISTS, SET_USERNAME, DATA, REQUIRES_LOGIN, USER_INFO, ALL_USERS, 
}