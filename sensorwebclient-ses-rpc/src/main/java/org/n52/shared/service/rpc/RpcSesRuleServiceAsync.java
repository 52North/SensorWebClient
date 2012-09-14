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
/**********************************************************************************
 Copyright (C) 2010
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk 
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under the
 terms of the GNU General Public License version 2 as published by the Free
 Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with this 
 program (see gnu-gplv2.txt). If not, write to the Free Software Foundation, Inc., 
 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or visit the Free Software
 Foundation web page, http://www.fsf.org.

 Created on: 27.07.2010
 *********************************************************************************/
package org.n52.shared.service.rpc;

import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface RpcSesRuleServiceAsync.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public interface RpcSesRuleServiceAsync {

    /**
     * Subscribe.
     * @param userID 
     * 
     * @param ruleName
     *            the rule name
     * @param medium 
     * @param eml 
     * @param callback
     *            the callback
     */
    void subscribe(String userID, String ruleName, String medium, String eml, AsyncCallback<SesClientResponse> callback);

    /**
     * Un subscribe.
     * 
     * @param ruleName
     *            the rule name
     * @param userID 
     * @param medium 
     * @param format 
     * @param callback
     *            the callback
     */
    void unSubscribe(String ruleName, String userID, String medium, String format, AsyncCallback<SesClientResponse> callback);

    /**
     * Creates the basic rule.
     * 
     * @param rule
     *            the rule
     * @param edit 
     * @param oldRuleName 
     * @param callback
     *            the callback
     */
    void createBasicRule(Rule rule, boolean edit, String oldRuleName, AsyncCallback<SesClientResponse> callback);

    /**
     * @param parameterId
     * @param edit
     * @param callback
     */
    void getAllOwnRules(String id, boolean edit,  AsyncCallback<SesClientResponse> callback);

    /**
     * @param parameterId
     * @param edit 
     * @param callback
     */
    void getAllOtherRules(String id, boolean edit, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param ruleName
     * @param value 
     * @param role 
     * @param callback
     */
    void publishRule(String ruleName, boolean value, String role, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param callback
     */
    void getAllRules(AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param ruleName
     * @param callback
     */
    void deleteRule(String ruleName, AsyncCallback<SesClientResponse> callback);

    /**
     * @param ruleName
     * @param callback
     */
    void getRuleForEditing(String ruleName, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param userID 
     * @param callback
     */
    void getAllPublishedRules(String userID, int operator, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param ruleName
     * @param callback
     */
    void ruleNameExists(String ruleName, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param rule 
     * @param edit 
     * @param oldRuleName 
     * @param callback
     */
    void createComplexRule(ComplexRuleData rule, boolean edit, String oldRuleName, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param userID
     * @param callback
     */
    void getUserSubscriptions(String userID, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param text
     * @param criterion
     * @param userID 
     * @param callback
     */
    void search(String text, int criterion, String userID, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param userID
     * @param ruleName
     * @param callback
     */
    void copy(String userID, String ruleName, AsyncCallback<SesClientResponse> callback);
}