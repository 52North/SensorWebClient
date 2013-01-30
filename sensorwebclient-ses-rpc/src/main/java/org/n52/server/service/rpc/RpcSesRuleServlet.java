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
import org.n52.server.ses.service.SesRulesServiceImpl;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.service.rpc.RpcSesRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RpcSesRuleServlet extends RemoteServiceServlet implements RpcSesRuleService {

    private static final long serialVersionUID = 3219805776368229776L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesRuleServlet.class);
    
    private SesRuleService service;
    
    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
        service = new SesRulesServiceImpl();
    }
    
    public synchronized SesClientResponse subscribe(String userID, String ruleName, String medium, String eml) throws Exception {
        return service.subscribe(userID, ruleName, medium, eml);
    }

    // unsubscribe rule from SES
    public synchronized SesClientResponse unSubscribe(String ruleName, String userID, String medium, String eml) throws Exception {
        return service.unSubscribe(ruleName, userID, medium, eml);
    }

    // create basic rule from user inputs
    public synchronized SesClientResponse createBasicRule(Rule rule, boolean edit, String oldRuleName) throws Exception {
        return service.createBasicRule(rule, edit, oldRuleName);
    }

    // returns SesClientResponse with a list of all rules of the user
    public synchronized SesClientResponse getAllOwnRules(String id, boolean edit) throws Exception {
        return service.getAllOwnRules(id, edit);
    }

    // returns all published rules of other users
    public synchronized SesClientResponse getAllOtherRules(String id, boolean edit) throws Exception {
        return service.getAllOtherRules(id, edit);
    }

    public synchronized SesClientResponse publishRule(String ruleName, boolean value, String role) throws Exception {
        return service.publishRule(ruleName, value, role);
    }

    // returns all rules. Published and not published. This method is for admins only
    public synchronized SesClientResponse getAllRules() throws Exception {
        return service.getAllRules();
    }

    // delete rule. If a rule is still subscribed, the user gets a message
    public synchronized SesClientResponse deleteRule(String ruleName) throws Exception {
        return service.deleteRule(ruleName);
    }

    public synchronized SesClientResponse getAllPublishedRules(String userID, int operator) throws Exception {
        return service.getAllPublishedRules(userID, operator);
    }

    // loads all needed informations from EML file of a rule to fill all fields in the
    // edit rule view
    public synchronized SesClientResponse getRuleForEditing(String ruleName) throws Exception {
        return service.getRuleForEditing(ruleName);
    }

    // checks wether a rule name still exists. The client works with unique rule names
    public synchronized SesClientResponse ruleNameExists(String ruleName) throws Exception {
        return service.ruleNameExists(ruleName);
    }

    // creates a complex rule from user inputs
    public synchronized SesClientResponse createComplexRule(ComplexRuleData rule, boolean edit, String oldName) throws Exception {
        return service.createComplexRule(rule, edit, oldName);
    }
    // returns a list with all subscriptions of the given userID
    public synchronized SesClientResponse getUserSubscriptions(String userID) throws Exception {
        return service.getUserSubscriptions(userID);
    }

    public SesClientResponse search(String text, int criterion, String userID) throws Exception {
        return service.search(text, criterion, userID);
    }

    // copy a rule and set a new owner to the copy
    public SesClientResponse copy(String userID, String ruleName) throws Exception {
        return service.copy(userID, ruleName);
    }
}