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
package org.n52.client.eventBus.events.dataEvents.sos;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.dataEvents.sos.handler.RequestDataEventHandler;

/**
 * The Class RequestDataEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class RequestDataEvent extends FilteredDispatchGwtEvent<RequestDataEventHandler> {

    /** The TYPE. */
    public static Type<RequestDataEventHandler> TYPE = new Type<RequestDataEventHandler>();

    /** The ID. */
    private String ID = null;

    /**
     * Instantiates a new request data event.
     * 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public RequestDataEvent(RequestDataEventHandler... blockedHandlers) {
        super(blockedHandlers);
    }

    /**
     * Instantiates a new request data event.
     * 
     * @param tsID
     *            the ts parameterId
     * @param blockedHandlers
     *            the blocked handlers
     */
    public RequestDataEvent(String tsID, RequestDataEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ID = tsID;
    }

    /**
     * Gets the iD.
     * 
     * @return the iD
     */
    public String getID() {
        return this.ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(RequestDataEventHandler handler) {
        handler.onRequestData(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<RequestDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
