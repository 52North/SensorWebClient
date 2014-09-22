/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import org.n52.client.sos.event.data.handler.PropagateOfferingFullEventHandler;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

/**
 * The Class StoreOfferingsFullEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class PropagateOfferingsFullEvent extends
        FilteredDispatchGwtEvent<PropagateOfferingFullEventHandler> {

    /** The TYPE. */
    public static Type<PropagateOfferingFullEventHandler> TYPE =
            new Type<PropagateOfferingFullEventHandler>();

    private final SOSMetadata meta;

    /**
     * Instantiates a new store offerings full event.
     * 
     * @param meta
     * 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public PropagateOfferingsFullEvent(SOSMetadata meta,
            PropagateOfferingFullEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.meta = meta;
    }

    /**
     * @return SOSMetadata
     */
    public SOSMetadata getMetadata() {
        return this.meta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(PropagateOfferingFullEventHandler handler) {
        handler.onNewFullOfferings(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PropagateOfferingFullEventHandler> getAssociatedType() {
        return TYPE;
    }

}
