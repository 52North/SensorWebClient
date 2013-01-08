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
import java.util.Collection;
import java.util.HashMap;
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

import com.google.gwt.core.client.GWT;

public class DataManagerSosImpl implements DataManager<SOSMetadata> {

    private static DataManagerSosImpl instance;

    private Map<String, SOSMetadata> metadatas = new HashMap<String, SOSMetadata>();
    
    private DataManagerSosImpl() {
        new SOSEventBroker();
    }

    public static DataManagerSosImpl getInst() {
        if (instance == null) {
            instance = new DataManagerSosImpl();
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
            storeData(evt.getMetadata().getId(), evt.getMetadata());
        }

		public void onStore(StorePhenomenaEvent evt) {
			SOSMetadata meta = getServiceMetadata(evt.getSosURL());
			if (meta == null) {
				String reason = "Request failed for datamapping reasons.";
				RequestFailedException e = new RequestFailedException(reason);
				ExceptionHandler.handleUnexpectedException(e);
				return;
			}

			Set<String> phenomenonIds = evt.getPhenomena().keySet();
			for (String phenomenonId : phenomenonIds) {
				meta.addPhenomenon(new Phenomenon(phenomenonId));
			}
			EventBus.getMainEventBus().fireEvent(new NewPhenomenonsEvent(meta.getId(), phenomenonIds));
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
				metadata.setSrs(evt.getSrs());
				for (Station station : evt.getStations()) {
					if (station == null) {
						GWT.log("StoreProcedurePositionsEvent contained a 'null' station.");
						continue; // cannot throw IllegalStateException at
									// client side
					}
					Station local = metadata.getStation(station.getId());
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
