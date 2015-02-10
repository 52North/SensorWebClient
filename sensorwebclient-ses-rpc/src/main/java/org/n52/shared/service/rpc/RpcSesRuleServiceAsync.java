/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
import org.n52.shared.session.SessionInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RpcSesRuleServiceAsync {

    void subscribe(SessionInfo sessionInfo, String uuid, String medium, String eml, AsyncCallback<SesClientResponse> callback);

    void unSubscribe(SessionInfo sessionInfo, String uuid, String medium, String format, AsyncCallback<SesClientResponse> callback);

    void createBasicRule(SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName, AsyncCallback<SesClientResponse> callback);

    void getAllOwnRules(SessionInfo sessionInfo, boolean edit,  AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void getAllOtherRules(SessionInfo sessionInfo, boolean edit, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void publishRule(SessionInfo sessionInfo, String ruleName, boolean published, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void getAllRules(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    void deleteRule(SessionInfo sessionInfo, String uuid, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void getRuleForEditing(String ruleName, AsyncCallback<SesClientResponse> callback);

    void getAllPublishedRules(SessionInfo sessionInfo, int operator, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void ruleNameExists(String ruleName, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void createComplexRule(SessionInfo sessionInfo, ComplexRuleData rule, boolean edit, String oldRuleName, AsyncCallback<SesClientResponse> callback);

    void getUserSubscriptions(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    void search(String text, int criterion, String userID, AsyncCallback<SesClientResponse> callback);

    @Deprecated
    void copy(String userID, String ruleName, AsyncCallback<SesClientResponse> callback);
}
