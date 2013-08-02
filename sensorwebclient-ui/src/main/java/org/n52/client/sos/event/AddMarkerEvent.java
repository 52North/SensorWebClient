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
package org.n52.client.sos.event;

import java.util.List;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.AddMarkerEventHandler;
import org.n52.shared.serializable.pojos.sos.Station;

/**
 * The Class AddMarkerEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class AddMarkerEvent extends FilteredDispatchGwtEvent<AddMarkerEventHandler> {

    /** The TYPE. */
    public static Type<AddMarkerEventHandler> TYPE = new Type<AddMarkerEventHandler>();

    private List<Station> stations;

    /**
     * Instantiates a new adds the marker event.
     * 
     * @param p
     *            the p
     */
    public AddMarkerEvent(List<Station> stations) {
        this.stations = stations;
    }

    /**
     * Gets the procedure.
     * 
     * @return the procedure
     */
    public List<Station> getStations() {
        return this.stations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(AddMarkerEventHandler handler) {
        handler.onAddMarker(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AddMarkerEventHandler> getAssociatedType() {
        return TYPE;
    }

}
