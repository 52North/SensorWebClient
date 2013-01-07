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
import org.n52.client.sos.event.data.handler.StoreTimeSeriesEventHandler;
import org.n52.client.sos.legend.TimeSeries;

/**
 * The Class StoreTimeSeriesEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class StoreTimeSeriesEvent extends
        FilteredDispatchGwtEvent<StoreTimeSeriesEventHandler> {

    /** The TYPE. */
    public static Type<StoreTimeSeriesEventHandler> TYPE =
            new Type<StoreTimeSeriesEventHandler>();

    /** The ts. */
    private TimeSeries ts;

    /**
     * Instantiates a new store time series event.
     * 
     * @param ts
     *            the ts
     * @param blockedHandlers
     *            the blocked handlers
     */
    public StoreTimeSeriesEvent(TimeSeries ts, StoreTimeSeriesEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ts = ts;

    }

    /**
     * Gets the time series.
     * 
     * @return the time series
     */
    public TimeSeries getTimeSeries() {
        return this.ts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(StoreTimeSeriesEventHandler handler) {
        handler.onStore(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StoreTimeSeriesEventHandler> getAssociatedType() {
        return TYPE;
    }

}
