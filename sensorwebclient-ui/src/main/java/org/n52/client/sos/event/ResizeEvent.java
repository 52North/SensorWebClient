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
