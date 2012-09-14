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

public interface SesRuleService {

    public abstract SesClientResponse subscribe(String userID, String ruleName, String medium, String eml) throws Exception;

    public abstract SesClientResponse unSubscribe(String ruleName, String userID, String medium, String format) throws Exception;

    public abstract SesClientResponse createBasicRule(Rule rule, boolean edit, String oldRuleName) throws Exception;

    public abstract SesClientResponse getAllOwnRules(String id, boolean edit) throws Exception;

    public abstract SesClientResponse getAllOtherRules(String id, boolean edit) throws Exception;

    public abstract SesClientResponse publishRule(String ruleName, boolean value, String role) throws Exception;

    public abstract SesClientResponse getAllRules() throws Exception;

    public abstract SesClientResponse deleteRule(String ruleName) throws Exception;

    public abstract SesClientResponse getRuleForEditing(String ruleName) throws Exception;

    public abstract SesClientResponse getAllPublishedRules(String userID, int operator) throws Exception;

    public abstract SesClientResponse ruleNameExists(String ruleName) throws Exception;

    public abstract SesClientResponse createComplexRule(ComplexRuleData rule, boolean edit, String oldRuleName) throws Exception;

    public abstract SesClientResponse getUserSubscriptions(String userID) throws Exception;

    public abstract SesClientResponse search(String text, int criterion, String userID) throws Exception;

    public abstract SesClientResponse copy(String userID, String ruleName) throws Exception;

}