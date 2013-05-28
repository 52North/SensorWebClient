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
import org.n52.client.sos.event.data.handler.StoreTimeSeriesPropsEventHandler;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

/**
 * The Class StoreTimeSeriesPropsEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class StoreTimeSeriesPropsEvent extends
        FilteredDispatchGwtEvent<StoreTimeSeriesPropsEventHandler> {

    /** The TYPE. */
    public static Type<StoreTimeSeriesPropsEventHandler> TYPE =
            new Type<StoreTimeSeriesPropsEventHandler>();

    /** The i d. */
    private String iD;

    /** The props. */
    private TimeseriesProperties props;

    public StoreTimeSeriesPropsEvent(String ID, TimeseriesProperties props,
            StoreTimeSeriesPropsEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.iD = ID;
        this.props = props;

    }

    public String getId() {
        return this.iD;
    }

    public TimeseriesProperties getProps() {
        return this.props;
    }

    @Override
    protected void onDispatch(StoreTimeSeriesPropsEventHandler handler) {
        handler.onStore(this);
    }


    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StoreTimeSeriesPropsEventHandler> getAssociatedType() {
        return TYPE;
    }

}
