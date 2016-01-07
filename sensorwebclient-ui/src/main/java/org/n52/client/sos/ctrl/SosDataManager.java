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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.DataManager;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.ctrl.RequestFailedException;
import org.n52.client.sos.DataparsingException;
import org.n52.client.sos.event.AddMarkerEvent;
import org.n52.client.sos.event.data.NewPhenomenonsEvent;
import org.n52.client.sos.event.data.NewStationPositionsEvent;
import org.n52.client.sos.event.data.StorePhenomenaEvent;
import org.n52.client.sos.event.data.StoreSOSMetadataEvent;
import org.n52.client.sos.event.data.StoreStationsEvent;
import org.n52.client.sos.event.data.handler.StorePhenomenaEventHandler;
import org.n52.client.sos.event.data.handler.StoreSOSMetadataEventHandler;
import org.n52.client.sos.event.data.handler.StoreStationsEventHandler;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

import com.google.gwt.core.client.GWT;

public class SosDataManager implements DataManager<SOSMetadata> {

    private static SosDataManager instance;

    private Map<String, SOSMetadata> metadatas = new HashMap<String, SOSMetadata>();

    private SosDataManager() {
        new SOSEventBroker();
    }

    public static SosDataManager getDataManager() {
        if (instance == null) {
            instance = new SosDataManager();
        }
        return instance;
    }

    public boolean contains(String serviceURL) {
    	return metadatas.containsKey(serviceURL);
    }

    private class SOSEventBroker implements
            StoreSOSMetadataEventHandler,
            StorePhenomenaEventHandler,
            StoreStationsEventHandler {

        public SOSEventBroker() {
            EventBus.getMainEventBus().addHandler(StoreSOSMetadataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StorePhenomenaEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreStationsEvent.TYPE, this);
        }

        public void onStore(StoreSOSMetadataEvent evt) {
            storeData(evt.getMetadata().getServiceUrl(), evt.getMetadata());
        }

		public void onStore(StorePhenomenaEvent evt) {
			SOSMetadata meta = getServiceMetadata(evt.getSosURL());
			if (meta == null) {
				String reason = "Request failed for datamapping reasons.";
				RequestFailedException e = new RequestFailedException(reason);
				ExceptionHandler.handleUnexpectedException(e);
				return;
			}

			Set<String> phenomenonIds = new HashSet<String>();
			TimeseriesParametersLookup lookup = meta.getTimeseriesParametersLookup();
			for (Phenomenon phenomenon : evt.getPhenomenons()) {
				lookup.addPhenomenon(phenomenon);
				phenomenonIds.add(phenomenon.getPhenomenonId());
			}
			EventBus.getMainEventBus().fireEvent(new NewPhenomenonsEvent(meta.getServiceUrl(), phenomenonIds));
		}

		public void onStore(StoreStationsEvent evt) {
			SOSMetadata metadata = getServiceMetadata(evt.getSosURL());
			if (metadata == null) {
				String reason = "An unknown SERVICES instance was requested.";
				RequestFailedException e = new RequestFailedException(reason);
                ExceptionHandler.handleUnexpectedException(e);
				return;
			}

			try {
				ArrayList<Station> stations = new ArrayList<Station>();
				for (Station station : evt.getStations()) {
					if (station == null) {
						GWT.log("StoreProcedurePositionsEvent contained a 'null' station.");
						continue; // cannot throw IllegalStateException at
									// client side
					}
					Station local = metadata.getStationByFeature(station.getFeature());
					if (local == null) {
						// means we don not have data on that station in the
						// client metadata
						stations.add(station);
						metadata.addStation(station);
					}
					stations.add(station);
				}
				EventBus.getMainEventBus().fireEvent(new NewStationPositionsEvent());
				EventBus.getMainEventBus().fireEvent(new AddMarkerEvent(stations));
			} catch (Exception e) {
				ExceptionHandler.handleUnexpectedException(new DataparsingException("Failed to load positions", e));
			}
		}
	}

	public void storeData(String id, SOSMetadata data) {
		metadatas.put(id, data);
	}

	public SOSMetadata getServiceMetadata(String id) {
		return metadatas.get(id);
	}

	public Collection<SOSMetadata> getServiceMetadatas() {
		return metadatas.values();
	}

}
