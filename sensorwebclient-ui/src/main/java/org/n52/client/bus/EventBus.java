/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.bus;

import org.n52.client.ctrl.ExceptionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * The client's backbone for event handling.
 */
public class EventBus extends HandlerManager {

    private static EventBus mainEventBus;
    
    private static EventBus overviewChartEventBus;

    private EventBus(Object source) {
        super(source);
    }

    public static EventBus getMainEventBus() {
        if (mainEventBus == null) {
            mainEventBus = new EventBus(null);
        }
        return mainEventBus;
    }
    
    public static EventBus getOverviewChartEventBus() {
        if (overviewChartEventBus == null) {
        	overviewChartEventBus = new EventBus(null);
        }
        return overviewChartEventBus;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        try {
            if (!GWT.isProdMode()) {
                GWT.log("Firing " + event.toDebugString()); 
            }
            super.fireEvent(event);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
    }

    public void fireEvent(GwtEvent<?> event, EventCallback callback) {
        try {
            if (!GWT.isProdMode()) {
                GWT.log("Firing " + event.toDebugString()); 
            }
            super.fireEvent(event);
            
            if (!GWT.isProdMode()) {
                GWT.log("    Event fired, calling callback"); 
            }
            callback.onEventFired();
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
    }

}
