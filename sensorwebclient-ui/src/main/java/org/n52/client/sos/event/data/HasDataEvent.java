/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
import org.n52.client.sos.event.data.handler.HasDataEventHandler;

/**
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 *
 */
public class HasDataEvent extends FilteredDispatchGwtEvent<HasDataEventHandler> {

    public static Type<HasDataEventHandler> TYPE = new Type<HasDataEventHandler>();
    private boolean hasData; 
    private String id;
    
    /**
     * Instantiates a new checks for data event.
     *
     * @param hasData the has data
     * @param blockedHandlers the blocked handlers
     */
    public HasDataEvent(boolean hasData, HasDataEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.hasData = hasData;
        this.setId(id);
    }
    
    /**
     * Checks for data.
     *
     * @return true, if successful
     */
    public boolean hasData() {
        return this.hasData;
    }
    
    /* (non-Javadoc)
     * @see org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(HasDataEventHandler handler) {
        handler.onEvent(this);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<HasDataEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @param parameterId the parameterId to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the parameterId
     */
    public String getId() {
        return id;
    }

}
