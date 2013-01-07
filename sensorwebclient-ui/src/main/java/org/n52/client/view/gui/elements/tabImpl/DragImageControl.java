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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
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
import org.eesgmbh.gimv.client.presenter.ZoomBoxPresenter;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.n52.client.eventBus.events.sos.SwitchAutoscaleEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * This control will change the image position within the viewport when the user
 * moves the mouse over the viewport with the left mouse button down (dragging).
 * 
 * <p>
 * After the user stopped dragging, new image data with the new domain bounds is
 * requested.
 * 
 * <p>
 * This control must be activated or deactivated with a {@link StateChangeEvent}
 * . The reason behind the mandatory activation is, that dragging can both mean
 * zooming or moving the image. Only this control or the
 * {@link ZoomBoxPresenter} can be active at the same time.
 * 
 * <p>
 * Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * <li> {@link ViewportDragInProgressEvent} (the control changes the image
 * position accordingly)
 * <li> {@link ViewportDragFinishedEvent} (will trigger a
 * {@link SetDomainBoundsEvent} and a {@link LoadImageDataEvent} )
 * <li> {@link SetDomainBoundsEvent} (mandatory, won't work otherwise)
 * <li> {@link StateChangeEvent} (must be set to move for the control to do
 * something)
 * <li> {@link SetMaxDomainBoundsEvent} (optional, if not received, there will be
 * no restriction)
 * </ul>
 * 
 * <p>
 * Fires the following events
 * <ul>
 * <li> {@link ChangeImagePixelBoundsEvent} fired on each
 * {@link ViewportDragInProgressEvent}
 * <li> {@link SetDomainBoundsEvent} fired after recieving
 * {@link ViewportDragFinishedEvent} with the new domain bounds
 * <li> {@link LoadImageDataEvent} fired after recieving
 * {@link ViewportDragFinishedEvent}
 * </ul>
 * 
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class DragImageControl {

    private final HandlerManager handlerManager;

    private boolean fireLoadImageDataEvent;

    private boolean active;

    private Bounds currentDomainBounds;

    private SetMaxDomainBoundsEvent currentMaxDomainBounds;

    /**
     * Registers with the handlerManager to receive all required events.
     * 
     * @param handlerManager
     *            A {@link HandlerManager}
     */
    public DragImageControl(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;

        DragImageControlEventHandler eventHandler = new DragImageControlEventHandler();
        handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
        handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(SetMaxDomainBoundsEvent.TYPE, eventHandler);
        handlerManager.addHandler(StateChangeEvent.TYPE, eventHandler);

        setFireLoadImageDataEvent(true);
    }

    /**
     * <p>
     * Specify whether a {@link LoadImageDataEvent} will be fired after a drag
     * is completed.
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

    private void onStateChange(StateChangeEvent event) {
        this.active = event.isMove();
    }

    private void onDragInProgress(ViewportDragInProgressEvent event) {
        if (this.active) {
            this.handlerManager.fireEvent(new ChangeImagePixelBoundsEvent(event.getHorizontalDragOffset(), event
                    .getVerticalDragOffset()));
        }
    }

    private void onDragFinished(ViewportDragFinishedEvent event) {
        if (this.active) {
            if (this.currentDomainBounds != null) {
                double xOffsetProp = -event.getProportionalBounds().getWidth();
                double yOffsetProp = -event.getProportionalBounds().getHeight();

                // constraining to max bounds
                Bounds newBounds = this.currentDomainBounds;

                if (this.currentMaxDomainBounds == null
                        || this.currentMaxDomainBounds.containsHorizontally(this.currentDomainBounds.shiftProportional(
                                xOffsetProp, 0).getLeft(), this.currentDomainBounds.shiftProportional(xOffsetProp, 0)
                                .getRight())) {
                    newBounds = newBounds.shiftProportional(xOffsetProp, 0);
                }

                if (this.currentMaxDomainBounds == null
                        || this.currentMaxDomainBounds.containsVertically(this.currentDomainBounds.shiftProportional(0,
                                yOffsetProp).getTop(), this.currentDomainBounds.shiftProportional(0, yOffsetProp)
                                .getBottom())) {
                    newBounds = newBounds.shiftProportional(0, yOffsetProp);
                }

                this.handlerManager.fireEvent(new SwitchAutoscaleEvent(false));

                this.handlerManager.fireEvent(new SetDomainBoundsEvent(newBounds));

                if (this.fireLoadImageDataEvent) {
                    this.handlerManager.fireEvent(new LoadImageDataEvent());
                }
            }
        }
    }

    private void onSetDomainBounds(SetDomainBoundsEvent event) {
        this.currentDomainBounds = event.getBounds();
    }

    private void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
        this.currentMaxDomainBounds = event;
    }

    private class DragImageControlEventHandler implements ViewportDragInProgressEventHandler,
            ViewportDragFinishedEventHandler, SetDomainBoundsEventHandler, SetMaxDomainBoundsEventHandler,
            StateChangeEventHandler {
        public void onDragInProgress(ViewportDragInProgressEvent event) {
            DragImageControl.this.onDragInProgress(event);
        }

        public void onDragFinished(ViewportDragFinishedEvent event) {
            DragImageControl.this.onDragFinished(event);
        }

        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            DragImageControl.this.onSetDomainBounds(event);
        }

        public void onSetMaxDomainBounds(SetMaxDomainBoundsEvent event) {
            DragImageControl.this.onSetMaxDomainBounds(event);
        }

        public void onStateChange(StateChangeEvent event) {
            DragImageControl.this.onStateChange(event);
        }
    }
}
