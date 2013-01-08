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

import org.n52.client.bus.EventBus;
import org.n52.client.model.data.dataManagers.DataManagerSosImpl;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreOfferingEvent;
import org.n52.client.sos.event.data.StoreProcedureEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreOfferingEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureEventHandler;
import org.n52.client.sos.event.data.handler.StoreStationEventHandler;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;

public class PermaLinkController {
	
	private String url;
	
	private String offering;
	
	private String procedure;
	
	private String phenomenon;
	
	private String foi;
	
	private boolean offeringReady = false;
	
	private boolean procedureReady = false;
	
	private boolean featureReady = false;
	
	private boolean stationReady = false;
	
	private boolean permalinkLoaded = false;
	
	public PermaLinkController(String url, String offering, String procedure, String phenomenon, String foi) {
		new PermalinkControllerEventBroker();
		this.url = url;
		this.offering = offering;
		this.procedure = procedure;
		this.phenomenon = phenomenon;
		this.foi = foi;
	}
	
	private class PermalinkControllerEventBroker implements
			StoreOfferingEventHandler,
			StoreProcedureEventHandler,
			StoreFeatureEventHandler,
			StoreStationEventHandler {
		
		public PermalinkControllerEventBroker() {
			EventBus.getMainEventBus().addHandler(StoreOfferingEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(StoreProcedureEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(StoreFeatureEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(StoreStationEvent.TYPE, this);
		}

		@Override
		public void onStore(StoreFeatureEvent evt) {
			featureReady = true;
			check();
		}

		@Override
		public void onStore(StoreProcedureEvent evt) {
			procedureReady = true;
			check();
		}

		@Override
		public void onStore(StoreOfferingEvent evt) {
			offeringReady = true;
			check();
		}

		@Override
		public void onStore(StoreStationEvent evt) {
			stationReady = true;
			check();
		}
	}

	public void check() {
		if (!permalinkLoaded && featureReady && procedureReady && offeringReady && stationReady) {
            SOSMetadata metadata = DataManagerSosImpl.getInst().getServiceMetadata(url);        
            Phenomenon phen = metadata.getPhenomenon(phenomenon);
            FeatureOfInterest f = metadata.getFeature(foi);
            Offering o = metadata.getOffering(offering);
            Procedure p = metadata.getProcedure(procedure);
            Station station = metadata.getStation(offering, foi, procedure, phenomenon);
            
            Toaster.getInstance().addMessage("load session from permalink");
            permalinkLoaded = true;
            
            NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder(url)
            		.addStation(station)
            		.addOffering(o)
    				.addFOI(f)
    				.addProcedure(p)
    				.addPhenomenon(phen)
    				.build();
            EventBus.getMainEventBus().fireEvent(event);
		}
	}
}
