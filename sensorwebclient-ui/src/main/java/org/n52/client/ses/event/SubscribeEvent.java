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
package org.n52.client.ses.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.ses.event.handler.SubscribeEventHandler;

/**
 * The Class SubscribeEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class SubscribeEvent extends FilteredDispatchGwtEvent<SubscribeEventHandler> {

    /** The TYPE. */
    public static Type<SubscribeEventHandler> TYPE = new Type<SubscribeEventHandler>();

    /** The userID. */
    private String userID;
    
    /** The rule name. */
    private String ruleName;

    /** The medium. */
    private String medium;
    
    /** The format. */
    private String format;

    /**
     * Instantiates a new subscribe event.
     * @param userID 
     * 
     * @param ruleName
     *            the rule name
     * @param medium 
     * @param format 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public SubscribeEvent(String userID, String ruleName, String medium, String format, SubscribeEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.userID = userID;
        this.ruleName = ruleName;
        this.medium = medium;
        this.format = format;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(SubscribeEventHandler handler) {
        handler.onSubscribe(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SubscribeEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the rule name.
     * 
     * @return the rule name
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * @return {@link String}
     */
    public String getMedium() {
        return this.medium;
    }

    /**
     * @return {@link String}
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * @return {@link String}
     */
    public String getFormat() {
        return this.format;
    }
}