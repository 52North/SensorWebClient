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
package org.n52.client.eventBus.events.sos;

import java.util.List;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.sos.handler.StoreStationsEventHandler;
import org.n52.shared.serializable.pojos.sos.Station;

/**
 * The Class StoreProcedurePositionsEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class StoreStationsEvent extends
        FilteredDispatchGwtEvent<StoreStationsEventHandler> {

    /** The TYPE. */
    public static Type<StoreStationsEventHandler> TYPE =
            new Type<StoreStationsEventHandler>();

    /** The sos url. */
    private final String sosURL;

    /** The procedures. */
    private final List<Station> stations;

    /** The srs. */
    private final String srs;

    /**
     * Instantiates a new store procedure positions event.
     * 
     * @param sosURL
     *            the sos url
     * @param procedures
     *            the procedures
     * @param srs
     *            the srs
     * @param blockedHandlers
     *            the blocked handlers
     */
    public StoreStationsEvent(String sosURL, List<Station> stations, String srs,
            StoreStationsEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.sosURL = sosURL;
        this.stations = stations;
        this.srs = srs;
    }

    /**
     * Gets the srs.
     * 
     * @return the srs
     */
    public String getSrs() {
        return this.srs;
    }

    /**
     * Gets the sos url.
     * 
     * @return the sos url
     */
    public String getSosURL() {
        return this.sosURL;
    }

    /**
     * Gets the procedures.
     * 
     * @return the procedures
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
    protected void onDispatch(StoreStationsEventHandler handler) {
        handler.onStore(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StoreStationsEventHandler> getAssociatedType() {
        return TYPE;
    }

}
