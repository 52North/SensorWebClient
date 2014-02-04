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
package org.n52.client.ui.legend;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.Collection;
import java.util.HashMap;

import org.n52.client.bus.EventBus;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.LegendElementSelectedEvent;
import org.n52.client.sos.event.data.DeleteTimeSeriesEvent;
import org.n52.client.sos.event.data.ExportEvent;
import org.n52.client.sos.event.data.ExportEvent.ExportType;
import org.n52.client.sos.event.data.ExportFinishedEvent;
import org.n52.client.sos.event.data.FinishedLoadingTimeSeriesEvent;
import org.n52.client.sos.event.data.handler.DeleteTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.ExportFinishedEventHandler;
import org.n52.client.sos.event.data.handler.FinishedLoadingTimeSeriesEventHandler;
import org.n52.client.sos.event.handler.LegendElementSelectedEventHandler;
import org.n52.client.sos.legend.TimeseriesLegendData;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

/**
 * Controls data/interaction flow between legend, its entries and user's client
 * interaction.
 */
public class LegendController {

	private Legend legend;

	public LegendController(Legend legend) {
		this.legend = legend;
		new LegendControllerEventBroker(this);
	}

	private class LegendControllerEventBroker implements 
				LegendElementSelectedEventHandler, 
				FinishedLoadingTimeSeriesEventHandler,
				ExportFinishedEventHandler,
				DeleteTimeSeriesEventHandler {

		private LegendController controller;

		LegendControllerEventBroker(LegendController legendController) {
			this.controller = legendController;
			EventBus eventBus = EventBus.getMainEventBus();
			eventBus.addHandler(LegendElementSelectedEvent.TYPE, this);
			eventBus.addHandler(FinishedLoadingTimeSeriesEvent.TYPE, this);
			eventBus.addHandler(ExportFinishedEvent.TYPE, this);
			eventBus.addHandler(DeleteTimeSeriesEvent.TYPE, this);
		}

		public void onSelected(LegendElementSelectedEvent evt) {
			controller.setSelectedElement(evt.getElement());
		}

		@Override
		public void onFinishedLoadingTimeSeries(
				FinishedLoadingTimeSeriesEvent evt) {
			legend.stopExportLoadingSpinner();
			legend.setExportButtonActiv(true);
		}

		@Override
		public void onExportFinished(ExportFinishedEvent evt) {
			legend.stopExportLoadingSpinner();
		}

		@Override
		public void onDeleteTimeSeries(DeleteTimeSeriesEvent evt) {
			TimeseriesDataStore dataStore = TimeseriesDataStore.getTimeSeriesDataStore();
			TimeseriesLegendData[] timeseries = dataStore.getTimeSeriesSorted();
			if (timeseries.length <= 1) {
				legend.setExportButtonActiv(false);
			}
		}

	}

	public void setSelectedElement(LegendElement element) {
		legend.setSelectedElement(element);
	}

	void exportTo(ExportType exportType) {
		TimeseriesDataStore dataStore = TimeseriesDataStore.getTimeSeriesDataStore();
		HashMap<String, TimeseriesLegendData> dataItems = dataStore.getDataItems();
		if (!dataItems.isEmpty()) {
			legend.startExportLoadingSpinner();
            Collection<TimeseriesLegendData> values = dataItems.values();
			ExportEvent event = new ExportEvent(values, exportType);
			EventBus.getMainEventBus().fireEvent(event);
        }
	}
	
	void confirmBeforeReload(final String url) {
		SC.ask(i18n.changeLanguage(), new BooleanCallback() {
            public void execute(Boolean value) {
                if (value) {
                    Window.Location.assign(url);
                }
            }
        });
	}
}
