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
package org.n52.client.control.legendMap;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Marker;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.LegendElementSelectedEvent;
import org.n52.client.eventBus.events.ResizeEvent;
import org.n52.client.eventBus.events.TimeSeriesChangedEvent;
import org.n52.client.eventBus.events.dataEvents.sos.DeleteTimeSeriesEvent;
import org.n52.client.eventBus.events.dataEvents.sos.TimeSeriesHasDataEvent;
import org.n52.client.eventBus.events.dataEvents.sos.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.TimeSeriesHasDataEventHandler;
import org.n52.client.eventBus.events.handler.LegendElementSelectedEventHandler;
import org.n52.client.eventBus.events.handler.ResizeEventHandler;
import org.n52.client.eventBus.events.handler.TimeSeriesChangedEventHandler;
import org.n52.client.model.data.representations.TimeSeries;
import org.n52.client.view.gui.widgets.mapping.LegendMap;
import org.n52.client.view.gui.widgets.mapping.OpenlayersMarker;

@Deprecated
public class LegendMapController {

	private LegendMap map;
	
    public LegendMapController(LegendMap legendMap) {
    	this.map = legendMap;
        new LegendMapEventBroker(this);
    }
    
    private void updateMap() {
    	map.update();
    }

    private class LegendMapEventBroker implements
            TimeSeriesChangedEventHandler,
            ResizeEventHandler,
            DeleteTimeSeriesEventHandler,
            LegendElementSelectedEventHandler,
            TimeSeriesHasDataEventHandler {

        private LegendMapController controller;

		public LegendMapEventBroker(LegendMapController controller) {
        	this.controller = controller;
        	EventBus eventBus = EventBus.getMainEventBus();
            eventBus.addHandler(TimeSeriesChangedEvent.TYPE, this);
            eventBus.addHandler(ResizeEvent.TYPE, this);
            eventBus.addHandler(DeleteTimeSeriesEvent.TYPE, this);
            eventBus.addHandler(LegendElementSelectedEvent.TYPE, this);
            eventBus.addHandler(TimeSeriesHasDataEvent.TYPE, this);
        }

        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
        	//TODO: only update when new Timeseries is loaded
            controller.updateMap();
        }

        public void onResize(ResizeEvent evt) {
        	controller.updateMap();
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
//            update();
        }

        public void onSelected(LegendElementSelectedEvent evt) {

            if (evt.getElement().getDataWrapper() instanceof TimeSeries) {

                TimeSeries ts = (TimeSeries) evt.getElement().getDataWrapper();
                controller.setSelectedTimeSeries(ts.getId());
                ArrayList<Marker> markers = controller.getMarkers();

                for (Marker m : markers) {
                    if (m instanceof OpenlayersMarker) {
                        OpenlayersMarker olm = (OpenlayersMarker) m;
                        if (olm.containsTS(ts.getId())) {
                            olm.mark();
                        }
                        else {
                            olm.unmark();
                        }
                    }
                }
            }
        }

        public void onHasData(TimeSeriesHasDataEvent evt) {
        	controller.updateMap();
        }
    }
    
    private void setSelectedTimeSeries(String id) {
    	map.setSelectedTimeSeries(id);
    }
    
    protected ArrayList<Marker> getMarkers() {
        return map.getMap().getMarkers();
    }
}
