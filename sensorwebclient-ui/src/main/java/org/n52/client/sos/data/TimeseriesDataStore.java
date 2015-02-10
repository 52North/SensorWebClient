/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.client.sos.data;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ctrl.ExceptionHandler.handleException;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.n52.client.bus.EventBus;
import org.n52.client.model.ADataStore;
import org.n52.client.sos.DataparsingException;
import org.n52.client.sos.event.ChangeTimeSeriesStyleEvent;
import org.n52.client.sos.event.DatesChangedEvent;
import org.n52.client.sos.event.LegendElementSelectedEvent;
import org.n52.client.sos.event.SwitchGridEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.data.DeleteTimeSeriesEvent;
import org.n52.client.sos.event.data.FirstValueOfTimeSeriesEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.StoreAxisDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesLastValueEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesPropsEvent;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;
import org.n52.client.sos.event.data.TimeSeriesHasDataEvent;
import org.n52.client.sos.event.data.UndoEvent;
import org.n52.client.sos.event.data.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.StoreAxisDataEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesDataEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesFirstValueEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesLastValueEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesPropsEventHandler;
import org.n52.client.sos.event.data.handler.SwitchAutoscaleEventHandler;
import org.n52.client.sos.event.data.handler.TimeSeriesHasDataEventHandler;
import org.n52.client.sos.event.data.handler.UndoEventHandler;
import org.n52.client.sos.event.handler.ChangeTimeSeriesStyleEventHandler;
import org.n52.client.sos.event.handler.SwitchGridEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.legend.LegendDataComparator;
import org.n52.client.ui.legend.LegendElement;
import org.n52.client.ui.legend.LegendEntryTimeSeries;
import org.n52.shared.serializable.pojos.Axis;

public class TimeseriesDataStore extends ADataStore<TimeseriesLegendData> {

    private static TimeseriesDataStore inst;

    private DataStoreTimeSeriesEventBroker eventBroker;

    private boolean gridEnabled = true;

    private TimeseriesDataStore() {
        this.eventBroker = new DataStoreTimeSeriesEventBroker();
    }

    public static TimeseriesDataStore getTimeSeriesDataStore() {
        if (inst == null) {
            inst = new TimeseriesDataStore();
        }
        return inst;
    }

    public TimeseriesLegendData[] getTimeSeriesSorted() {
        TimeseriesLegendData[] timeSeries = new TimeseriesLegendData[this.dataItems.size()];
        Arrays.sort(getDataAsArray(timeSeries), new LegendDataComparator());
        return timeSeries;
    }

    public DataStoreTimeSeriesEventBroker getEventBroker() {
        return this.eventBroker;
    }

