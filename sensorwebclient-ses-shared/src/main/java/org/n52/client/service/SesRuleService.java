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
package org.n52.client.service;

import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.session.SessionInfo;

public interface SesRuleService {

    public SesClientResponse subscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception;

    public SesClientResponse unSubscribe(SessionInfo sessionInfo, String uuid, String medium, String format) throws Exception;

    public SesClientResponse createBasicRule(SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName) throws Exception;

    public SesClientResponse getAllOwnRules(SessionInfo sessionInfo, boolean edit) throws Exception;

    public SesClientResponse getAllOtherRules(SessionInfo sessioninfo, boolean edit) throws Exception;

    public SesClientResponse publishRule(SessionInfo sessioninfo, String ruleName, boolean published) throws Exception;

    public SesClientResponse getAllRules(SessionInfo sessioninfo) throws Exception;

    public SesClientResponse deleteRule(SessionInfo sessioninfo, String uuid) throws Exception;

    @Deprecated
    public SesClientResponse getRuleForEditing(String ruleName) throws Exception;

    public SesClientResponse getAllPublishedRules(SessionInfo sessioninfo, int operator) throws Exception;

    @Deprecated
    public SesClientResponse ruleNameExists(String ruleName) throws Exception;

    @Deprecated
    public SesClientResponse createComplexRule(SessionInfo sessioninfo, ComplexRuleData rule, boolean edit, String oldRuleName) throws Exception;

    public SesClientResponse getUserSubscriptions(SessionInfo sessioninfo) throws Exception;

    public SesClientResponse search(String text, int criterion, String userID) throws Exception;

    @Deprecated
    public SesClientResponse copy(String userID, String ruleName) throws Exception;

}