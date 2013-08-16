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

import java.util.ArrayList;
import java.util.HashMap;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ATabEventBroker;
import org.n52.client.ctrl.Controller;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.ctrl.GUIException;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.LegendElementSelectedEvent;
import org.n52.client.sos.event.ResizeEvent;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.data.DeleteTimeSeriesEvent;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.RequestSensorDataEvent;
import org.n52.client.sos.event.data.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.NewTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.RequestDataEventHandler;
import org.n52.client.sos.event.handler.LegendElementSelectedEventHandler;
import org.n52.client.sos.event.handler.ResizeEventHandler;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.sos.ui.TableTab;
import org.n52.client.ui.View;
import org.n52.client.ui.legend.Legend;
import org.n52.client.ui.legend.LegendElement;

@Deprecated
public class TableTabController extends Controller<TableTab> {

    private LegendElement selectedLegendElement;

    public TableTabController(TableTab tableTab) {
        super(tableTab);
        new TableTabEventBroker();

        this.dataControls = null;
        try {
            this.dataControls = new DataControlsTable(this);
        } catch (Exception e) {
            ExceptionHandler.handleException(new GUIException("Failed to load controls for table", e)); //$NON-NLS-1$
        }
    }


    public void fillLegend(ArrayList<LegendElement> legendElems) {

        Legend l = View.getView().getLegend();
        l.fill(legendElems);

    }


    private class TableTabEventBroker extends ATabEventBroker implements TimeSeriesChangedEventHandler,
            NewTimeSeriesEventHandler, ResizeEventHandler, TabSelectedEventHandler, LegendElementSelectedEventHandler,
            RequestDataEventHandler, DeleteTimeSeriesEventHandler {

        public TableTabEventBroker() {
            EventBus.getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(NewTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ResizeEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(LegendElementSelectedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TabSelectedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(RequestDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteTimeSeriesEvent.TYPE, this);
        }

        private void contributeToLegend() {
            if (isSelfSelectedTab()) {
                ArrayList<LegendElement> legendElems = new ArrayList<LegendElement>();
                TimeseriesLegendData[] ts = TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted();
                for (int i = 0; i < ts.length; i++) {
                    legendElems.add(ts[i].getLegendElement());
                }
                fillLegend(legendElems);
            }
        }

        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
            if (isSelfSelectedTab()) {
                Legend l = View.getView().getLegend();
                TableTabController.this.setSelectedLegendElement(l.getSelectedLegendelement());
                if (TableTabController.this.getSelectedLegendElement() != null) {
                    requestData();
                }
                contributeToLegend();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.n52.client.eventBus.events.handler.SelectedEventHandler#onSelected
         * (java.lang.Object)
         */
        public void onSelected(LegendElementSelectedEvent evt) {
            if (TableTabController.this.getSelectedLegendElement() == evt.getElement()) {
                return;
            }
            if (isSelfSelectedTab()) {
                TableTabController.this.setSelectedLegendElement(evt.getElement());
                requestData();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.n52.client.eventBus.events.handler.NewTimeSeriesEventHandler#
         * onNewTimeSeries(org.n52.client.eventBus.events.NewTimeSeriesEvent)
         */
        public void onNewTimeSeries(NewTimeSeriesEvent evt) {
            if (isSelfSelectedTab()) {
                contributeToLegend();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.n52.client.eventBus.events.handler.ResizeEventHandler#onResize
         * (org.n52.client.eventBus.events.ResizeEvent)
         */
        public void onResize(ResizeEvent evt) {
            if (isSelfSelectedTab()) {
                int tabWidth = View.getView().getDataPanelWidth();
                int tabHeight = View.getView().getDataPanelHeight();
                TableTabController.this.getTab().resizeTo(tabWidth, tabHeight);
                requestData();
            }
        }

        public void onSelected(TabSelectedEvent evt) {
            if (isSelfSelectedTab()) {
                Legend l = View.getView().getLegend();
                TableTabController.this.setSelectedLegendElement(l.getSelectedLegendelement());
                if (TableTabController.this.getSelectedLegendElement() != null) {
                    loadData();
                }
            }
        }

        public void onRequestData(RequestDataEvent evt) {
            if (isSelfSelectedTab()) {
                loadData();
            }
        }

        void requestData() {
            if (isSelfSelectedTab()) {
                HashMap<Long, Double> result = new HashMap<Long, Double>();

                HashMap<String, TimeseriesLegendData> data = TimeseriesDataStore.getTimeSeriesDataStore().getDataItems();
                for (TimeseriesLegendData dw : data.values()) {

                    if (dw.equals(TableTabController.this.getSelectedLegendElement().getDataWrapper())) {
                        result = dw.getData(TimeManager.getInst().getBegin(), TimeManager.getInst().getEnd());
                    }
                }

                TableTabController.this.getTab().update(result, TableTabController.this.getSelectedLegendElement());
            }

        }

        @Override
        protected boolean isSelfSelectedTab() {
            return View.getView().getCurrentTab().equals(TableTabController.this.getTab());
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.n52.client.eventBus.events.dataEvents.sos.handler.
         * DeleteTimeSeriesEventHandler
         * #onDeleteTimeSeries(org.n52.client.eventBus
         * .events.dataEvents.sos.DeleteTimeSeriesEvent)
         */
        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {

            HashMap<Long, Double> result = new HashMap<Long, Double>();
            HashMap<String, TimeseriesLegendData> data = TimeseriesDataStore.getTimeSeriesDataStore().getDataItems();
            for (TimeseriesLegendData dw : data.values()) {
            	//
            	try {
                    if (dw.equals(TableTabController.this.getSelectedLegendElement().getDataWrapper())) {
                        TableTabController.this.getTab().update(result,
                                TableTabController.this.getSelectedLegendElement());
                    }
                } catch (Exception e) {
                    // tabletab not opened
                }
            }
        }
    }

    void loadData() {
        EventBus.getMainEventBus().fireEvent(new RequestSensorDataEvent(null));
    }

    /**
     * Sets the selected legend element.
     * 
     * @param selectedLegendElement
     *            the selectedLegendElement to set
     */
    public void setSelectedLegendElement(LegendElement selectedLegendElement) {
        this.selectedLegendElement = selectedLegendElement;
    }

    /**
     * Gets the selected legend element.
     * 
     * @return the selectedLegendElement
     */
    public LegendElement getSelectedLegendElement() {
        return this.selectedLegendElement;
    }

}
