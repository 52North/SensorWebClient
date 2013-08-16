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

import java.util.HashMap;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesDataEventHandler;

public class StoreTimeSeriesDataEvent extends
        FilteredDispatchGwtEvent<StoreTimeSeriesDataEventHandler> {

    public static Type<StoreTimeSeriesDataEventHandler> TYPE =
            new Type<StoreTimeSeriesDataEventHandler>();

    private HashMap<String, HashMap<Long, Double>> data;

    public StoreTimeSeriesDataEvent(HashMap<String, HashMap<Long, Double>> data,
            StoreTimeSeriesDataEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.data = data;

    }

    public HashMap<String, HashMap<Long, Double>> getData() {
        return this.data;
    }

    @Override
    protected void onDispatch(StoreTimeSeriesDataEventHandler handler) {
        handler.onStore(this);
    }

    @Override
    public Type<StoreTimeSeriesDataEventHandler> getAssociatedType() {
        return TYPE;
    }

}
