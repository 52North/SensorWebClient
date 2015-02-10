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
package org.n52.client.ses.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.ses.event.handler.RuleNameExistsEventHandler;

/**
 * The Class DeleteRuleEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class RuleNameExistsEvent extends FilteredDispatchGwtEvent<RuleNameExistsEventHandler> {

    /** The TYPE. */
    public static Type<RuleNameExistsEventHandler> TYPE = new Type<RuleNameExistsEventHandler>();

    /** The ruleName. */
    private String ruleName;

    /**
     * Instantiates a new delete rule event.
     * 
     * @param ruleName
     *            the rule name
     * @param blockedHandlers
     *            the blocked handlers
     */
    public RuleNameExistsEvent(String ruleName, RuleNameExistsEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ruleName = ruleName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(RuleNameExistsEventHandler handler) {
        handler.onExists(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RuleNameExistsEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the ruleName.
     * 
     * @return the ruleName
     */
    public String getRuleName() {
        return this.ruleName;
    }
}
