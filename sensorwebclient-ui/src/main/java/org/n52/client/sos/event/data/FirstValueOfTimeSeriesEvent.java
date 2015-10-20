/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
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
