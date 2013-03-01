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