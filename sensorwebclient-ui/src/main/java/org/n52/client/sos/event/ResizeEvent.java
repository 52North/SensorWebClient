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
package org.n52.client.sos.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.ResizeEventHandler;

/**
 * The Class ResizeEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class ResizeEvent extends FilteredDispatchGwtEvent<ResizeEventHandler> {

    /** The TYPE. */
    public static Type<ResizeEventHandler> TYPE = new Type<ResizeEventHandler>();

    /** The w. */
    private int w = 0;

    /** The h. */
    private int h = 0;

    /** The silent. */
    private boolean silent = false;

    /**
     * Instantiates a new resize event.
     * 
     * @param w
     *            the w
     * @param h
     *            the h
     * @param blockedHandlers
     *            the blocked handlers
     */
    public ResizeEvent(int w, int h, ResizeEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.w = w;
        this.h = h;
    }

    /**
     * Instantiates a new resize event.
     * 
     * @param w
     *            the w
     * @param h
     *            the h
     * @param silent
     *            the silent
     * @param blockedHandlers
     *            the blocked handlers
     */
    public ResizeEvent(int w, int h, boolean silent, ResizeEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.w = w;
        this.h = h;
        this.silent = silent;
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public int getWidth() {
        return this.w;
    }

    /**
     * Gets the height.
     * 
     * @return the height
     */
    public int getHeight() {
        return this.h;
    }

    /**
     * Checks if is silent.
     * 
     * @return true, if is silent
     */
    public boolean isSilent() {
        return this.silent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(ResizeEventHandler handler) {
        handler.onResize(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

}
