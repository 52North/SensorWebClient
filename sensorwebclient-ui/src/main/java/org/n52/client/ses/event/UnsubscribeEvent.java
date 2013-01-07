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
import org.n52.client.ses.event.handler.UnsubscribeEventHandler;

/**
 * The Class SubscribeEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class UnsubscribeEvent extends FilteredDispatchGwtEvent<UnsubscribeEventHandler> {

    /** The TYPE. */
    public static Type<UnsubscribeEventHandler> TYPE = new Type<UnsubscribeEventHandler>();

    /** The rule name. */
    private String ruleName;
    
    /** The userID. */
    private String userID;
    
    /** The medium. */
    private String medium;
    
    /** The format. */
    private String format;


    /**
     * Instantiates a new subscribe event.
     * 
     * @param ruleName
     *            the rule name
     * @param userID 
     * @param medium 
     * @param format 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public UnsubscribeEvent(String ruleName, String userID, String medium, String format, UnsubscribeEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ruleName = ruleName;
        this.userID = userID;
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
    protected void onDispatch(UnsubscribeEventHandler handler) {
        handler.onUnsubscribe(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UnsubscribeEventHandler> getAssociatedType() {
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
     * @return userID
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * 
     * @return medium
     */
    public String getMedium() {
        return this.medium;
    }

    /**
     * @return format
     */
    public String getFormat() {
        return this.format;
    }
}