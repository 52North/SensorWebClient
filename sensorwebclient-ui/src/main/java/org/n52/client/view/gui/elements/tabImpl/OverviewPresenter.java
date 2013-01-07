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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEventHandler;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEventHandler;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;
import org.n52.client.control.ClientUtils;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * This presenter can be used to display and manipulate the currently shown
 * bounds of the main image within larger bounds.
 * 
 * <p>
 * E.g. it can show the currently displayed map section within a larger map as a
 * rectangle. The rectangle can be moved as a whole or only a single bound can
 * be changed.
 * 
 * <p>
 * The overview presenter must be instantiated with a different handler manager
 * than the one used to process events for the main image. This handler manager
 * will connect the presenter with its own {@link Viewport} to receive
 * notifications of drag operations.
 * 
 * <p>
 * If the user changes the overview with this presenter, the resulting change
 * will be propagated to all registered dependant handler managers with a
 * {@link SetDomainBoundsEvent}. If you got a single main image with one
 * overview, the only dependant handler manager is the one of the main image.
 * 
 * <p>
 * After instantiating the presenter, it must be configured with handles. These
 * handles represent the visual representation of the overview and are
 * configured to alter a certain set of bounds (see
 * {@link #addHandle(GenericWidgetView, Bound...)}.
 * 
 * <p>
 * Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * <li> {@link ViewportDragInProgressEvent} (mandatory, received from the
 * overview's viewport)
 * <li> {@link ViewportDragFinishedEvent} (mandatory, received from the
 * overview's viewport, will fire a {@link SetDomainBoundsEvent} and
 * {@link LoadImageDataEvent} on all dependant handler managers afterwards)
 * <li> {@link SetDomainBoundsEvent} (mandatory, the total domain bounds of the
 * overview)
 * <li> {@link SetOverviewDomainBoundsEvent} (mandatory, the actual portion of
 * the total bounds highlighted by the presenter)
 * <li> {@link SetViewportPixelBoundsEvent} (mandatory, the dimensions of the
 * overview's viewport)
 * <li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be
 * no restriction)
 * </ul>
 * 
 * <p>
 * Fires the following events on all dependant handler managers
 * <ul>
 * <li> {@link SetDomainBoundsEvent} (the new domain bounds after a drag
 * finished)
 * <li> {@link LoadImageDataEvent} (after a drag finished)
 * </ul>
 * 
 * @since 0.1.3
 * @author Sascha Hagedorn - EES GmbH - s.hagedorn@ees-gmbh.de
 */
public class OverviewPresenter {

    /**
     * Contains a list of {@link HandlerManager} instances who will receive a
     * {@link SetDomainBoundsEvent} and (optionally) a
     * {@link LoadImageDataEvent} after a drag on the overviewWidget finished.
     * 
     * The bounds are the current domain bounds of the overview (the portion
     * selected).
     */
    private final List<HandlerManager> dependantHandlerManagers = new ArrayList<HandlerManager>();

    /**
     * The widget of the overview
     */
    private GenericWidgetView overviewWidgetView;

    /**
     * The handles defining the dragable areas of the overview widget
     */
    private final List<Handle> handleWidgets = new ArrayList<OverviewPresenter.Handle>();

    /**
     * Handle which was used to initate the drag
     */
    private Handle initialDragHandle;

    /**
     * Pixel bounds of the overview relative to the {@link #viewport}
     */
    private Bounds currentPixelBounds;

    /**
     * Overall domain bounds
     */
    private Bounds currentDomainBounds;

    /**
     * Domain bounds of the overview. These domain bounds are represented by the
     * overview. This is a subset of the {@link currentDomainBounds} and defines
     * where the overview will be placed.
     */
    private Bounds overviewBounds;

    /**
     * The overall maxima domain bounds
     */
    private SetMaxDomainBoundsEvent currentMaxDomainBounds;

    /**
     * The bounds of the viewport
     */
    private Bounds currentViewportBounds;

    /**
     * The minimum width the overview widget can be resized to.<br />
     * <br />
     * <b>Default</b>: 0 pixels
     */
    private int minClippingWidth = 0;

    /**
     * The minimum height the overview widget can be resized to<br />
     * <br />
     * <b>Default</b>: 0 pixels
     */
    private int minClippingHeight = 0;

    /**
     * Configures the behaviour of the overview presenter regarding the aspect
     * ratio of the {@link #overviewWidgetView}. If set to <code>true</code> the
     * overview presenter keeps the overview at it's original aspect ratio.<br />
     * <br />
     * <b>Default</b>: false
     */
    private boolean preserveAspectRatio = false;

    /**
     * If set to true the overview widget's left and right bounds are locked. So
     * the left and right handles (if any) can't be moved. An overview widget
     * which is horizontally locked can only move vertically.
     */
    private boolean horizontallyLocked = false;

    /**
     * If set to true the overview widget's top and bottom bounds are locked. So
     * the top and bottom handles (if any) can't be moved. An overview widget
     * which is vertically locked can only move horizontally.
     */
    private boolean verticallyLocked = false;

    /**
     * Specifies the top offset of the overview widget<br />
     * <br />
     * <b>Default:</b> 0 pixels
     */
    private int overviewTopOffset = 0;

    /**
     * Specifies the left offset of the overview widget<br />
     * <br />
     * <b>Default:</b> 0 pixels
     */
    private int overviewLeftOffset = 0;

    private boolean fireLoadImageDataEvent;

    /*
     * public API
     */

    /**
     * Instantiates the overview presenter.
     * 
     * @param overviewWidgetView
     *            The UI for displaying the selected portion
     * @param handlerManager
     *            A {@link HandlerManager}
     * @param dependantHandlerManager
     *            A {@link HandlerManager} who will receive a
     *            {@link SetDomainBoundsEvent} and (optionally) a
     *            {@link LoadImageDataEvent} after a drag on the overviewWidget
     *            finished. The bounds are the current domain bounds of the
     *            overview (the portion selected).
     */
    public OverviewPresenter(GenericWidgetView overviewWidgetView, HandlerManager handlerManager,
            HandlerManager dependantHandlerManager) {
        Validate.notNull(handlerManager);
        this.overviewWidgetView = Validate.notNull(overviewWidgetView);

        addDependantHandlerManager(dependantHandlerManager);

        OverviewPresenterEventHandler eventHandler = new OverviewPresenterEventHandler();
        handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
        handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetOverviewDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);

        setFireLoadImageDataEvent(true);
    }

    /**
     * Adds a view as a dragging handle. The {@link Bound} enums specify which
     * bound of the overview will change as a result of dragging the handle.
     * This will both be reflected in the placement and dimensions of the
     * overview on the UI and also on the domain bounds change of the components
     * registerd as dependant handler managers.
     * 
     * <p>
     * A handle with the <code>LEFT</code> bound changes the left bound.
     * Dragging this handle will change the left bound of the overview bound and
     * leave all other bounds unchanged. The width of the overview changes
     * accordingly
     * 
     * <p>
     * When two bounds are specified, e. g. <code>LEFT</code> and
     * <code>RIGHT</code>, dragging the handle will result in the view moving
     * horizontally, preserving it's width, but changing both left and right by
     * the same amount.
     * 
     * @param handleView
     *            the view to be added as a dragging handle
     * @param bounds
     *            bounds which the handle will change
     */
    public void addHandle(GenericWidgetView handleView, Bound... bounds) {
        this.handleWidgets.add(new Handle(handleView, bounds));
    }

    /**
     * Specifies whether the overview should keep its aspect ratio when it is
     * being resized or not.<br />
     * <br />
     * <b>Default:</b> <code>false</code>
     * 
     * @param preserveAspectRatio
     *            <code>true</code> to preserve the aspect ratio,
     *            <code>false</code> not to
     */
    public void setPreserveAspectRatio(boolean preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
    }

    /**
     * The minimum width the overview can be resized to.
     * 
     * <b>Default</b>: 0 pixels
     * 
     * @param minWidth
     *            minimum width, in pixels
     */
    public void setMinClippingWidth(int minWidth) {
        this.minClippingWidth = minWidth;
    }

    /**
     * The minimum height the overview can be resized to.
     * 
     * <p>
     * <b>Default</b>: 0 pixels
     * 
     * @param minHeight
     *            minimum height, in pixels
     */
    public void setMinClippingHeight(int minHeight) {
        this.minClippingHeight = minHeight;
    }

    /**
     * If set to true the overview widget's left and right pixel bounds are
     * locked. So the left and right handles (if any) can't be moved. An
     * overview widget which is horizontally locked can only move vertically.<br />
     * <br>
     * <b>Disables:</b> {@link #preserveAspectRatio}<br />
     * 
     * <p>
     * <b>Default:</b> false
     * 
     * @param horizontallyLocked
     */
    public void setHorizontallyLocked(boolean horizontallyLocked) {
        this.horizontallyLocked = horizontallyLocked;

        if (horizontallyLocked) {
            this.preserveAspectRatio = false;
        }
    }

    /**
     * If set to true the overview widget's top and bottom pixel bounds are
     * locked. So the top and bottom handles (if any) can't be moved. An
     * overview widget which is vertically locked can only move horizontally.<br />
     * <br>
     * <b>Disables:</b> {@link #preserveAspectRatio}<br />
     * 
     * <p>
     * <b>Default:</b> false
     * 
     * @param verticallyLocked
     */
    public void setVerticallyLocked(boolean verticallyLocked) {
        this.verticallyLocked = verticallyLocked;

        if (verticallyLocked) {
            this.preserveAspectRatio = false;
        }
    }

    /**
     * Sets the amount of pixels for the vertical offset of the overview widget.
     * 
     * <p>
     * <b>Default:</b> 0 pixels
     * 
     * @param overviewTopOffsetInPixels
     *            offset in pixels
     */
    public void setOverviewTopOffset(int overviewTopOffsetInPixels) {
        this.overviewTopOffset = overviewTopOffsetInPixels;
    }

    /**
     * Sets the amount of pixels for the vertical offset of the overview widget.
     * 
     * <p>
     * <b>Default:</b> 0 pixels
     * 
     * @param overviewLeftOffsetInPixels
     *            offset in pixels
     */
    public void setOverviewLeftOffset(int overviewLeftOffsetInPixels) {
        this.overviewLeftOffset = overviewLeftOffsetInPixels;
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

    /**
     * Adds {@link HandlerManager} who will receive a
     * {@link SetDomainBoundsEvent} and (optionally) a
     * {@link LoadImageDataEvent} after a drag on the overviewWidget finished.
     * 
     * @param handlerManager
     *            A {@link HandlerManager} instance
     */
    public void addDependantHandlerManager(HandlerManager handlerManager) {
        this.dependantHandlerManagers.add(Validate.notNull(handlerManager));
    }

    /*
     * methods that receive events
     */

    private void onSetDomainBounds(SetDomainBoundsEvent event) {
        this.currentDomainBounds = event.getBounds();

        if (isOverviewPlacementAvailable()) {
            this.currentPixelBounds = this.overviewBounds.transform(this.currentDomainBounds, this.currentViewportBounds);
            placeOverviewWidget(this.currentPixelBounds);
        }
    }

    private void onSetOverviewBounds(SetOverviewDomainBoundsEvent event) {
        this.overviewBounds = event.getBounds();

        if (isOverviewPlacementAvailable()) {
            this.currentPixelBounds = this.overviewBounds.transform(this.currentDomainBounds, this.currentViewportBounds);
            // If the incoming converted pixel data is smaller than the minimum
            // widget size: recalculate to mimimum pixel size.
            // When dragging, this will be the starting point instead of too
            // minimalistic data.
            if (this.currentPixelBounds.getAbsWidth() < this.minClippingWidth) {
                this.currentPixelBounds =
                        this.currentPixelBounds.setLeft(this.currentPixelBounds.getHorizontalCenter() - this.minClippingWidth / 2)
                                .setRight(this.currentPixelBounds.getHorizontalCenter() + this.minClippingWidth / 2);

            }

            if (this.currentPixelBounds.getAbsHeight() < this.minClippingHeight) {
                this.currentPixelBounds =
                        this.currentPixelBounds.setTop(this.currentPixelBounds.getVerticalCenter() - this.minClippingHeight / 2)
                                .setBottom(this.currentPixelBounds.getVerticalCenter() + this.minClippingHeight / 2);

            }

            placeOverviewWidget(this.currentPixelBounds);
        }

    }

    private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
        this.currentMaxDomainBounds = event;
    }

    private void onDragFinished(ViewportDragFinishedEvent event) {

        if (this.currentPixelBounds != null && this.currentViewportBounds != null) {
            Bounds newBounds = this.currentPixelBounds.transform(this.currentViewportBounds, this.currentDomainBounds);
            
            // WORKAROUND to have a maximum zoom level in the time frame Bug 508
            long begin = newBounds.getLeft().longValue();
            long end = newBounds.getRight().longValue();
            if (ClientUtils.isValidTimeFrameForZoomIn(begin, end)) {
                if (isValidDataBounds(newBounds) && isValidPixelBounds(this.currentPixelBounds)) {

                    fireEventOnAllDependantHandlerManagers(new SwitchAutoscaleEvent(true));
                    
                    fireEventOnAllDependantHandlerManagers(new SetDomainBoundsEvent(newBounds));
                    
                    if (this.fireLoadImageDataEvent) {
                        fireEventOnAllDependantHandlerManagers(new LoadImageDataEvent());
                    }
                }
            }
        }

        this.initialDragHandle = null;
    }

    private void onDragInProgress(ViewportDragInProgressEvent event) {
        Handle handle =
                getHandleBeingHovered(event.getAbsolutePixelBounds().getLeft().intValue(), event
                        .getAbsolutePixelBounds().getTop().intValue());

        if (this.initialDragHandle == null) {
            this.initialDragHandle = handle;
        }

        if (this.initialDragHandle != null && this.currentPixelBounds != null && this.currentViewportBounds != null) {

            Bounds newPixelBounds = this.currentPixelBounds;

            double aspect = this.currentViewportBounds.getAbsWidth() / this.currentViewportBounds.getAbsHeight();

            for (Bound bound : this.initialDragHandle.getBound()) {
                switch (bound) {
                case LEFT:
                    if (this.horizontallyLocked) {
                        // if horizontally locked don't move this handle

                        break;

                    } else if (this.preserveAspectRatio && !this.initialDragHandle.hasAllBounds()) {
                        // to preserve the aspect ratio resize all handles
                        // accordingly, except for the center handle.
                        // the center handle has all bounds attached to is and
                        // moves the whole overview
                        newPixelBounds =
                                newPixelBounds.shiftLeft(event.getHorizontalDragOffset()).shiftRight(
                                        -event.getHorizontalDragOffset()).shiftTop(
                                        event.getHorizontalDragOffset() / aspect).shiftBottom(
                                        -(event.getHorizontalDragOffset() / aspect));
                    } else {
                        // if this handle is not locked and doesn't need to
                        // preserve the aspect ratio move freely
                        newPixelBounds = newPixelBounds.shiftLeft(event.getHorizontalDragOffset());

                    }
                    break;
                case RIGHT:
                    if (this.horizontallyLocked) {

                        break;

                    } else if (this.preserveAspectRatio && !this.initialDragHandle.hasAllBounds()) {

                        newPixelBounds =
                                newPixelBounds.shiftLeft(-event.getHorizontalDragOffset()).shiftRight(
                                        event.getHorizontalDragOffset()).shiftTop(
                                        -(event.getHorizontalDragOffset() / aspect)).shiftBottom(
                                        event.getHorizontalDragOffset() / aspect);
                    } else {

                        newPixelBounds = newPixelBounds.shiftRight(event.getHorizontalDragOffset());

                    }
                    break;
                case TOP:
                    if (this.verticallyLocked) {

                        break;

                    } else if (this.preserveAspectRatio && !this.initialDragHandle.hasAllBounds()) {
                        newPixelBounds =
                                newPixelBounds.shiftLeft(event.getVerticalDragOffset() * aspect).shiftRight(
                                        -(event.getVerticalDragOffset() * aspect)).shiftTop(
                                        event.getVerticalDragOffset()).shiftBottom(-event.getVerticalDragOffset());
                    } else {

                        newPixelBounds = newPixelBounds.shiftTop(event.getVerticalDragOffset());

                    }
                    break;
                case BOTTOM:
                    if (this.verticallyLocked) {

                        break;

                    } else if (this.preserveAspectRatio && !this.initialDragHandle.hasAllBounds()) {

                        newPixelBounds =
                                newPixelBounds.shiftLeft(-(event.getVerticalDragOffset() * aspect)).shiftRight(
                                        event.getVerticalDragOffset() * aspect).shiftTop(
                                        -event.getVerticalDragOffset()).shiftBottom(event.getVerticalDragOffset());

                    } else {

                        newPixelBounds = newPixelBounds.shiftBottom(event.getVerticalDragOffset());

                    }
                    break;
                }
            }

            Bounds dataBounds = newPixelBounds.transform(this.currentViewportBounds, this.currentDomainBounds);

            if (isValidDataBounds(dataBounds) && isValidPixelBounds(newPixelBounds)) {

                this.currentPixelBounds = newPixelBounds;
                placeOverviewWidget(this.currentPixelBounds);

            }
        }
    }

    private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
        this.currentViewportBounds = event.getBounds();

        if (isOverviewPlacementAvailable()) {
            this.currentPixelBounds = this.overviewBounds.transform(this.currentDomainBounds, this.currentViewportBounds);
            placeOverviewWidget(this.currentPixelBounds);
        }
    }

    /*
     * helper methods
     */

    private void fireEventOnAllDependantHandlerManagers(GwtEvent<? extends EventHandler> gwtEvent) {
        for (HandlerManager hm : this.dependantHandlerManagers) {
            hm.fireEvent(gwtEvent);
        }
    }

    /**
     * Get the Handle, which area contains the the relative postition
     * <code>x</code>, <code>y</code>.
     * 
     * @param x
     *            the relative X position, in pixels
     * @param y
     *            the relative Y position, in pixels
     * @return the {@link Handle} at position x, y
     */
    private Handle getHandleBeingHovered(int x, int y) {
        for (Handle handleWidget : this.handleWidgets) {
            if (handleWidget.getBounds().contains(x, y)) {
                return handleWidget;
            }
        }
        return null;
    }

    /**
     * Place the overview widget respectively to the offset.
     */
    private void placeOverviewWidget(Bounds bounds) {
        this.overviewWidgetView.setWidth(this.currentPixelBounds.getAbsWidth().intValue());
        this.overviewWidgetView.setHeight(this.currentPixelBounds.getAbsHeight().intValue());

        if (this.horizontallyLocked) {
            this.overviewWidgetView.setRelX(this.overviewLeftOffset);
        } else {
            this.overviewWidgetView.setRelX(this.currentPixelBounds.getLeft().intValue() + this.overviewLeftOffset);
        }

        if (this.verticallyLocked) {
            this.overviewWidgetView.setRelY(this.overviewTopOffset);
        } else {
            this.overviewWidgetView.setRelY(this.currentPixelBounds.getTop().intValue() + this.overviewTopOffset);
        }

        this.overviewWidgetView.show();
    }

    private boolean isOverviewPlacementAvailable() {
        return this.overviewBounds != null && this.currentDomainBounds != null && this.currentViewportBounds != null;
    }

    private boolean isValidDataBounds(Bounds dataBounds) {
        return this.currentMaxDomainBounds == null
                || this.currentMaxDomainBounds.containsHorizontally(dataBounds.getLeft(), dataBounds.getRight())
                && this.currentMaxDomainBounds.containsVertically(dataBounds.getTop(), dataBounds.getBottom());
    }

    private boolean isValidPixelBounds(Bounds pixelBounds) {
        return pixelBounds.getAbsWidth() >= this.minClippingWidth && pixelBounds.getAbsHeight() >= this.minClippingHeight;
    }

    /*
     * inner class that delegates received events to internal methods
     */

    private class OverviewPresenterEventHandler implements ViewportDragInProgressEventHandler,
            ViewportDragFinishedEventHandler, SetDomainBoundsEventHandler, SetOverviewDomainBoundsEventHandler,
            SetMaxDomainBoundsEventHandler, SetViewportPixelBoundsEventHandler {

        public void onDragInProgress(ViewportDragInProgressEvent event) {
            OverviewPresenter.this.onDragInProgress(event);
        }

        public void onDragFinished(ViewportDragFinishedEvent event) {
            OverviewPresenter.this.onDragFinished(event);
        }

        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            OverviewPresenter.this.onSetDomainBounds(event);
        }

        public void onSetOverviewDomainBounds(SetOverviewDomainBoundsEvent event) {
            OverviewPresenter.this.onSetOverviewBounds(event);
        }

        public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
            OverviewPresenter.this.onSetMaxDomainBounds(event);
        }

        public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
            OverviewPresenter.this.onSetViewportBounds(event);
        }

    }

    private class Handle {
        private GenericWidgetView widget;

        private List<Bound> bounds;

        public Handle(GenericWidgetView widget, Bound[] bounds) {
            super();
            this.widget = widget;
            this.bounds = Arrays.asList(bounds);
        }

        public List<Bound> getBound() {
            return this.bounds;
        }

        public boolean hasAllBounds() {
            return this.bounds.contains(Bound.LEFT) && this.bounds.contains(Bound.RIGHT) && this.bounds.contains(Bound.TOP)
                    && this.bounds.contains(Bound.BOTTOM);
        }

        public Bounds getBounds() {
            return this.widget.getAbsBounds();
        }
    }
}
