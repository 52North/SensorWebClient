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
import org.n52.client.eventBus.events.ses.handler.PublishRuleEventHandler;

/**
 * The Class PublishRuleEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class PublishRuleEvent extends FilteredDispatchGwtEvent<PublishRuleEventHandler> {

    /** The TYPE. */
    public static Type<PublishRuleEventHandler> TYPE = new Type<PublishRuleEventHandler>();

    /** The ruleName. */
    private String ruleName;

    /** The value. */
    private boolean value;
    
    /** The role */
    private String role;

    /**
     * Instantiates a new publish rule event.
     * 
     * @param ruleName 
     * @param value 
     * @param role 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public PublishRuleEvent(String ruleName, boolean value, String role, PublishRuleEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ruleName = ruleName;
        this.value = value;
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
    protected void onDispatch(PublishRuleEventHandler handler) {
        handler.onPublish(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PublishRuleEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return {@link String}
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * @return {@link Boolean}
     */
    public boolean isValue() {
        return this.value;
    }

    /**
     * @return {@link String}
     */
    public String getRole() {
        return this.role;
    }
}