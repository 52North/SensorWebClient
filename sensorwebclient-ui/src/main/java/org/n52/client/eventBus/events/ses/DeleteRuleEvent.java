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
package org.n52.client.eventBus.events.ses;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.ses.handler.DeleteRuleEventHandler;

/**
 * The Class DeleteRuleEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class DeleteRuleEvent extends FilteredDispatchGwtEvent<DeleteRuleEventHandler> {

    /** The TYPE. */
    public static Type<DeleteRuleEventHandler> TYPE = new Type<DeleteRuleEventHandler>();

    /** The ruleName. */
    private String ruleName;
    
    /** The role of user who makes the call */
    private String role;

    /**
     * Instantiates a new delete rule event.
     * 
     * @param ruleName
     *            the rule name
     * @param blockedHandlers
     *            the blocked handlers
     */
    public DeleteRuleEvent(String ruleName, String role, DeleteRuleEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ruleName = ruleName;
        this.role = role;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(DeleteRuleEventHandler handler) {
        handler.onDeleteRule(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteRuleEventHandler> getAssociatedType() {
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

    /**
     * @return role
     */
    public String getRole() {
        return this.role;
    }
}
