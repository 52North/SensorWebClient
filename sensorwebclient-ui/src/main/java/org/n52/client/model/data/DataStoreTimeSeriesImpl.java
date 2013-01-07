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

package org.n52.client.model.data;

import static org.n52.client.sos.i18n.I18NStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.n52.client.eventBus.EventBus;
import org.n52.client.model.data.representations.DataWrapperComparator;
import org.n52.client.model.data.representations.TimeSeries;
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
import org.n52.client.util.exceptions.DataparsingException;
import org.n52.client.util.exceptions.ExceptionHandler;
import org.n52.client.view.gui.elements.legend.LegendElement;
import org.n52.client.view.gui.elements.legend.LegendEntryTimeSeries;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.shared.serializable.pojos.Axis;

public class DataStoreTimeSeriesImpl extends ADataStore<TimeSeries> {

    private static DataStoreTimeSeriesImpl inst;

    private DataStoreTimeSeriesEventBroker eventBroker;

    private boolean gridEnabled = true;

    private DataStoreTimeSeriesImpl() {
        this.eventBroker = new DataStoreTimeSeriesEventBroker();
    }

    public static DataStoreTimeSeriesImpl getInst() {
        if (inst == null) {
            inst = new DataStoreTimeSeriesImpl();
        }
        return inst;
    }

    public TimeSeries[] getTimeSeriesSorted() {
        TimeSeries[] timeSeries = new TimeSeries[this.dataItems.size()];
        Arrays.sort(getDataAsArray(timeSeries), new DataWrapperComparator());
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
            EventBus.getMainEventBus().addHandler(DeleteTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesPropsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ChangeTimeSeriesStyleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreAxisDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SetDomainBoundsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UndoEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(FirstValueOfTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesLastValueEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SwitchAutoscaleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TimeSeriesHasDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SwitchGridEvent.TYPE, this);
        }

        public void onStore(StoreTimeSeriesEvent evt) {
            storeDataItem(evt.getTimeSeries().getId(), evt.getTimeSeries());
            EventBus.getMainEventBus().fireEvent(new TimeSeriesChangedEvent());
        }

        public void onStore(StoreTimeSeriesDataEvent evt) {
            try {
                Set<String> itemIds = evt.getData().keySet();
                for (String id : itemIds) {
                    TimeSeries timeSeries = getDataItem(id);
                    timeSeries.addData(evt.getData().get(id));
                }
                EventBus.getMainEventBus().fireEvent(new TimeSeriesChangedEvent());
            }
            catch (DataparsingException e1) {
                ExceptionHandler.handleException(e1);
            }
        }

        public TimeSeries getFirst() {
            if ( !DataStoreTimeSeriesImpl.this.dataItems.isEmpty()) {
                return getTimeSeriesSorted()[0];
            }
            return null;
        }

        public void onStore(StoreTimeSeriesPropsEvent evt) {
            getDataItem(evt.getId()).setProperties(evt.getProps());
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
            TimeSeries tsDataItem = getDataItem(evt.getId());
            tsDataItem.setLegendElement(null);
            deleteDataItem(evt.getId());
            if (getFirst() != null) {
                LegendElement legendElement = getFirst().getLegendElement();
                LegendElementSelectedEvent event = new LegendElementSelectedEvent(legendElement, false);
                EventBus.getMainEventBus().fireEvent(event);
            }

            ArrayList<TimeSeriesChangedEventHandler> updateHandlers = new ArrayList<TimeSeriesChangedEventHandler>();
            Collection<TimeSeries> timeSeries = DataStoreTimeSeriesImpl.this.dataItems.values();
            for (TimeSeries timeSerie : timeSeries) {
                LegendEntryTimeSeries le = (LegendEntryTimeSeries) timeSerie.getLegendElement();
                updateHandlers.add(le.getEventBroker());
            }

            TimeSeriesChangedEvent event = new TimeSeriesChangedEvent();
            EventBus.getMainEventBus().fireEvent(event);
            EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
        }

        public void onChange(ChangeTimeSeriesStyleEvent evt) {
            TimeSeries ts = getDataItem(evt.getID());
            ts.setColor(evt.getHexColor());
            ts.setOpacity(evt.getOpacityPercentage());
            ts.setScaleToZero(evt.isZeroScaled());
            ts.setAutoScale(evt.getAutoScale());
            EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
        }

        public void onStore(StoreAxisDataEvent evt) {
            try {
                TimeSeries dataItem = getDataItem(evt.getTsID());
                if (dataItem.getProperties().isSetAxis()) {
                    dataItem.setAxisData(evt.getAxis());
                }
                dataItem.getProperties().setSetAxis(true);
            } catch (NullPointerException e) {
                Toaster.getInstance().addErrorMessage(i18n.timeSeriesNotExists());
            }
        }

        public void onUndo() {
            TimeSeries[] series = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
            for (int i = 0; i < series.length; i++) {
                series[i].popAxis();
                series[i].getProperties().setSetAxis(false);
                series[i].getProperties().setAutoScale(false);
            }
        }

        public void onStore(FirstValueOfTimeSeriesEvent evt) {
            TimeSeries ts = getDataItem(evt.getTsID());
            if (ts != null) {
                ts.setFirstValueDate(evt.getDate());
                ts.setFirstValue(evt.getVal());
            }
        }

        public void onStore(StoreTimeSeriesLastValueEvent evt) {
            TimeSeries ts = getDataItem(evt.getTsID());
            if (ts != null) {
                ts.setLastValueDate(evt.getDate());
                ts.setLastValue(evt.getVal());
            }
        }

        public void onSwitch(SwitchAutoscaleEvent evt) {
            for (TimeSeries ts : getDataItems().values()) {
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

            for (TimeSeries ts : DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted()) {
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
            if (DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted().length > 0) {
                // EventBus.getInst().fireEvent(new RequestDataEvent());
            }

        }

        public void onHasData(TimeSeriesHasDataEvent evt) {
            try {
                DataStoreTimeSeriesImpl.this.getDataItem(evt.getTSID()).setHasData(evt.hasData());
            } catch (NullPointerException e) {
                Toaster.getInstance().addErrorMessage(i18n.timeSeriesNotExists());
            }
        }

        public void onSwitch() {
            DataStoreTimeSeriesImpl.this.gridEnabled = !DataStoreTimeSeriesImpl.this.gridEnabled;
        }
    }

    public boolean isGridEnabled() {
        return this.gridEnabled;

    }

}