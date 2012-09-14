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
import org.n52.client.eventBus.events.ses.handler.CreateComplexRuleEventHandler;
import org.n52.shared.serializable.pojos.ComplexRuleData;

/**
 * The Class CreateSimpleRuleEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class CreateComplexRuleEvent extends FilteredDispatchGwtEvent<CreateComplexRuleEventHandler> {

    /** The TYPE. */
    public static Type<CreateComplexRuleEventHandler> TYPE = new Type<CreateComplexRuleEventHandler>();

    /** The rule. */
    private ComplexRuleData rule;
    
    private boolean edit;
    
    private String oldName;

    /**
     * Instantiates a new creates the complex rule event.
     * 
     * @param rule
     *            the rule
     * @param edit 
     * @param oldName 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public CreateComplexRuleEvent(ComplexRuleData rule, boolean edit, String oldName, CreateComplexRuleEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.rule = rule;
        this.edit = edit;
        this.oldName = oldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(CreateComplexRuleEventHandler handler) {
        handler.onCreate(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateComplexRuleEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the rule.
     * 
     * @return the rule
     */
    public ComplexRuleData getRule() {
        return this.rule;
    }

    /**
     * @return is edit
     */
    public boolean isEdit() {
        return this.edit;
    }

    /**
     * @return old name
     */
    public String getOldName() {
        return this.oldName;
    }
}