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
