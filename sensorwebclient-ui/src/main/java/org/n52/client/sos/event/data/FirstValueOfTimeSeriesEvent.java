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
import org.n52.client.sos.event.data.handler.StoreTimeSeriesFirstValueEventHandler;

/**
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 * 
 */
public class FirstValueOfTimeSeriesEvent extends
        FilteredDispatchGwtEvent<StoreTimeSeriesFirstValueEventHandler> {

    /**
     * 
     */
    public static Type<StoreTimeSeriesFirstValueEventHandler> TYPE =
            new Type<StoreTimeSeriesFirstValueEventHandler>();

    private final long date;

    private final String val;

    private final String tsID;

    /**
     * @param date
     * @param val
     * @param tsID
     * @param blockedHandlers
     */
    public FirstValueOfTimeSeriesEvent(long date, String val, String tsID,
            StoreTimeSeriesFirstValueEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.date = date;
        this.val = val;
        this.tsID = tsID;
    }

    /**
     * @return the tsID
     */
    public String getTsID() {
        return this.tsID;
    }

    /**
     * @return the date
     */
    public long getDate() {
        return this.date;
    }

    /**
     * @return the val
     */
    public String getVal() {
        return this.val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(StoreTimeSeriesFirstValueEventHandler handler) {
        handler.onStore(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StoreTimeSeriesFirstValueEventHandler> getAssociatedType() {
        return TYPE;
    }

}
