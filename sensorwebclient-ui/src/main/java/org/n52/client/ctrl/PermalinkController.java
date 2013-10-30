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

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreOfferingEvent;
import org.n52.client.sos.event.data.StorePhenomenaEvent;
import org.n52.client.sos.event.data.StoreProcedureEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreOfferingEventHandler;
import org.n52.client.sos.event.data.handler.StorePhenomenaEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureEventHandler;
import org.n52.client.sos.event.data.handler.StoreStationEventHandler;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class PermalinkController {
    
    private class LoadingStatus {
        private SosTimeseries timeseriesToLoad;
        private TimeseriesRenderingOptions options;
        private LoadingStatus(SosTimeseries timeseriesToLoad, TimeseriesRenderingOptions options) {
            this.timeseriesToLoad = timeseriesToLoad;
            this.options = options;
        }
    }
	
    private List<LoadingStatus> timeseriesStatusList;
	
	public PermalinkController() {
        new PermalinkControllerEventBroker();
	    timeseriesStatusList = new ArrayList<PermalinkController.LoadingStatus>();
	}
	
	public void addTimeseries(SosTimeseries timeseries) {
        timeseriesStatusList.add(new LoadingStatus(timeseries, null));
    }
	
	public void addTimeseries(SosTimeseries timeseries, TimeseriesRenderingOptions options) {
	    timeseriesStatusList.add(new LoadingStatus(timeseries, options));
	}
	
	private class PermalinkControllerEventBroker implements
			StoreOfferingEventHandler,
			StorePhenomenaEventHandler,
			StoreProcedureEventHandler,
			StoreFeatureEventHandler,
			StoreStationEventHandler {
		
		public PermalinkControllerEventBroker() {
			getMainEventBus().addHandler(StoreOfferingEvent.TYPE, this);
            getMainEventBus().addHandler(StorePhenomenaEvent.TYPE, this);
			getMainEventBus().addHandler(StoreProcedureEvent.TYPE, this);
			getMainEventBus().addHandler(StoreFeatureEvent.TYPE, this);
			getMainEventBus().addHandler(StoreStationEvent.TYPE, this);
		}

		@Override
		public void onStore(StoreFeatureEvent evt) {
			check();
		}
		
		@Override
        public void onStore(StorePhenomenaEvent evt) {
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
	    Iterator<LoadingStatus> iterator = timeseriesStatusList.iterator();
	    while (iterator.hasNext()) {
	        LoadingStatus loadingStatus = iterator.next();
	        SosTimeseries timeseries = loadingStatus.timeseriesToLoad;
            SOSMetadata metadata = getServiceMetadata(timeseries);
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            if (lookup.hasLoadedCompletely(timeseries)) {
                Station station = metadata.getStationByTimeSeries(timeseries);
                NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder()
                        .addStation(station)
                        .addTimeseries(timeseries)
                        .addRenderingOptions(loadingStatus.options)
                        .build();
                getMainEventBus().fireEvent(event);
                iterator.remove();
            }
        }
	}

    private SOSMetadata getServiceMetadata(SosTimeseries timeseries) {
        String serviceUrl = timeseries.getServiceUrl();
        return getDataManager().getServiceMetadata(serviceUrl);
    }

}
