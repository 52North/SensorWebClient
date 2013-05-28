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
package org.n52.client.ui.legend;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.Collection;
import java.util.HashMap;

import org.n52.client.bus.EventBus;
import org.n52.client.sos.data.DataStoreTimeSeriesImpl;
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
import org.n52.client.sos.legend.TimeSeries;

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
			DataStoreTimeSeriesImpl dataStore = DataStoreTimeSeriesImpl.getInst();
			TimeSeries[] timeseries = dataStore.getTimeSeriesSorted();
			if (timeseries.length <= 1) {
				legend.setExportButtonActiv(false);
			}
		}

	}

	public void setSelectedElement(LegendElement element) {
		legend.setSelectedElement(element);
	}

	void exportTo(ExportType exportType) {
		DataStoreTimeSeriesImpl dataStore = DataStoreTimeSeriesImpl.getInst();
		HashMap<String, TimeSeries> dataItems = dataStore.getDataItems();
		if (!dataItems.isEmpty()) {
			legend.startExportLoadingSpinner();
            Collection<TimeSeries> values = dataItems.values();
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
