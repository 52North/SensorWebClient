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
package org.n52.client.ctrl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;

import java.util.HashMap;
import java.util.Map;

import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreOfferingEvent;
import org.n52.client.sos.event.data.StoreProcedureEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreOfferingEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureEventHandler;
import org.n52.client.sos.event.data.handler.StoreStationEventHandler;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class PermalinkController {
	
	private Map<SosTimeseries, Boolean> timeseriesToLoad;
	
	public PermalinkController() {
		new PermalinkControllerEventBroker();
		this.timeseriesToLoad = new HashMap<SosTimeseries, Boolean>();
	}
	
	public void addTimeseries(SosTimeseries timeseries) {
	    this.timeseriesToLoad.put(timeseries, FALSE);
	}
	
	private class PermalinkControllerEventBroker implements
			StoreOfferingEventHandler,
			StoreProcedureEventHandler,
			StoreFeatureEventHandler,
			StoreStationEventHandler {
		
		public PermalinkControllerEventBroker() {
			getMainEventBus().addHandler(StoreOfferingEvent.TYPE, this);
			getMainEventBus().addHandler(StoreProcedureEvent.TYPE, this);
			getMainEventBus().addHandler(StoreFeatureEvent.TYPE, this);
			getMainEventBus().addHandler(StoreStationEvent.TYPE, this);
		}

		@Override
		public void onStore(StoreFeatureEvent evt) {
			check();
		}

		@Override
		public void onStore(StoreProcedureEvent evt) {
			check();
		}

		@Override
		public void onStore(StoreOfferingEvent evt) {
			check();
		}

		@Override
		public void onStore(StoreStationEvent evt) {
			check();
		}
	}

	public void check() {
	    for (SosTimeseries timeseries : PermalinkController.this.timeseriesToLoad.keySet()) {
            String serviceUrl = timeseries.getServiceUrl();
            SOSMetadata metadata = getDataManager().getServiceMetadata(serviceUrl);
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParamtersLookup();
            if (lookup.hasLoadedCompletely(timeseries) && !isNewTimeseriesEventAlreadyFired(timeseries)) {
                Station station = metadata.getStationByTimeSeries(timeseries);
                PermalinkController.this.timeseriesToLoad.put(timeseries, TRUE);
                
                NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder(serviceUrl)
                        .addStation(station)
                        .addTimeseries(timeseries)
                        .build();
                getMainEventBus().fireEvent(event);
            }
        }
	}

    private boolean isNewTimeseriesEventAlreadyFired(SosTimeseries timeseries) {
        return PermalinkController.this.timeseriesToLoad.get(timeseries);
    }
}
