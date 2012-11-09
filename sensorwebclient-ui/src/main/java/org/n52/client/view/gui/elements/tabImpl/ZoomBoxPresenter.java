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
package org.n52.client.view.gui.elements.tabImpl;
/*
 * Copyright 2010 EES GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.eesgmbh.gimv.client.controls.DragImageControl;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.StateChangeEventHandler;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEventHandler;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEventHandler;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.n52.client.control.ClientUtils;
import org.n52.client.eventBus.events.dataEvents.sos.SwitchAutoscaleEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * This presenter enables to zoom the image by mouse dragging on the viewport,
 * thus creating some kind of bounding box.
 * 
 * <p>
 * The image will be zoomed to the selection made with the bounding box.<br>
 * Currently only zoom in is supported.
 * 
 * <p>
 * The passed in view is the actual visual representation of the bounding box.
 * 
 * <p>
 * This presenter must be activated or deactivated with a
 * {@link StateChangeEvent}. The reason behind the mandatory activation is, that
 * dragging can both mean zooming or moving the image. Only this presenter or
 * the {@link DragImageControl} can be active at the same time.
 * 
 * <p>
 * Registers with the {@link HandlerManager} to recieve the following events
 * <ul>
 * <li> {@link ViewportDragInProgressEvent} (the presenter changes the position
 * and dimension of the view accordingly)
 * <li> {@link ViewportDragFinishedEvent} (will trigger a
 * {@link SetDomainBoundsEvent} and a {@link LoadImageDataEvent} )
 * <li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * <li> {@link StateChangeEvent} (must be set to move for the control to do
 * something)
 * <li> {@link SetMaxDomainBoundsEvent} (optional, if not recieved, there will be
 * no restriction)
 * </ul>
 * 
 * <p>
 * Fires the following events
 * <ul>
 * <li> {@link SetDomainBoundsEvent} fired after recieving
 * {@link ViewportDragFinishedEvent} with the new domain bounds
 * <li> {@link LoadImageDataEvent} fired after recieving
 * {@link ViewportDragFinishedEvent}
 * </ul>
 * 
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class ZoomBoxPresenter {

    private final HandlerManager handlerManager;

    private final GenericWidgetView view;

    private boolean fireLoadImageDataEvent;

    private int minimalDragOffsetInPixel;

    private boolean active;

    private Bounds currentDomainBounds;

    private SetMaxDomainBoundsEvent currentMaxDomainBounds;

    /**
     * Instantiates the presenter.
     * 
     * @param handlerManager
     *            A {@link HandlerManager}
     * @param view
     *            An implementation of {@link GenericWidgetView}. This is the
     *            visual representation of the bounding box.
     */
    public ZoomBoxPresenter(HandlerManager handlerManager, GenericWidgetView view) {
        this.handlerManager = handlerManager;
        this.view = view;

        ZoomBoxPresenterEventHandler eventHandler = new ZoomBoxPresenterEventHandler();
        handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
        handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(StateChangeEvent.TYPE, eventHandler);

        setMinimalDragOffsetInPixel(15);
        setFireLoadImageDataEvent(true);
    }

    /**
     * Specifies the minimal width and height of the zoom area. If the dragging
     * width or height is below that, no {@link SetDomainBoundsEvent} or
     * {@link LoadImageDataEvent} will be fired. The presenter simply has no
     * effect in this case.
     * 
     * <p>
     * This is to ensure, that the image does not get rendered with extremly
     * small domain bounds, just because the user accidently clicked on the
     * viewport.
     * 
     * <p>
     * The default value is 15 pixels.
     * 
     * @param minimalDragOffsetInPixel
     */
    public void setMinimalDragOffsetInPixel(int minimalDragOffsetInPixel) {
        this.minimalDragOffsetInPixel = minimalDragOffsetInPixel;
    }

    /**
     * <p>
     * Defines, whether a {@link LoadImageDataEvent} is fired.
     * 
     * <p>
     * Default is true.
     * 
     * @param fireLoadImageDataEvent
     *            fire it, or not
     */
    public void setFireLoadImageDataEvent(boolean fireLoadImageDataEvent) {
        this.fireLoadImageDataEvent = fireLoadImageDataEvent;
    }

    protected void onDragInProgress(ViewportDragInProgressEvent event) {
        if (this.active) {
            Bounds bounds = event.getPixelBounds().normalizeBounds();

            this.view.setRelX(bounds.getLeft().intValue());
            this.view.setRelY(bounds.getTop().intValue());
            this.view.setWidth(bounds.getWidth().intValue());
            this.view.setHeight(bounds.getHeight().intValue());

            this.view.show();
        }
    }

    protected void onDragFinished(ViewportDragFinishedEvent event) {
        if (this.active) {
            this.view.hide();

            if (this.currentDomainBounds != null) {
                if (dragNotAccidental(event)) {
                    Bounds proportionalBounds = event.getProportionalBounds().normalizeBounds();

                    Bounds newBounds = this.currentDomainBounds.transformProportional(proportionalBounds);

                    if (this.currentMaxDomainBounds != null) {
                        if (!this.currentMaxDomainBounds.containsHorizontally(newBounds.getLeft(), newBounds
                                .getRight())) {
                            newBounds = newBounds.setLeft(this.currentDomainBounds.getLeft());
                            newBounds = newBounds.setRight(this.currentDomainBounds.getRight());
                        }

                        if (!this.currentMaxDomainBounds.containsVertically(newBounds.getTop(), newBounds.getBottom())) {
                            newBounds = newBounds.setTop(this.currentDomainBounds.getTop());
                            newBounds = newBounds.setBottom(this.currentDomainBounds.getBottom());
                        }
                    }
                    
                    // WORKAROUND to have a maximum zoom level in the time frame Bug 508
                    long begin = newBounds.getLeft().longValue();
                    long end = newBounds.getRight().longValue();
                    if (ClientUtils.isValidTimeFrameForZoomIn(begin, end)) {
                        this.handlerManager.fireEvent(new SwitchAutoscaleEvent(false));
                        this.handlerManager.fireEvent(new SetDomainBoundsEvent(newBounds));
                        if (this.fireLoadImageDataEvent) {
                            this.handlerManager.fireEvent(new LoadImageDataEvent());
                        }
                    }
                }
            }
        }
    }

    protected void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
        this.currentMaxDomainBounds = event;
    }

    protected void onSetDomainBounds(SetDomainBoundsEvent event) {
        this.currentDomainBounds = event.getBounds();
    }

    protected void onStateChange(StateChangeEvent event) {
        this.active = event.isZoom();
    }

    private boolean dragNotAccidental(ViewportDragFinishedEvent event) {
        return event.getRelativePixelBounds().getAbsWidth() > this.minimalDragOffsetInPixel
                && event.getRelativePixelBounds().getAbsHeight() > this.minimalDragOffsetInPixel;
    }

    protected class ZoomBoxPresenterEventHandler implements ViewportDragInProgressEventHandler,
            ViewportDragFinishedEventHandler, SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler,
            StateChangeEventHandler {
        public void onDragInProgress(ViewportDragInProgressEvent event) {
            ZoomBoxPresenter.this.onDragInProgress(event);
        }

        public void onDragFinished(ViewportDragFinishedEvent event) {
            ZoomBoxPresenter.this.onDragFinished(event);
        }

        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            ZoomBoxPresenter.this.onSetDomainBounds(event);
        }

        public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
            ZoomBoxPresenter.this.onSetMaxDomainBounds(event);
        }

        public void onStateChange(StateChangeEvent event) {
            ZoomBoxPresenter.this.onStateChange(event);
        }
    }
}
