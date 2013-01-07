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
package org.n52.client.sos.event.data;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.StoreAxisDataEventHandler;
import org.n52.shared.serializable.pojos.Axis;

/**
 * The Class StoreAxisDataEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class StoreAxisDataEvent extends FilteredDispatchGwtEvent<StoreAxisDataEventHandler> {

    /** The TYPE. */
    public static Type<StoreAxisDataEventHandler> TYPE = new Type<StoreAxisDataEventHandler>();

    /** The axis. */
    private Axis axis;

    /** The ts parameterId. */
    private String tsID;

    /**
     * Instantiates a new store axis data event.
     * 
     * @param tsID
     *            the ts parameterId
     * @param axis
     *            the axis
     */
    public StoreAxisDataEvent(String tsID, Axis axis) {
        this.tsID = tsID;
        this.axis = axis;
    }

    /**
     * Gets the axis.
     * 
     * @return the axis
     */
    public Axis getAxis() {
        return this.axis;
    }

    /**
     * Gets the ts parameterId.
     * 
     * @return the ts parameterId
     */
    public String getTsID() {
        return this.tsID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(StoreAxisDataEventHandler handler) {
        handler.onStore(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StoreAxisDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
