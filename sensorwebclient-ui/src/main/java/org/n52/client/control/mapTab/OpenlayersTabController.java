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
package org.n52.client.control.mapTab;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Marker;
import org.n52.client.control.Controller;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.LegendElementSelectedEvent;
import org.n52.client.eventBus.events.ResizeEvent;
import org.n52.client.eventBus.events.TabSelectedEvent;
import org.n52.client.eventBus.events.TimeSeriesChangedEvent;
import org.n52.client.eventBus.events.dataEvents.sos.DeleteTimeSeriesEvent;
import org.n52.client.eventBus.events.dataEvents.sos.TimeSeriesHasDataEvent;
import org.n52.client.eventBus.events.dataEvents.sos.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.TimeSeriesHasDataEventHandler;
import org.n52.client.eventBus.events.handler.LegendElementSelectedEventHandler;
import org.n52.client.eventBus.events.handler.ResizeEventHandler;
import org.n52.client.eventBus.events.handler.TabSelectedEventHandler;
import org.n52.client.eventBus.events.handler.TimeSeriesChangedEventHandler;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
import org.n52.client.model.data.representations.TimeSeries;
import org.n52.client.view.View;
import org.n52.client.view.gui.elements.interfaces.LegendElement;
import org.n52.client.view.gui.elements.tabImpl.ATabEventBroker;
import org.n52.client.view.gui.elements.tabImpl.OpenlayersTab;
import org.n52.client.view.gui.widgets.mapping.OpenlayersMarker;

@SuppressWarnings("deprecation")
public class OpenlayersTabController extends Controller<OpenlayersTab> {

    public OpenlayersTabController(OpenlayersTab tab) {
        super(tab);

        // register event broker on event bus
        new OpenLayersTabEventBroker(EventBus.getMainEventBus());
    }

    private class OpenLayersTabEventBroker extends ATabEventBroker implements
            TimeSeriesChangedEventHandler,
            ResizeEventHandler,
            TabSelectedEventHandler,
            DeleteTimeSeriesEventHandler,
            LegendElementSelectedEventHandler,
            TimeSeriesHasDataEventHandler {

        public OpenLayersTabEventBroker(EventBus eventBus) {
            eventBus.addHandler(TimeSeriesChangedEvent.TYPE, this);
            eventBus.addHandler(ResizeEvent.TYPE, this);
            eventBus.addHandler(TabSelectedEvent.TYPE, this);
            eventBus.addHandler(DeleteTimeSeriesEvent.TYPE, this);
            eventBus.addHandler(LegendElementSelectedEvent.TYPE, this);
            eventBus.addHandler(TimeSeriesHasDataEvent.TYPE, this);
        }

        protected void contributeToLegend() {
            ArrayList<LegendElement> legendElems = new ArrayList<LegendElement>();
            TimeSeries[] ts = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
            for (TimeSeries timeSeries : ts) {
                legendElems.add(timeSeries.getLegendElement());
            }
            fillLegend(legendElems);
        }
        
        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
            if (isSelfSelectedTab()) {
                update();
            }
        }

        private void update() {
            int width = View.getInstance().getDataPanelWidth();
            int height = View.getInstance().getDataPanelHeight();
            OpenlayersTabController.this.tab.update();
            OpenlayersTabController.this.tab.resizeTo(width, height);
            contributeToLegend();
        }

        public void onResize(ResizeEvent evt) {
            update();
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
            update();
        }

        public void onSelected(TabSelectedEvent evt) {
            update();
        }

        public void onSelected(LegendElementSelectedEvent evt) {

            if (evt.getElement().getDataWrapper() instanceof TimeSeries) {

                TimeSeries ts = (TimeSeries) evt.getElement().getDataWrapper();
                OpenlayersTabController.this.tab.setSelectedTimeSeries(ts.getId());
                ArrayList<Marker> markers = OpenlayersTabController.this.getMarkers();

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

        @Override
        protected boolean isSelfSelectedTab() {
            return View.getInstance().getCurrentTab().equals(OpenlayersTabController.this.getTab());
        }

        public void onHasData(TimeSeriesHasDataEvent evt) {
            if (isSelfSelectedTab()) {
                update();
            }   
        }
    }

    protected ArrayList<Marker> getMarkers() {
        return this.tab.getMap().getMarkers();
    }
}
