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

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEventHandler;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEventHandler;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ServiceController;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.data.ExportEvent;
import org.n52.client.sos.event.data.FinishedLoadingTimeSeriesEvent;
import org.n52.client.sos.event.data.GetFeatureEvent;
import org.n52.client.sos.event.data.GetOfferingEvent;
import org.n52.client.sos.event.data.GetPhenomenonsEvent;
import org.n52.client.sos.event.data.GetProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.GetProcedureEvent;
import org.n52.client.sos.event.data.GetStationForTimeseriesEvent;
import org.n52.client.sos.event.data.GetStationsWithinBBoxEvent;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.RequestSensorDataEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreOfferingEvent;
import org.n52.client.sos.event.data.StoreProcedureEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.UpdateSOSMetadataEvent;
import org.n52.client.sos.event.data.handler.ExportEventHandler;
import org.n52.client.sos.event.data.handler.FinishedLoadingTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.GetFeatureEventHandler;
import org.n52.client.sos.event.data.handler.GetOfferingEventHandler;
import org.n52.client.sos.event.data.handler.GetPhenomenonsEventHandler;
import org.n52.client.sos.event.data.handler.GetProcedureDetailsUrlEventHandler;
import org.n52.client.sos.event.data.handler.GetProcedureEventHandler;
import org.n52.client.sos.event.data.handler.GetStationForTimeseriesEventHandler;
import org.n52.client.sos.event.data.handler.GetStationsWithinBBoxEventHandler;
import org.n52.client.sos.event.data.handler.NewTimeSeriesEventHandler;
import org.n52.client.sos.event.data.handler.RequestSensorDataEventHandler;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreOfferingEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureEventHandler;
import org.n52.client.sos.event.data.handler.StoreStationEventHandler;
import org.n52.client.sos.event.data.handler.UpdateSOSMetadataEventHandler;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

import com.google.gwt.core.client.GWT;

public class SOSController extends ServiceController {

    protected boolean isAddingNewTimeSeries;
    
    public static boolean isDeletingTS;

    public SOSController() {
        new SosControllerEventBroker();
    }

    SOSRequestManager getRequestManager() {
        return SOSRequestManager.getInstance();
    }

