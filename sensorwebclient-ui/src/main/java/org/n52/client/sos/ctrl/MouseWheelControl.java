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
package org.n52.client.sos.ctrl;
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
import java.util.List;

import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDataAreaPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDataAreaPixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.ViewportMouseWheelEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseWheelEventHandler;
import org.eesgmbh.gimv.client.util.EventAccumulator;
import org.eesgmbh.gimv.client.util.EventAccumulator.Callback;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Point;
import org.eesgmbh.gimv.shared.util.Validate;
import org.n52.client.util.ClientUtils;

import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * <p>
 * Reacts to {@link MouseWheelEvent} over the viewport and zooms the image
 * accordingly. The zoom centers on the current mouse position within the
 * viewport.
 * 
 * <p>
 * The zoom factor which is a percentage value (0 to 1 based) can be set with
 * {@link #setZoomFactor(double)}. The default value is 0.2.
 * 
 * <p>
 * The control can be configured to preview a zoomed image (meaning rescaled on
 * the client side) by setting
 * {@link #setPreviewZoomByRescalingTheImage(boolean)} before it is being fully
 * rendered. This is enabled by default.
 * 
 * <p>
 * By default, a {@link LoadImageDataEvent} is only fired after 150ms passed
 * since the last mouse wheel event. This effectively restricts the number of
 * {@link LoadImageDataEvent}, potentially causing expensive rendering on the
 * server side. The value can be changed with
 * {@link #setLoadImageDataEventFiringDelay(int)}.
 * 
 * <p>
 * Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * <li> {@link ViewportMouseWheelEvent} (mandatory, won't do anything otherwise)
 * <li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * <li> {@link SetViewportPixelBoundsEvent} (mandatory, won't work otherwise)
 * <li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be
 * no restriction)
 * <li> {@link SetDataAreaPixelBoundsEvent} (optional, if the vieport contains an
 * image which contains a data area related to {@link SetDomainBoundsEvent},
 * e.g. axis areas)
 * </ul>
 * 
 * <p>
 * Fires the following events
 * <ul>
 * <li> {@link ChangeImagePixelBoundsEvent} recomputed after recieving a mouse
 * wheel event, rescales the image to have an immediate preview
 * <li> {@link SetDomainBoundsEvent} recomputed after recieving a mouse wheel
 * event
 * <li> {@link LoadImageDataEvent} fired after firing
 * {@link ChangeImagePixelBoundsEvent}
 * </ul>
 * 
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class MouseWheelControl {

    private final HandlerManager handlerManager;

    private double zoomFactor;

    private boolean previewZoomByRescalingTheImage;

    private boolean fireLoadImageDataEvent;

    private EventAccumulator eventAccumulator;

    private SetDomainBoundsEvent currentSetDomainBoundsEvent;

    private SetMaxDomainBoundsEvent currentMaxDomainBoundsEvent;

    private SetDataAreaPixelBoundsEvent currentDataAreaBoundsEvent;

    private SetViewportPixelBoundsEvent currentViewportBoundsEvent;

    /**
     * Instantiates the control with the default configuration.
     * 
     * @param handlerManager
     *            A {@link HandlerManager}
     */
    public MouseWheelControl(HandlerManager handlerManager) {
        this.handlerManager = Validate.notNull(handlerManager);

        MouseWheelControlEventHandler eventHandler = new MouseWheelControlEventHandler();
        this.handlerManager.addHandler(ViewportMouseWheelEvent.TYPE, eventHandler);
        this.handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
        this.handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
        this.handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);
        this.handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);

        setZoomFactor(0.2);
        setPreviewZoomByRescalingTheImage(true);
        setFireLoadImageDataEvent(true);
        setLoadImageDataEventFiringDelay(150);
    }

    /**
     * Sets the zoom factor both for zooming in and out. Default is 0.2 (20%)
     * 
     * @param zoomFactor
     *            zoom factor must be positive and not zero
     */
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = Validate.isPositive(zoomFactor / 2);
    }

    /**
     * <p>
     * Whether you want to see a preview of the image by simply repositioning
     * and rescaling it before the new image is beeing rendered and displayed.
     * This gives an instant feedback to the user.
     * 
     * <p>
     * Default is true.
     * 
     * @param previewZoomByRescalingTheImage
     */
    public void setPreviewZoomByRescalingTheImage(boolean previewZoomByRescalingTheImage) {
        this.previewZoomByRescalingTheImage = previewZoomByRescalingTheImage;
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
     * Sets the time in milliseconds for how long to defer firing a
     * {@link LoadImageDataEvent}.
     * 
     * <p>
     * The default value is 150 ms. This means that 150 ms have to pass after
     * the last wheel event until a {@link LoadImageDataEvent} is fired causing
     * the image to be newly rendered. If multiple wheel events were received
     * during that period, the new image bounds will be the 'sum' of all wheel
     * events.
     * 
     * <p>
     * This helps to reduce server load where rendering might take place.
     * 
     * @param delayInMillis
     *            A millisecond value. Pass in 0 (zero) to deactivate delayed
     *            firing of {@link LoadImageDataEvent}
     */
    public void setLoadImageDataEventFiringDelay(int delayInMillis) {
        if (this.eventAccumulator == null) {
            this.eventAccumulator = new EventAccumulator(delayInMillis, new EventAccumulatorCallback());
        } else {
            this.eventAccumulator.setCallbackExecutionDelay(delayInMillis);
        }
    }

    private void onMouseWheel(ViewportMouseWheelEvent event) {
        if (this.currentSetDomainBoundsEvent != null && this.currentViewportBoundsEvent != null) {
            Bounds newDomainBounds = changeDomainBounds(event.getMouseWheelEvent());

            if (newDomainBounds != null) {

                // WORKAROUND to have a maximum zoom level in the time frame Bug 508
                long begin = newDomainBounds.getLeft().longValue();
                long end = newDomainBounds.getRight().longValue();
                if (ClientUtils.isValidTimeFrameForZoomIn(begin, end)) {
                    if (this.previewZoomByRescalingTheImage) {
                        Bounds newImageBounds = changeImageBounds(event.getMouseWheelEvent());

                        // rescale the image
                        this.handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(newImageBounds.getLeft()
                                - getViewportBounds().getLeft(), newImageBounds.getTop()
                                - getViewportBounds().getTop(), newImageBounds.getWidth()
                                - getViewportBounds().getWidth(), newImageBounds.getHeight()
                                - getViewportBounds().getHeight()));
                    }
                    this.handlerManager.fireEvent(new SetDomainBoundsEvent(newDomainBounds));
                    this.eventAccumulator.addEvent(new LoadImageDataEvent());
                }
            }
        }
    }

    /**
     * Computes the new domain bounds
     */
    private Bounds changeDomainBounds(MouseWheelEvent mouseWheelEvent) {
        Bounds setBounds = this.currentSetDomainBoundsEvent.getBounds();

        // memorizing the domain point at the current mouse position
        Point originalDomainPoint = computeDomainPointAtMousePosition(mouseWheelEvent, setBounds);

        // zoom in or zoom out at the center
        if (mouseWheelEvent.isNorth()) {
            setBounds =
                    this.currentSetDomainBoundsEvent.getBounds().transformProportional(
                            createZoomInBounds(computeDelta(mouseWheelEvent)));
        } else {
            setBounds =
                    this.currentSetDomainBoundsEvent.getBounds().transformProportional(
                            createZoomOutBounds(computeDelta(mouseWheelEvent)));
        }

        // figuring out the new domain point at the mouse position
        Point newDomainPoint = computeDomainPointAtMousePosition(mouseWheelEvent, setBounds);

        // shifting the originalDomainPoint back to the mouse position
        setBounds =
                setBounds.shiftAbsolute(originalDomainPoint.getX() - newDomainPoint.getX(), originalDomainPoint.getY()
                        - newDomainPoint.getY());

        if (this.currentMaxDomainBoundsEvent == null
                || (this.currentMaxDomainBoundsEvent.containsHorizontally(setBounds.getLeft(), setBounds.getRight()) && this.currentMaxDomainBoundsEvent
                        .containsVertically(setBounds.getTop(), setBounds.getBottom()))) {

            return setBounds;
        } else {
            return null;
        }
    }

    /**
     * Computes the new bounds of the image for the preview
     */
    private Bounds changeImageBounds(MouseWheelEvent mouseWheelEvent) {
        // memorizing current mouse position
        Point mousePos =
                new Point(mouseWheelEvent.getX() - getViewportBounds().getLeft(), mouseWheelEvent.getY()
                        - getViewportBounds().getTop());

        // zoom in or zoom out at the center (the inverse of changeSetBounds)
        Bounds newImageBounds;
        if (mouseWheelEvent.isNorth()) {
            newImageBounds =
                    getViewportDataAreaBounds().transformProportional(
                            createZoomOutBounds(computeDelta(mouseWheelEvent)));
        } else {
            newImageBounds =
                    getViewportDataAreaBounds().transformProportional(
                            createZoomInBounds(computeDelta(mouseWheelEvent)));
        }

        // translating it back to the viewport size
        newImageBounds = newImageBounds.transform(getViewportDataAreaBounds(), getViewportBounds());

        // new pixel position at mouse coordinates
        Point relPoint =
                new Point((mouseWheelEvent.getX() - getViewportBounds().getLeft()) / getViewportBounds().getWidth(),
                        (mouseWheelEvent.getY() - getViewportBounds().getTop()) / getViewportBounds().getHeight());

        Point newMousePos = newImageBounds.findAbsolutePoint(relPoint);

        // shifting the mousePos back to the mouse position
        newImageBounds =
                newImageBounds.shiftAbsolute(mousePos.getX() - newMousePos.getX(), mousePos.getY()
                        - newMousePos.getY());

        return newImageBounds;
    }

    private Point computeDomainPointAtMousePosition(MouseWheelEvent mouseWheelEvent, Bounds domainBounds) {
        Bounds viewportDataAreaBounds = getViewportDataAreaBounds();

        Point relPoint =
                new Point((mouseWheelEvent.getX() - viewportDataAreaBounds.getLeft())
                        / viewportDataAreaBounds.getWidth(),
                        (mouseWheelEvent.getY() - viewportDataAreaBounds.getTop())
                                / viewportDataAreaBounds.getHeight());

        return domainBounds.findAbsolutePoint(relPoint);
    }

    private Bounds createZoomInBounds(int delta) {
        Bounds zoomOutBounds = new Bounds(0, 1, 0, 1);

        for (int i = 0; i < delta; i++) {
            /*
             * Inverse of the zoomfactor, so that zooming back results in the
             * same bounds.
             * 
             * The formula is unfortunatly quite hard
             */
            zoomOutBounds =
                    zoomOutBounds
                            .transformProportional(new Bounds((1 - (1 / (1 + this.zoomFactor * 2))) / 2,
                                    1 - ((1 - (1 / (1 + this.zoomFactor * 2))) / 2),
                                    (1 - (1 / (1 + this.zoomFactor * 2))) / 2,
                                    1 - ((1 - (1 / (1 + this.zoomFactor * 2))) / 2)));
        }

        return zoomOutBounds;
    }

    private Bounds createZoomOutBounds(int delta) {
        Bounds zoomOutBounds = new Bounds(0, 1, 0, 1);

        for (int i = 0; i < delta; i++) {
            zoomOutBounds =
                    zoomOutBounds.transformProportional(new Bounds(-this.zoomFactor, 1 + this.zoomFactor,
                            -this.zoomFactor, 1 + this.zoomFactor));
        }

        return zoomOutBounds;
    }

    private int computeDelta(MouseWheelEvent mouseWheelEvent) {
        /*
         * If the user scrolls fast, multiple mouse wheels might be summed up to
         * one event. GWT seems to normalize the different deltas across browser
         * to a multiple of 3 (3, 6, 9... tested with IE, FF, Chrome).
         * 
         * Here we normalize to 1, 2, 3...
         */
        return Math.abs(mouseWheelEvent.getDeltaY() / 3); // integer division
    }

    private Bounds getViewportDataAreaBounds() {
        if (this.currentDataAreaBoundsEvent != null) {
            return this.currentDataAreaBoundsEvent.getBounds();
        } else {
            return getViewportBounds();
        }
    }

    private Bounds getViewportBounds() {
        return this.currentViewportBoundsEvent.getBounds();
    }

    private void onSetDomainBounds(SetDomainBoundsEvent event) {
        this.currentSetDomainBoundsEvent = event;
    }

    private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
        this.currentMaxDomainBoundsEvent = event;
    }

    private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
        this.currentDataAreaBoundsEvent = event;
    }

    private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
        this.currentViewportBoundsEvent = event;
    }

    /*
     * Gets invoked after a configurable delay and causes the image to be
     * actually rendered.
     */
    private class EventAccumulatorCallback implements Callback {
        public void excute(List<GwtEvent<? extends EventHandler>> gwtEvents) {
            if (MouseWheelControl.this.fireLoadImageDataEvent) {
                MouseWheelControl.this.handlerManager.fireEvent(new LoadImageDataEvent());
            }
        }
    }

    private class MouseWheelControlEventHandler implements SetDomainBoundsEventHandler,
            SetMaxDomainBoundsEventHandler, SetDataAreaPixelBoundsEventHandler, SetViewportPixelBoundsEventHandler,
            ViewportMouseWheelEventHandler {
        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            MouseWheelControl.this.onSetDomainBounds(event);
        }

        public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
            MouseWheelControl.this.onSetMaxDomainBounds(event);
        }

        public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
            MouseWheelControl.this.onSetDataAreaBounds(event);
        }

        public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
            MouseWheelControl.this.onSetViewportBounds(event);
        }

        public void onMouseWheel(ViewportMouseWheelEvent event) {
            MouseWheelControl.this.onMouseWheel(event);
        }
    }
}
