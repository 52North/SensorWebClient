/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client.sos.ctrl;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.data.TimeseriesDataStore.getTimeSeriesDataStore;

import java.util.ArrayList;

import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEventHandler;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEventHandler;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.n52.client.Application;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ATabEventBroker;
import org.n52.client.ctrl.Controller;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.DatesChangedEvent;
import org.n52.client.sos.event.InitEvent;
import org.n52.client.sos.event.ResizeEvent;
import org.n52.client.sos.event.SwitchGridEvent;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.data.DeleteTimeSeriesEvent;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesPropsEvent;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;
import org.n52.client.sos.event.data.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.NewTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.RequestDataEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesPropsEventHandler;
import org.n52.client.sos.event.data.handler.SwitchAutoscaleEventHandler;
import org.n52.client.sos.event.handler.DatesChangedEventHandler;
import org.n52.client.sos.event.handler.InitEventHandler;
import org.n52.client.sos.event.handler.ResizeEventHandler;
import org.n52.client.sos.event.handler.SwitchGridEventHandler;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.sos.ui.DiagramTab;
import org.n52.client.ui.View;
import org.n52.client.ui.legend.LegendElement;

import com.smartgwt.client.types.Visibility;

public class DiagramTabController extends Controller<DiagramTab> {

    public Bounds currentOverviewBounds;

    public DiagramTabController(DiagramTab tab) {
        super(tab);

        // register event buses
        new EESTabEventBroker();
        new OverviewImageEventBroker();

		this.dataControls = new DataControlsEES();
		this.dataControls.setVisibility(Visibility.HIDDEN);
    }

    private class OverviewImageEventBroker implements SetOverviewDomainBoundsEventHandler, LoadImageDataEventHandler {

        public OverviewImageEventBroker() {
            EventBus bus = EventBus.getOverviewChartEventBus();
            bus.addHandler(SetOverviewDomainBoundsEvent.TYPE, this);
            bus.addHandler(LoadImageDataEvent.TYPE, this);
        }

        public void onSetOverviewDomainBounds(SetOverviewDomainBoundsEvent event) {
            DiagramTabController.this.currentOverviewBounds = event.getBounds();
        }

        public void onLoadImageData(LoadImageDataEvent event) {
            Double left = DiagramTabController.this.currentOverviewBounds.getLeft();
            Double right = DiagramTabController.this.currentOverviewBounds.getRight();
            Bounds bounds = new Bounds(left, right, null, null);
            EventBus.getMainEventBus().fireEvent(new SetDomainBoundsEvent(bounds));
            EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
        }

    }

    private class EESTabEventBroker extends ATabEventBroker implements
            TabSelectedEventHandler,
            ResizeEventHandler,
            TimeSeriesChangedEventHandler,
            RequestDataEventHandler,
            NewTimeSeriesEventHandler,
            InitEventHandler,
            DeleteTimeSeriesEventHandler,
            SetImageUrlEventHandler,
            SwitchAutoscaleEventHandler,
            ChangeImagePixelBoundsEventHandler,
            DatesChangedEventHandler,
            SwitchGridEventHandler,
            StoreTimeSeriesPropsEventHandler {

        public EESTabEventBroker() {
            EventBus.getMainEventBus().addHandler(TabSelectedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ResizeEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(InitEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(RequestDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DeleteTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(NewTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SetImageUrlEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SwitchAutoscaleEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ChangeImagePixelBoundsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(DatesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SwitchGridEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesPropsEvent.TYPE, this);
        }

        private void contributeToLegend() {
            if (isSelfSelectedTab()) {
                ArrayList<LegendElement> legendItems = new ArrayList<LegendElement>();
                TimeseriesLegendData[] timeSeries = TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted();
                for (int i = 0; i < timeSeries.length; i++) {
                    legendItems.add(timeSeries[i].getLegendElement());
                }
                fillLegend(legendItems);
            }
        }

        public void onSelected(TabSelectedEvent evt) {
            if (isSelfSelectedTab()) {
                contributeToLegend();
                DiagramTabController.this.getTab().setVisibleSlider(true);
            }
            else {
                DiagramTabController.this.getTab().setVisibleSlider(false);
            }
        }

        public void onResize(ResizeEvent evt) {
        	getTab().showLoadingSpinner();
            if ( Application.isHasStarted() && !evt.isSilent()) {
                EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
            }
        }

        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
            contributeToLegend();
            if (getTimeSeriesDataStore().getDataItems().isEmpty()) {
                DiagramTabController.this.tab.hideTooltips();
                DiagramTabController.this.getTab().removeSlider();
                getMainEventBus().fireEvent(new LoadImageDataEvent());
            }
            DiagramTabController.this.getTab().addSlider();
        }

        public void onRequestData(RequestDataEvent evt) {
            getMainEventBus().fireEvent(new LoadImageDataEvent());
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
        	getTab().showLoadingSpinner();
            if (getTimeSeriesDataStore().getDataItems().isEmpty()) {
                DiagramTabController.this.tab.hideTooltips();
            }
        }

        @Override
        protected boolean isSelfSelectedTab() {
            return View.getView().getCurrentTab().equals(DiagramTabController.this.getTab());
        }

        public void onNewTimeSeries(NewTimeSeriesEvent evt) {
        	getTab().showLoadingSpinner();
            if (isSelfSelectedTab()) {
                DiagramTabController.this.getTab().addSlider();
                /*
                 * automatically switch on zoom and pan functionality
                 */
                getMainEventBus().fireEvent(StateChangeEvent.createMove());
            }
        }

        public void onInit(InitEvent evt) {
            DiagramTabController.this.getTab().init();
        }

		@Override
		public void onSetImageUrl(SetImageUrlEvent event) {
			getTab().hideLoadingSpinner();
		}

		@Override
		public void onSwitch(SwitchAutoscaleEvent evt) {
			getTab().showLoadingSpinner();
		}

		@Override
		public void onSetImageBounds(ChangeImagePixelBoundsEvent event) {
			getTab().showLoadingSpinner();
		}

		@Override
		public void onDatesChanged(DatesChangedEvent evt) {
			getTab().showLoadingSpinner();
		}

		@Override
		public void onSwitch() {
			getTab().showLoadingSpinner();
		}

		@Override
		public void onStore(StoreTimeSeriesPropsEvent evt) {
			getTab().showLoadingSpinner();
		}
    }
}
