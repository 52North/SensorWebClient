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
