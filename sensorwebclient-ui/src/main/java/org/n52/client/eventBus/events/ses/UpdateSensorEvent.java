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
import org.n52.client.eventBus.events.ses.handler.UpdateSensorEventHandler;

/**
 * The Class UpdateSensorEvent.
 */
public class UpdateSensorEvent extends FilteredDispatchGwtEvent<UpdateSensorEventHandler> {

    /** The TYPE. */
    public static Type<UpdateSensorEventHandler> TYPE = new Type<UpdateSensorEventHandler>();

    /** The parameterId. */
    private String id;

    /** The status. */
    private boolean status;

    /**
     * Instantiates a new delete user event.
     * 
     * @param parameterId
     *            the parameterId
     * @param status
     * @param blockedHandlers
     *            the blocked handlers
     */
    public UpdateSensorEvent(String id, boolean status,
            UpdateSensorEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.id = id;
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(UpdateSensorEventHandler handler) {
        handler.onUpdateSensor(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateSensorEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the parameterId.
     * 
     * @return the parameterId
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return {@link Boolean}
     */
    public boolean isStatus() {
        return this.status;
    }
}