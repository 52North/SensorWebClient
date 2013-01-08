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
import org.n52.client.ctrl.Application;
import org.n52.client.ctrl.Controller;
import org.n52.client.eventBus.EventBus;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
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
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.sos.ui.EESTab;
import org.n52.client.ui.View;
import org.n52.client.view.gui.elements.ATabEventBroker;
import org.n52.client.view.gui.elements.legend.LegendElement;

import com.smartgwt.client.types.Visibility;

public class EESTabController extends Controller<EESTab> {

    public Bounds currentOverviewBounds;

    public EESTabController(EESTab tab) {
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
            EESTabController.this.currentOverviewBounds = event.getBounds();
        }

        public void onLoadImageData(LoadImageDataEvent event) {
            Double left = EESTabController.this.currentOverviewBounds.getLeft();
            Double right = EESTabController.this.currentOverviewBounds.getRight();
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
                TimeSeries[] timeSeries = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
                for (int i = 0; i < timeSeries.length; i++) {
                    legendItems.add(timeSeries[i].getLegendElement());
                }
                fillLegend(legendItems);
            }
        }

        public void onSelected(TabSelectedEvent evt) {
            if (isSelfSelectedTab()) {
                contributeToLegend();
                EESTabController.this.getTab().setVisibleSlider(true);
            }
            else {
                EESTabController.this.getTab().setVisibleSlider(false);
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
            if (DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                EESTabController.this.tab.hideTooltips();
                EESTabController.this.getTab().removeSlider();
                EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
            }
            EESTabController.this.getTab().addSlider();
        }

        public void onRequestData(RequestDataEvent evt) {
            EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
        }

        public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
        	getTab().showLoadingSpinner();
            if (DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                EESTabController.this.tab.hideTooltips();
            }
        }

        @Override
        protected boolean isSelfSelectedTab() {
            return View.getInstance().getCurrentTab().equals(EESTabController.this.getTab());
        }

        public void onNewTimeSeries(NewTimeSeriesEvent evt) {
        	getTab().showLoadingSpinner();
            if (isSelfSelectedTab()) {
                EESTabController.this.getTab().addSlider(); // TODO check, if onUpdate is enough
                /*
                 * automatically switch on zoom and pan functionality
                 */
                EventBus.getMainEventBus().fireEvent(StateChangeEvent.createMove());
                EventBus.getMainEventBus().fireEvent(StateChangeEvent.createMove());
            }
        }

        /* (non-Javadoc)
         * @see org.n52.client.eventBus.events.handler.InitEventHandler#onInit(org.n52.client.eventBus.events.InitEvent)
         */
        public void onInit(InitEvent evt) {
            EESTabController.this.getTab().init();
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