    private class DataStoreTimeSeriesEventBroker implements
            StoreTimeSeriesEventHandler,
            StoreTimeSeriesDataEventHandler,
            StoreTimeSeriesPropsEventHandler,
            DeleteTimeSeriesEventHandler,
            ChangeTimeSeriesStyleEventHandler,
            StoreAxisDataEventHandler,
            SetDomainBoundsEventHandler,
            UndoEventHandler,
            StoreTimeSeriesFirstValueEventHandler,
            StoreTimeSeriesLastValueEventHandler,
            SwitchAutoscaleEventHandler,
            TimeSeriesHasDataEventHandler,
            SwitchGridEventHandler {

        public DataStoreTimeSeriesEventBroker() {
            getMainEventBus().addHandler(DeleteTimeSeriesEvent.TYPE, this);
            getMainEventBus().addHandler(StoreTimeSeriesPropsEvent.TYPE, this);
            getMainEventBus().addHandler(StoreTimeSeriesEvent.TYPE, this);
            getMainEventBus().addHandler(StoreTimeSeriesDataEvent.TYPE, this);
            getMainEventBus().addHandler(ChangeTimeSeriesStyleEvent.TYPE, this);
            getMainEventBus().addHandler(StoreAxisDataEvent.TYPE, this);
            getMainEventBus().addHandler(SetDomainBoundsEvent.TYPE, this);
            getMainEventBus().addHandler(UndoEvent.TYPE, this);
            getMainEventBus().addHandler(FirstValueOfTimeSeriesEvent.TYPE, this);
            getMainEventBus().addHandler(StoreTimeSeriesLastValueEvent.TYPE, this);
            getMainEventBus().addHandler(SwitchAutoscaleEvent.TYPE, this);
            getMainEventBus().addHandler(TimeSeriesHasDataEvent.TYPE, this);
            getMainEventBus().addHandler(SwitchGridEvent.TYPE, this);
        }

        public void onStore(StoreTimeSeriesEvent evt) {
            storeDataItem(evt.getTimeSeries().getId(), evt.getTimeSeries());
            getMainEventBus().fireEvent(new TimeSeriesChangedEvent());
        }

        public void onStore(StoreTimeSeriesDataEvent evt) {
            try {
                Set<String> itemIds = evt.getData().keySet();
                for (String id : itemIds) {
                    TimeseriesLegendData timeSeries = getDataItem(id);
                    timeSeries.addData(evt.getData().get(id));
                }
                getMainEventBus().fireEvent(new TimeSeriesChangedEvent());
            }
            catch (DataparsingException e1) {
                handleException(e1);
            }
        }

        public TimeseriesLegendData getFirst() {
            if ( !TimeseriesDataStore.this.dataItems.isEmpty()) {
                return getTimeSeriesSorted()[0];
            }
            return null;
        }

        public void onStore(StoreTimeSeriesPropsEvent evt) {
            getDataItem(evt.getId()).setProperties(evt.getProps());
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
            TimeseriesLegendData tsDataItem = getDataItem(evt.getId());
            tsDataItem.setLegendElement(null);
            deleteDataItem(evt.getId());
            if (getFirst() != null) {
                LegendElement legendElement = getFirst().getLegendElement();
                LegendElementSelectedEvent event = new LegendElementSelectedEvent(legendElement, false);
                EventBus.getMainEventBus().fireEvent(event);
            }

            ArrayList<TimeSeriesChangedEventHandler> updateHandlers = new ArrayList<TimeSeriesChangedEventHandler>();
            Collection<TimeseriesLegendData> timeSeries = TimeseriesDataStore.this.dataItems.values();
            for (TimeseriesLegendData timeSerie : timeSeries) {
                LegendEntryTimeSeries le = (LegendEntryTimeSeries) timeSerie.getLegendElement();
                updateHandlers.add(le.getEventBroker());
            }

            TimeSeriesChangedEvent event = new TimeSeriesChangedEvent();
            EventBus.getMainEventBus().fireEvent(event);
            EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
        }

        public void onChange(ChangeTimeSeriesStyleEvent evt) {
            TimeseriesLegendData ts = getDataItem(evt.getID());
            ts.setColor(evt.getHexColor());
            ts.setOpacity(evt.getOpacityPercentage());
            ts.setScaleToZero(evt.isZeroScaled());
            ts.setAutoScale(evt.getAutoScale());
            EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
        }

        public void onStore(StoreAxisDataEvent evt) {
            try {
                TimeseriesLegendData dataItem = getDataItem(evt.getTsID());
                if (dataItem.getProperties().isSetAxis()) {
                    dataItem.setAxisData(evt.getAxis());
                }
                dataItem.getProperties().setSetAxis(true);
            } catch (NullPointerException e) {
                Toaster.getToasterInstance().addErrorMessage(i18n.timeSeriesNotExists());
            }
        }

        public void onUndo() {
            TimeseriesLegendData[] series = TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted();
            for (int i = 0; i < series.length; i++) {
                series[i].popAxis();
                series[i].getProperties().setSetAxis(false);
                series[i].getProperties().setAutoScale(false);
            }
        }

        public void onStore(FirstValueOfTimeSeriesEvent evt) {
            TimeseriesLegendData ts = getDataItem(evt.getTsID());
            if (ts != null) {
                ts.setFirstValueDate(evt.getDate());
                ts.setFirstValue(evt.getVal());
            }
        }

        public void onStore(StoreTimeSeriesLastValueEvent evt) {
            TimeseriesLegendData ts = getDataItem(evt.getTsID());
            if (ts != null) {
                ts.setLastValueDate(evt.getDate());
                ts.setLastValue(evt.getVal());
            }
        }

        public void onSwitch(SwitchAutoscaleEvent evt) {
            for (TimeseriesLegendData ts : getDataItems().values()) {
                ts.setAutoScale(evt.getSwitch());
            }
        }

        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            Double top = event.getBounds().getTop();
            Double bottom = event.getBounds().getBottom();

            if (top == null || bottom == null) {
                return;
            }

            long begin = event.getBounds().getLeft().longValue();
            long end = event.getBounds().getRight().longValue();

            EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(begin, end, true));

            for (TimeseriesLegendData ts : TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted()) {
                if (ts.getProperties().isAutoScale() != true) {
                    Axis a = ts.getProperties().getAxis();
                    double topDiff = a.getMinY() - top;
                    double bottomDiff = a.getMaxY() - bottom;
                    double topPercDiff = topDiff / a.getLength();
                    double bottomPercDiff = bottomDiff / a.getLength();

                    double range = a.getUpperBound() - a.getLowerBound();

                    double newUpper = a.getUpperBound() + topPercDiff * range;
                    a.setUpperBound(newUpper);
                    double newLower = a.getLowerBound() + bottomPercDiff * range;
                    a.setLowerBound(newLower);
                    a.setMaxY(a.getMaxY());
                    a.setMinY(a.getMinY());
                    ts.getProperties().setSetAxis(false);
                } 

            }
            if (TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted().length > 0) {
                // EventBus.getInst().fireEvent(new RequestDataEvent());
            }

        }

        public void onHasData(TimeSeriesHasDataEvent evt) {
            try {
                TimeseriesDataStore.this.getDataItem(evt.getTSID()).setHasData(evt.hasData());
            } catch (NullPointerException e) {
                Toaster.getToasterInstance().addErrorMessage(i18n.timeSeriesNotExists());
            }
        }

        public void onSwitch() {
            TimeseriesDataStore.this.gridEnabled = !TimeseriesDataStore.this.gridEnabled;
        }
    }

    public boolean isGridEnabled() {
        return this.gridEnabled;

    }

}