    private class SosControllerEventBroker implements
            NewTimeSeriesEventHandler,
            LoadImageDataEventHandler,
            RequestSensorDataEventHandler,
            GetPhenomenonsEventHandler,
            GetStationsWithinBBoxEventHandler,
            GetStationForTimeseriesEventHandler,
            GetProcedureDetailsUrlEventHandler,
            GetProcedureEventHandler,
            StoreProcedureEventHandler,
            GetOfferingEventHandler,
            StoreOfferingEventHandler,
            GetFeatureEventHandler,
            StoreFeatureEventHandler,
            StoreStationEventHandler,
            ExportEventHandler,
            FinishedLoadingTimeSeriesEventHandler,
            SetOverviewDomainBoundsEventHandler,
            SetDomainBoundsEventHandler,
            UpdateSOSMetadataEventHandler {

        public SosControllerEventBroker() {
            EventBus.getMainEventBus().addHandler(NewTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(LoadImageDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(RequestSensorDataEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetPhenomenonsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetProcedureDetailsUrlEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetProcedureEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreProcedureEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetOfferingEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreOfferingEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetFeatureEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreFeatureEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetStationsWithinBBoxEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(GetStationForTimeseriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreStationEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(ExportEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(FinishedLoadingTimeSeriesEvent.TYPE, this);
            EventBus.getOverviewChartEventBus().addHandler(SetOverviewDomainBoundsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(SetDomainBoundsEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(UpdateSOSMetadataEvent.TYPE, this);
        }

        public void onNewTimeSeries(NewTimeSeriesEvent evt) {
            try {
                GWT.log("load new timeseries: " + evt.getTimeseries());
                SOSController.this.isAddingNewTimeSeries = true;
                getRequestManager().requestSensorMetadata(evt);
            }
            catch (Exception e1) {
                GWT.log("Could not request timeseries.", e1);
            }
        }

        public void onLoadImageData(LoadImageDataEvent event) {
           getRequestManager().requestDiagram();
        }

        public void onRequest(RequestSensorDataEvent evt) {
            TimeseriesLegendData[] sortedTimeSeries = TimeseriesDataStore.getTimeSeriesDataStore().getTimeSeriesSorted();
            String id = evt.getID();
            if (id != null) {
            	getRequestManager().requestSensorData(sortedTimeSeries, id);
            }
            else {
            	getRequestManager().requestSensorData(sortedTimeSeries);
            }
        }

        public void onGetPhenomena(GetPhenomenonsEvent evt) {
            if (evt.getSosURL() != null) {
            	getRequestManager().requestPhenomenons(evt.getSosURL());
            }
        }

        public void onGetStations(GetStationsWithinBBoxEvent evt) {
        	getRequestManager().requestStations(evt.getSOSURL(), evt.getBBox());
        }

        public void onExport(ExportEvent evt) {
            switch (evt.getType()) {
            case PDF:
            	getRequestManager().requestExportPDF(evt.getTimeseries());
                break;
            case XLS:
            	getRequestManager().requestExportXLS(evt.getTimeseries());
                break;
            case CSV:
            	getRequestManager().requestExportCSV(evt.getTimeseries());
                break;
            case PD_ZIP:
            	getRequestManager().requestExportPDFzip(evt.getTimeseries());
                break;
            case XLS_ZIP:
            	getRequestManager().requestExportXLSzip(evt.getTimeseries());
                break;
            case CSV_ZIP:
            	getRequestManager().requestExportCSVzip(evt.getTimeseries());
                break;
            case PDF_ALL_IN_ONE:
            	getRequestManager().requestExportPDFallInOne(evt.getTimeseries());
                break;
            default:
                break;
            }

        }

        public void onFinishedLoadingTimeSeries(FinishedLoadingTimeSeriesEvent evt) {
            if (SOSController.this.isAddingNewTimeSeries == true) {
                SOSController.this.isAddingNewTimeSeries = false;
            }
        }

		@Override
		public void onGetProcedureDetailsUrl(GetProcedureDetailsUrlEvent evt) {
			getRequestManager().requestProcedureDetailsUrl(evt.getServiceURL(), evt.getProcedure());
		}

		@Override
		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			isDeletingTS = false;
		}

		@Override
		public void onSetOverviewDomainBounds(SetOverviewDomainBoundsEvent event) {
			isDeletingTS = false;
		}

		@Override
		public void onGetProcedure(GetProcedureEvent evt) {
			getRequestManager().requestProcedure(evt.getServiceURL(), evt.getProcedureID());
		}

		@Override
		public void onGetOffering(GetOfferingEvent evt) {
			getRequestManager().requestOffering(evt.getServiceURL(), evt.getOfferingID());
		}

        @Override
        public void onStore(StoreProcedureEvent evt) {
            TimeseriesParametersLookup lookup = getParametersLookup(evt.getServiceURL());
            lookup.addProcedure(evt.getProcedure());
        }

		@Override
		public void onStore(StoreOfferingEvent evt) {
            TimeseriesParametersLookup lookup = getParametersLookup(evt.getServiceURL());
			lookup.addOffering(evt.getOffering());
		}

        @Override
        public void onStore(StoreFeatureEvent evt) {
            TimeseriesParametersLookup lookup = getParametersLookup(evt.getServiceURL());
            lookup.addFeature(evt.getFeature());
        }

        private TimeseriesParametersLookup getParametersLookup(String serviceUrl) {
            SosDataManager dataManager = SosDataManager.getDataManager();
            SOSMetadata serviceMetadata = dataManager.getServiceMetadata(serviceUrl);
            return serviceMetadata.getTimeseriesParametersLookup();
        }

		@Override
		public void onGetFeature(GetFeatureEvent evt) {
			getRequestManager().requestFeature(evt.getServiceURL(), evt.getFeatureID());
		}

		@Override
		public void onGetStation(GetStationForTimeseriesEvent evt) {
			getRequestManager().requestStationWith(evt.getTimseries());
		}

		@Override
		public void onStore(StoreStationEvent evt) {
			SOSMetadata serviceMetadata = SosDataManager.getDataManager().getServiceMetadata(evt.getServiceURL());
			serviceMetadata.addStation(evt.getStation());
		}

		@Override
		public void onUpdate(UpdateSOSMetadataEvent evt) {
			getRequestManager().requestUpdateSOSMetadata();
		}
    }
}
