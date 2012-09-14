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
package org.n52.client.eventBus.events;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.handler.DatesChangedEventHandler;

/**
 * The Class DatesChangedEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 * 
 *         ONLY for the TimeManager!
 */
public class DatesChangedEvent extends FilteredDispatchGwtEvent<DatesChangedEventHandler> {

    /** The start. */
    private long start;

    /** The end. */
    private long end;

    /** The silent. */
    private boolean silent = false;

    /** The TYPE. */
    public static Type<DatesChangedEventHandler> TYPE = new Type<DatesChangedEventHandler>();

    /**
     * Instantiates a new dates changed event.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param blockedHandlers
     *            the blocked handlers
     */
    public DatesChangedEvent(long start, long end, DatesChangedEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.start = start;
        this.end = end;
    }

    /**
     * Instantiates a new dates changed event.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     * @param silent
     *            the silent
     * @param blockedHandlers
     *            the blocked handlers
     */
    public DatesChangedEvent(long start, long end, boolean silent,
            DatesChangedEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.start = start;
        this.end = end;
        this.silent = silent;
    }

    /**
     * Checks if is silent.
     * 
     * @return true, if is silent
     */
    public boolean isSilent() {
        return this.silent;
    }

    /**
     * Gets the start.
     * 
     * @return the start
     */
    public long getStart() {
        return this.start;
    }

    /**
     * Gets the end.
     * 
     * @return the end
     */
    public long getEnd() {
        return this.end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(DatesChangedEventHandler handler) {
        handler.onDatesChanged(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DatesChangedEventHandler> getAssociatedType() {
        return TYPE;
    }
}
