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
package org.n52.server.service.rpc;

import javax.servlet.ServletException;

import org.n52.client.service.SesRuleService;
import org.n52.server.util.ContextLoader;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.service.rpc.RpcSesRuleService;
import org.n52.shared.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcSesRuleServlet extends RemoteServiceServlet implements RpcSesRuleService {

    private static final long serialVersionUID = 3219805776368229776L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesRuleServlet.class);
    
    private SesRuleService service = ContextLoader.load("sesRulesService", SesRuleService.class);
    
    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
    }
    
    public synchronized SesClientResponse subscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        return service.subscribe(sessionInfo, uuid, medium, eml);
    }

    public synchronized SesClientResponse unSubscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        return service.unSubscribe(sessionInfo, uuid, medium, eml);
    }

    public synchronized SesClientResponse createBasicRule(SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName) throws Exception {
        return service.createBasicRule(sessionInfo, rule, edit, oldRuleName);
    }

    public synchronized SesClientResponse getAllOwnRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        return service.getAllOwnRules(sessionInfo, edit);
    }

    public synchronized SesClientResponse getAllOtherRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        return service.getAllOtherRules(sessionInfo, edit);
    }

    public synchronized SesClientResponse publishRule(SessionInfo sessionInfo, String ruleName, boolean value) throws Exception {
        return service.publishRule(sessionInfo, ruleName, value);
    }

    public synchronized SesClientResponse getAllRules(SessionInfo sessionInfo) throws Exception {
        return service.getAllRules(sessionInfo);
    }

    public synchronized SesClientResponse deleteRule(SessionInfo sessionInfo, String uuid) throws Exception {
        return service.deleteRule(sessionInfo, uuid);
    }

    public synchronized SesClientResponse getAllPublishedRules(SessionInfo sessionInfo, int operator) throws Exception {
        return service.getAllPublishedRules(sessionInfo, operator);
    }

    public synchronized SesClientResponse getRuleForEditing(String ruleName) throws Exception {
        return service.getRuleForEditing(ruleName);
    }

    public synchronized SesClientResponse ruleNameExists(String ruleName) throws Exception {
        return service.ruleNameExists(ruleName);
    }

    public synchronized SesClientResponse createComplexRule(SessionInfo sessionInfo, ComplexRuleData rule, boolean edit, String oldName) throws Exception {
        return service.createComplexRule(sessionInfo, rule, edit, oldName);
    }
    public synchronized SesClientResponse getUserSubscriptions(SessionInfo sessionInfo) throws Exception {
        return service.getUserSubscriptions(sessionInfo);
    }

    public SesClientResponse search(String text, int criterion, String userID) throws Exception {
        return service.search(text, criterion, userID);
    }

    public SesClientResponse copy(String userID, String ruleName) throws Exception {
        return service.copy(userID, ruleName);
    }
}