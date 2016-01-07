/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client.sos.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.DatesChangedEventHandler;

public class DatesChangedEvent extends FilteredDispatchGwtEvent<DatesChangedEventHandler> {

    private long start;

    private long end;

    private boolean silent = false;

    public static Type<DatesChangedEventHandler> TYPE = new Type<DatesChangedEventHandler>();

    public DatesChangedEvent(long start, long end, DatesChangedEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.start = start;
        this.end = end;
    }

    public DatesChangedEvent(long start, long end, boolean silent, DatesChangedEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.start = start;
        this.end = end;
        this.silent = silent;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    @Override
    protected void onDispatch(DatesChangedEventHandler handler) {
        handler.onDatesChanged(this);
    }

    @Override
    public Type<DatesChangedEventHandler> getAssociatedType() {
        return TYPE;
    }
}
