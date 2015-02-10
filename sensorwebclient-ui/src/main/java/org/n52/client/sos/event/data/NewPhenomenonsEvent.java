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

import java.util.Set;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.NewPhenomenonsEventHandler;

public class NewPhenomenonsEvent extends FilteredDispatchGwtEvent<NewPhenomenonsEventHandler> {

    public static Type<NewPhenomenonsEventHandler> TYPE = new Type<NewPhenomenonsEventHandler>();

    private String sosURL;

    private String offID;

    private Set<String> phenomenaIds;

    public NewPhenomenonsEvent(String sosURL, String offID, Set<String> phenomena, NewPhenomenonsEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.sosURL = sosURL;
        this.offID = offID;
        this.phenomenaIds = phenomena;
    }

    public NewPhenomenonsEvent(String sosURL, Set<String> phenomena, NewPhenomenonsEventHandler... blockedHandlers) {
        this(sosURL, null, phenomena, blockedHandlers);
    }

    public String getSosURL() {
        return this.sosURL;
    }

    public String getOffID() {
        return this.offID;
    }

    @Override
    protected void onDispatch(NewPhenomenonsEventHandler handler) {
        handler.onNewPhenomenons(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<NewPhenomenonsEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Set<String> getPhenomenaIds() {
        return phenomenaIds;
    }

    public void setPhenomenaIds(Set<String> phenomenaIds) {
        this.phenomenaIds = phenomenaIds;
    }
}
