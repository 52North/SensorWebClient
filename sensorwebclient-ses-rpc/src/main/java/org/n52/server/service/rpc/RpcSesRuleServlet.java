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
package org.n52.server.service.rpc;

import javax.servlet.ServletException;

import org.n52.client.service.SesRuleService;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.service.rpc.RpcSesRuleService;
import org.n52.shared.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.n52.server.ses.util.SesServiceConfig;

public class RpcSesRuleServlet extends RemoteServiceServlet implements RpcSesRuleService {

    private static final long serialVersionUID = 3219805776368229776L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesRuleServlet.class);

    private final SesRuleService service = SesServiceConfig.getService("sesRuleService", SesRuleService.class);

     private SesRuleService getService() {
        if (service == null) {
            LOGGER.error("SesRuleService not configured properly. Check 'spring-ses-config.xml'.");
            throw new NullPointerException("SES module not available.");
        }
        return service;
    }

    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
    }

    public synchronized SesClientResponse subscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        return getService().subscribe(sessionInfo, uuid, medium, eml);
    }

    public synchronized SesClientResponse unSubscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        return getService().unSubscribe(sessionInfo, uuid, medium, eml);
    }

    public synchronized SesClientResponse createBasicRule(SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName) throws Exception {
        return getService().createBasicRule(sessionInfo, rule, edit, oldRuleName);
    }

    public synchronized SesClientResponse getAllOwnRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        return getService().getAllOwnRules(sessionInfo, edit);
    }

    public synchronized SesClientResponse getAllOtherRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        return getService().getAllOtherRules(sessionInfo, edit);
    }

    public synchronized SesClientResponse publishRule(SessionInfo sessionInfo, String ruleName, boolean value) throws Exception {
        return getService().publishRule(sessionInfo, ruleName, value);
    }

    public synchronized SesClientResponse getAllRules(SessionInfo sessionInfo) throws Exception {
        return getService().getAllRules(sessionInfo);
    }

    public synchronized SesClientResponse deleteRule(SessionInfo sessionInfo, String uuid) throws Exception {
        return getService().deleteRule(sessionInfo, uuid);
    }

    public synchronized SesClientResponse getAllPublishedRules(SessionInfo sessionInfo, int operator) throws Exception {
        return getService().getAllPublishedRules(sessionInfo, operator);
    }

    public synchronized SesClientResponse getRuleForEditing(String ruleName) throws Exception {
        return getService().getRuleForEditing(ruleName);
    }

    public synchronized SesClientResponse ruleNameExists(String ruleName) throws Exception {
        return getService().ruleNameExists(ruleName);
    }

    public synchronized SesClientResponse createComplexRule(SessionInfo sessionInfo, ComplexRuleData rule, boolean edit, String oldName) throws Exception {
        return getService().createComplexRule(sessionInfo, rule, edit, oldName);
    }
    public synchronized SesClientResponse getUserSubscriptions(SessionInfo sessionInfo) throws Exception {
        return getService().getUserSubscriptions(sessionInfo);
    }

    public SesClientResponse search(String text, int criterion, String userID) throws Exception {
        return getService().search(text, criterion, userID);
    }

    public SesClientResponse copy(String userID, String ruleName) throws Exception {
        return getService().copy(userID, ruleName);
    }
}