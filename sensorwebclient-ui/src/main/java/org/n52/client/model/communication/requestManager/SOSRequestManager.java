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

package org.n52.client.model.communication.requestManager;

import static org.n52.client.sos.i18n.I18NStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eesgmbh.gimv.client.event.SetDataAreaPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetImageEntitiesEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEvent;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetOverviewDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.n52.client.control.Application;
import org.n52.client.control.PropertiesManager;
import org.n52.client.control.ServiceController;
import org.n52.client.eventBus.EventBus;
import org.n52.client.model.communication.requestManager.callbacks.EESDataCallback;
import org.n52.client.model.communication.requestManager.callbacks.FileCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetFeatureCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetOfferingCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetPhenomenaCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetPositionsCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetProcedureCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetProcedureDetailsUrlCallback;
import org.n52.client.model.communication.requestManager.callbacks.GetStationCallback;
import org.n52.client.model.communication.requestManager.callbacks.SensorMetadataCallback;
import org.n52.client.model.communication.requestManager.callbacks.TimeSeriesDataCallback;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
import org.n52.client.model.data.dataManagers.DataManagerSosImpl;
import org.n52.client.model.data.dataManagers.TimeManager;
import org.n52.client.sos.event.DeleteMarkersEvent;
import org.n52.client.sos.event.LegendElementSelectedEvent;
import org.n52.client.sos.event.data.FinishedLoadingTimeSeriesEvent;
import org.n52.client.sos.event.data.FirstValueOfTimeSeriesEvent;
import org.n52.client.sos.event.data.GetProcedurePositionsFinishedEvent;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.StoreAxisDataEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreOfferingEvent;
import org.n52.client.sos.event.data.StorePhenomenaEvent;
import org.n52.client.sos.event.data.StoreProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.StoreProcedureEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.StoreStationsEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesLastValueEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesPropsEvent;
import org.n52.client.sos.event.data.TimeSeriesHasDataEvent;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.util.exceptions.ExceptionHandler;
import org.n52.client.util.exceptions.RequestFailedException;
import org.n52.client.view.View;
import org.n52.client.view.gui.elements.legend.LegendElement;
import org.n52.client.view.gui.elements.tabImpl.EESTab;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.shared.exceptions.CompatibilityException;
import org.n52.shared.exceptions.ServerException;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.requests.EESDataRequest;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.EESDataResponse;
import org.n52.shared.responses.GetFeatureResponse;
import org.n52.shared.responses.GetOfferingResponse;
import org.n52.shared.responses.GetPhenomenonResponse;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.GetProcedureResponse;
import org.n52.shared.responses.GetStationResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.responses.StationPositionsResponse;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.service.rpc.RpcEESDataService;
import org.n52.shared.service.rpc.RpcEESDataServiceAsync;
import org.n52.shared.service.rpc.RpcFileDataService;
import org.n52.shared.service.rpc.RpcFileDataServiceAsync;
import org.n52.shared.service.rpc.RpcSensorMetadataService;
import org.n52.shared.service.rpc.RpcSensorMetadataServiceAsync;
import org.n52.shared.service.rpc.RpcServiceMetadataService;
import org.n52.shared.service.rpc.RpcServiceMetadataServiceAsync;
import org.n52.shared.service.rpc.RpcStationPositionsService;
import org.n52.shared.service.rpc.RpcStationPositionsServiceAsync;
import org.n52.shared.service.rpc.RpcTimeSeriesDataService;
import org.n52.shared.service.rpc.RpcTimeSeriesDataServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SOSRequestManager extends RequestManager {
	
	private static SOSRequestManager instance;

    private RpcTimeSeriesDataServiceAsync timeSeriesDataService;

    private RpcSensorMetadataServiceAsync sensorMetadataService;

    private RpcStationPositionsServiceAsync stationPositionService;

    private RpcServiceMetadataServiceAsync serviceMetadataService;

    private RpcEESDataServiceAsync eesDataService;

    private RpcFileDataServiceAsync fileDataService;

    public static SOSRequestManager getInstance() {
    	if (instance == null) {
			instance = new SOSRequestManager();
		}
    	return instance;
    }
    
    private SOSRequestManager() {
        createRpcServices();
    }

    private void createRpcServices() {
        this.serviceMetadataService = GWT.create(RpcServiceMetadataService.class);
        this.stationPositionService = GWT.create(RpcStationPositionsService.class);
        this.sensorMetadataService = GWT.create(RpcSensorMetadataService.class);
        this.timeSeriesDataService = GWT.create(RpcTimeSeriesDataService.class);
        this.fileDataService = GWT.create(RpcFileDataService.class);
        this.eesDataService = GWT.create(RpcEESDataService.class);
    }

    private void getSensorData(TimeSeriesProperties properties, final boolean requestSensorData) throws Exception {
        SensorMetadataCallback callback = new SensorMetadataCallback(this, "Could not get sensor data.") {
            @Override
            public void onSuccess(SensorMetadataResponse result) {
                removeRequest();
                try {
                    String tsID = result.getProps().getTsID();
                    DataStoreTimeSeriesImpl dataManager = DataStoreTimeSeriesImpl.getInst();
                    LegendElement legendElement = dataManager.getDataItem(tsID).getLegendElement();
                    EventBus.getMainEventBus().fireEvent(new StoreTimeSeriesPropsEvent(tsID, result.getProps()));
                    EventBus.getMainEventBus().fireEvent(new LegendElementSelectedEvent(legendElement, true));
                    if (requestSensorData) {
                        EventBus.getMainEventBus().fireEvent(new RequestDataEvent(tsID));
                    }
                    EventBus.getMainEventBus().fireEvent(new FinishedLoadingTimeSeriesEvent());
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
            }
        };
        addRequest();
        this.sensorMetadataService.getSensorMetadata(properties, callback);
    }

    public void requestSensorMetadata(NewTimeSeriesEvent evt) throws Exception {
        int width = evt.getWidth();
        int height = evt.getHeight();
        String url = evt.getSos();
        
        Station station = evt.getStation();
        Offering offering = evt.getOffering();
        FeatureOfInterest foi = evt.getFeature();
        Procedure procedure = evt.getProcedure();
        Phenomenon phenomenon = evt.getPhenomenon();

        TimeSeriesProperties props = new TimeSeriesProperties(url, station, offering, foi, procedure, phenomenon, width, height);
        TimeSeries timeSeries = new TimeSeries("TS_" + System.currentTimeMillis(), props);

        try {
            EventBus.getMainEventBus().fireEvent(new StoreTimeSeriesEvent(timeSeries));
        }
        catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
        finally {
            try {
                requestLastValueOf(timeSeries);
                requestFirstValueOf(timeSeries);
                getSensorData(props, evt.requestSensordata());
            }
            catch (Exception e) {
                ExceptionHandler.handleException(new RequestFailedException("Server did not respond!", e));
            }
        }
    }

    public void requestFirstValueOf(TimeSeries timeSeries) {
        try {
            ArrayList<TimeSeriesProperties> series = new ArrayList<TimeSeriesProperties>();
            series.add(timeSeries.getProperties());

            String firstValue = ServiceController.SOS_PARAM_FIRST;
            boolean grid = DataStoreTimeSeriesImpl.getInst().isGridEnabled();
            long begin = TimeManager.getInst().getBegin();
            long end = TimeManager.getInst().getEnd();
            DesignOptions options = new DesignOptions(series, begin, end, firstValue, grid);
            requestFirstValueFromTimeSeries(new TimeSeriesDataRequest(options), timeSeries);
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
        }
    }

    public void requestLastValueOf(TimeSeries timeSeries) {
        try {
            ArrayList<TimeSeriesProperties> series = new ArrayList<TimeSeriesProperties>();
            series.add(timeSeries.getProperties());
            String lastValue = ServiceController.SOS_PARAM_LAST;
            boolean grid = DataStoreTimeSeriesImpl.getInst().isGridEnabled();
            long begin = TimeManager.getInst().getBegin();
            long end = TimeManager.getInst().getEnd();
            DesignOptions options = new DesignOptions(series, begin, end, lastValue, grid);
            requestLastTimeSeriesData(new TimeSeriesDataRequest(options), timeSeries);
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
        }
    }

    public void requestSensorData(TimeSeries[] timeSeries, String id) {
        try {
            ArrayList<TimeSeriesProperties> series = new ArrayList<TimeSeriesProperties>();
            for (TimeSeries timeSerie : timeSeries) {
                if (timeSerie.getId().equals(id)) {
                    timeSerie.getProperties().setHeight(View.getInstance().getDataPanelHeight());
                    timeSerie.getProperties().setWidth(View.getInstance().getDataPanelWidth());
                    series.add(timeSerie.getProperties());
                    break;
                }
            }
            boolean grid = DataStoreTimeSeriesImpl.getInst().isGridEnabled();
            long begin = TimeManager.getInst().getBegin();
            long end = TimeManager.getInst().getEnd();
            DesignOptions options = new DesignOptions(series, begin, end, grid);
            getTimeSeriesData(new TimeSeriesDataRequest(options));
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
        }
    }

    private void requestFirstValueFromTimeSeries(TimeSeriesDataRequest request, final TimeSeries timeSeries) throws Exception {
        final long startTimeOfRequest = System.currentTimeMillis();
        addRequest();

        AsyncCallback<TimeSeriesDataResponse> callback = new AsyncCallback<TimeSeriesDataResponse>() {

            public void onFailure(Throwable caught) {
                removeRequest();
                Application.setHasStarted(true);
                ExceptionHandler.handleException(new CompatibilityException("Could not get first time series value",
                                                                            caught));
            }

            public void onSuccess(TimeSeriesDataResponse response) {
                removeRequest(System.currentTimeMillis() - startTimeOfRequest);
                HashMap<String, HashMap<Long, String>> payloadData = response.getPayloadData();
                try {
                    if (payloadData.isEmpty()) {
                        return;
                    }
                    String id = timeSeries.getId();
                    HashMap<Long, String> timeSeriesData = payloadData.get(id);
                    if (timeSeriesData.keySet().iterator().hasNext()) {
                        long timestamp = timeSeriesData.keySet().iterator().next().longValue();
                        String firstValue = timeSeriesData.get(timestamp).toString();
                        FirstValueOfTimeSeriesEvent event = new FirstValueOfTimeSeriesEvent(timestamp, firstValue, id);
                        EventBus.getMainEventBus().fireEvent(event);
                    }
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
                finally {
                    Application.setHasStarted(true);
                }
            }
        };
        this.timeSeriesDataService.getTimeSeriesData(request, callback);
    }

    private void requestLastTimeSeriesData(TimeSeriesDataRequest request, final TimeSeries timeSeries) throws Exception {
        final long startRequest = System.currentTimeMillis();
        addRequest();

        AsyncCallback<TimeSeriesDataResponse> callback = new AsyncCallback<TimeSeriesDataResponse>() {
            public void onFailure(Throwable caught) {
                removeRequest();
                Application.setHasStarted(true);
                ExceptionHandler.handleException(new CompatibilityException("Could not get last time series value.",
                                                                            caught));
            }

            public void onSuccess(TimeSeriesDataResponse response) {
                removeRequest(System.currentTimeMillis() - startRequest);
                HashMap<String, HashMap<Long, String>> payloadData = response.getPayloadData();

                try {
                    if (payloadData.isEmpty()) {
                        return; // nothing returned from server
                    }

                    String id = timeSeries.getId();
                    HashMap<Long, String> timeSeriesData = payloadData.get(id);
                    if (timeSeriesData.keySet().iterator().hasNext()) {
                        long date = timeSeriesData.keySet().iterator().next().longValue();
                        String lastValue = timeSeriesData.get(date).toString();
                        EventBus.getMainEventBus().fireEvent(new StoreTimeSeriesLastValueEvent(date, lastValue, id));
                    }
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
                finally {
                    Application.setHasStarted(true);
                }
            }
        };

        this.timeSeriesDataService.getTimeSeriesData(request, callback);
    }

    private void getTimeSeriesData(TimeSeriesDataRequest req) throws Exception {
        // prepare callback
        final long start = System.currentTimeMillis();
        TimeSeriesDataCallback callback = new TimeSeriesDataCallback(this, "Could not get timeseries data.") {
            @Override
            public void onSuccess(TimeSeriesDataResponse result) {
                requestMgr.removeRequest(System.currentTimeMillis() - start);
                try {
                    EventBus.getMainEventBus().fireEvent(new StoreTimeSeriesDataEvent(result.getPayloadData()));
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
                finally {
                    Application.setHasStarted(true);
                }
            }
        };
        addRequest();
        this.timeSeriesDataService.getTimeSeriesData(req, callback);
    }

    public void requestSensorData(TimeSeries[] timeseriesArray) {
        if (timeseriesArray.length > 0) {
            ArrayList<TimeSeriesProperties> series = new ArrayList<TimeSeriesProperties>();
            for (int i = 0; i < timeseriesArray.length; i++) {
                TimeSeries timeseries = timeseriesArray[i];
                series.add(timeseries.getProperties());
            }
            try {
                boolean gridEnabled = DataStoreTimeSeriesImpl.getInst().isGridEnabled();
                long begin = TimeManager.getInst().getBegin();
                long end = TimeManager.getInst().getEnd();
				DesignOptions options = new DesignOptions(series, begin, end, gridEnabled);
				getTimeSeriesData(new TimeSeriesDataRequest(options));
            }
            catch (Exception e) {
                ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
            }
        }
    }

    public void requestDiagram() {
        TimeSeries[] timeSeries = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
        if (timeSeries.length == 0) {
            // reset diagram to blank image
            EventBus.getMainEventBus().fireEvent(new SetImageUrlEvent("img/blank.gif"));
            EventBus.getOverviewChartEventBus().fireEvent(new SetImageUrlEvent("img/blank.gif"));
            EventBus.getOverviewChartEventBus().fireEvent(new SetDomainBoundsEvent(new Bounds(0d, null, 0d, null)));
            return;
        }

        ArrayList<TimeSeriesProperties> properties = new ArrayList<TimeSeriesProperties>();
        for (TimeSeries timeSerie : timeSeries) {
            timeSerie.getProperties().setHeight(EESTab.getPanelHeight());
            timeSerie.getProperties().setWidth(EESTab.getPanelWidth());
            properties.add(timeSerie.getProperties());
        }

        long begin = TimeManager.getInst().getBegin();
        long end = TimeManager.getInst().getEnd();
        boolean grid = DataStoreTimeSeriesImpl.getInst().isGridEnabled();

        try {
            DesignOptions o1 = new DesignOptions(properties, begin, end, grid);
            getDiagram(new EESDataRequest(o1));

            long interval = end - begin;
            long middle = (long) (end - (interval * 0.5));
            long timeRangeOverview = TimeManager.getInst().getOverviewInterval();

            long ovBegin = middle - timeRangeOverview / 2;
            long ovEnd = middle + timeRangeOverview / 2;
//            long currentTime = System.currentTimeMillis() + (middle - ovBegin) / 20; 
//            if (ovEnd > currentTime) {
//				ovEnd = currentTime;
//				ovBegin = ovEnd - timeRangeOverview;
//			}

            ArrayList<TimeSeriesProperties> copySeries = new ArrayList<TimeSeriesProperties>();
            for (TimeSeriesProperties pc : properties) {
                TimeSeriesProperties copy = pc.copy();
                setDefaultValues(copy);
                copy.setLanguage(PropertiesManager.language);
                copy.setShowYAxis(false);
                copy.setScaledToZero(true);
                copy.setHeight(100);
                copySeries.add(copy);
            }

            DesignOptions o2 = new DesignOptions(copySeries, ovBegin, ovEnd, grid);
            getDiagramOverview(new EESDataRequest(o2));
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Could not get diagram", e));
        }
    }

    private void setDefaultValues(TimeSeriesProperties copy) {
        PropertiesManager properties = PropertiesManager.getInstance();
        ArrayList<String> mappings = properties.getParameters("phenomenon");
        for (String mapping : mappings) {
            String[] values = mapping.split(",");
            if (copy.getPhenomenon().getLabel().equals(values[0])) {
                try {
                    copy.setLineStyle(values[1]);
                    copy.setSeriesType(values[2]);
                    if (RegExp.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").test(values[3])) {
                        copy.setHexColor(values[3]);
                    } else {
                        throw new Exception("Pattern for hex color do not match");
                    }
                } catch (Exception e) {
                    Toaster.getInstance().addErrorMessage(i18n.errorPhenomenonProperties());
                }
            }
        }
    }

    /**
     * Gets the diagram overview.
     * 
     * @param req
     *        the req
     * @throws TimeoutException
     *         the timeout exception
     */
    private void getDiagramOverview(final EESDataRequest req) throws Exception {

        // prepare callback
        EESDataCallback callback = new EESDataCallback(this, "Could not get overview diagram.") {
            @Override
            public void onSuccess(EESDataResponse result) {
                removeRequest();
                try {
                    // FIXME wait for fix
                    // EESController.getOverviewEventBus().fireEvent(new
                    // SetViewportBoundsEvent(new Bounds(
                    // 0d, new Double(result.getPlotArea().getWidth()),
                    // 0d, 100d)));
                    GWT.log("Got OverviewDiagram: " + result.getWidth() + "w x " + result.getHeight() + "h");
                    GWT.log("For Viewportsize: " + EESTab.getPanelWidth() + "w x " + EESTab.getPanelHeight() + "h");
                    // EESController.getOverviewEventBus().fireEvent(
                    // new SetMaxBoundsEvent(new Bounds((double)
                    // result.getDateRangeOnXAxis().getStart().getTime(),
                    // (double) result.getDateRangeOnXAxis().getEnd().getTime(),
                    // null, null)));

                    // inner pixel bounds = plotarea
                    EventBus bus = EventBus.getOverviewChartEventBus();
                    bus.fireEvent(new SetDataAreaPixelBoundsEvent(result.getPlotArea()));
                    bus.fireEvent(new SetImageUrlEvent(result.getImageUrl()));

//                    Bounds maxDomainBounds = new Bounds(0d, (double) new Date().getTime(), null, null);
//                    bus.fireEvent(new SetMaxDomainBoundsEvent(maxDomainBounds));

                    Double begin = new Double(result.getBegin() + 0.0);
                    Double end = new Double(result.getEnd() + 0.0);
                    Double top = result.getPlotArea().getTop();
                    Double bottom = result.getPlotArea().getBottom();
                    Bounds domainBounds = new Bounds(begin, end, top, bottom);
                    bus.fireEvent(new SetDomainBoundsEvent(domainBounds));

                    Double vpLeft = new Double(0.0);
                    Double vpRight = new Double(result.getWidth());
                    Double vpTop = new Double(0.0);
                    Double pixelBottom = new Double(result.getHeight());
                    Bounds viewportPixelBounds = new Bounds(vpLeft, vpRight, vpTop, pixelBottom);
                    bus.fireEvent(new SetViewportPixelBoundsEvent(viewportPixelBounds));
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
            }
        };

        // request
        addRequest();
        this.eesDataService.getEESOverview(req, callback);
    }

    private void getDiagram(final EESDataRequest request) throws Exception {

        // prepare callback
        EESDataCallback callback = new EESDataCallback(this, "Could not get diagram.") {
            @Override
            public void onSuccess(EESDataResponse result) {
                removeRequest();
                try {
                    // block overview
                    if ( !GWT.isProdMode()) {
                        GWT.log("Got Diagram: " + result.getWidth() + "w x " + result.getHeight() + "h");
                        GWT.log("For Viewportsize: " + EESTab.getPanelWidth() + "w x " + EESTab.getPanelHeight() + "h");
                    }
                    for (String key : result.getAxis().keySet()) {
                        EventBus.getMainEventBus().fireEvent(new StoreAxisDataEvent(key, result.getAxis().get(key)));
                    }

                    for (TimeSeriesProperties prop : result.getPropertiesList()) {
                        TimeSeriesHasDataEvent hasDataEvent = new TimeSeriesHasDataEvent(prop.getTsID(), prop.hasData());
                        EventBus.getMainEventBus().fireEvent(hasDataEvent);
                    }

                    EventBus.getMainEventBus().fireEvent(new SetImageUrlEvent(result.getImageUrl()));

                    // inner pixel bounds = plotarea
                    EventBus.getMainEventBus().fireEvent(new SetDataAreaPixelBoundsEvent(result.getPlotArea()));

                    Double left = new Double(0);
                    Double right = new Double(new Date().getTime());
                    Bounds maxDomainBounds = new Bounds(left, right, null, null);
                    EventBus.getMainEventBus().fireEvent(new SetMaxDomainBoundsEvent(maxDomainBounds));

                    SetDomainBoundsEventHandler[] blocked = {DataStoreTimeSeriesImpl.getInst().getEventBroker()};
                    Double mainLeft = new Double(result.getBegin() + 0.0);
                    Double mainRight = new Double(result.getEnd() + 0.0);
                    Double mainTop = result.getPlotArea().getTop();
                    Double mainBottom = result.getPlotArea().getBottom();
                    Bounds diagramBounds = new Bounds(mainLeft, mainRight, mainTop, mainBottom);
                    EventBus.getMainEventBus().fireEvent(new SetDomainBoundsEvent(diagramBounds, blocked));

                    Double overviewRight = new Double(result.getWidth());
                    Double overviewBottom = new Double(result.getHeight());
                    Bounds viewportBounds = new Bounds(left, overviewRight, left, overviewBottom);
                    EventBus.getMainEventBus().fireEvent(new SetViewportPixelBoundsEvent(viewportBounds));

                    List<ImageEntity> ie = new ArrayList<ImageEntity>();
                    for (ImageEntity imageEntity : result.getImageEntities()) {
                        ie.add(imageEntity);
                    }
                    EventBus.getMainEventBus().fireEvent(new SetImageEntitiesEvent(ie));
                    EventBus.getOverviewChartEventBus().fireEvent(new SetOverviewDomainBoundsEvent(diagramBounds));

                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
                finally {
                    Application.setHasStarted(true);
                    result.destroy();
                    result = null;
                }
            }
        };

        // request
        addRequest();
        this.eesDataService.getEESDiagram(request, callback);
    }

    public void requestProcedurePositions(String sosURL, BoundingBox boundingBox) {
        try {
            getProcedurePositions(sosURL, boundingBox);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new ServerException("could not get procedures", e));
        }
    }

    private void getProcedurePositions(String sosURL, BoundingBox boundingBox) throws Exception {

        SOSMetadata meta = DataManagerSosImpl.getInst().getServiceMetadata(sosURL);
        EventBus.getMainEventBus().fireEvent(new DeleteMarkersEvent());

        int chunkSize = meta.getRequestChunk() > 0 ? meta.getRequestChunk() : 25;
        if (meta != null) {
            // no position data available, request it
            getPositions(sosURL, 0, chunkSize, boundingBox);
        }
    }

    void getPositions(final String sosURL, int startIdx, final int interval, final BoundingBox boundingBox) throws Exception {
        final long begin = System.currentTimeMillis();
        GetPositionsCallback callback = new GetPositionsCallback(this, "Could not get positions.") {
            @Override
            public void onSuccess(final StationPositionsResponse result) {
                try {
                    removeRequest();
                    if (result.isFinished()) {
                        requestMgr.removeRequest(System.currentTimeMillis() - begin);
                        EventBus.getMainEventBus().fireEvent(new GetProcedurePositionsFinishedEvent());
                    } else {
                        getNextChunk(sosURL, interval, boundingBox, result);
                    }
                    String srs = result.getSrs();
                    String url = result.getSosURL();
                    List<Station> stations = result.getStations();
                    StoreStationsEvent event = new StoreStationsEvent(url, stations, srs);
                    EventBus.getMainEventBus().fireEvent(event);
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                    removeRequest();
                }
            }

            private void getNextChunk(final String sosURL, final int interval, final BoundingBox boundingBox, final StationPositionsResponse result) {
                final int start = result.getEndIdx();
                try {
                    getPositions(sosURL, start, interval, boundingBox);
                } catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                    removeRequest();
                }
            }
        };
        addRequest();
        this.stationPositionService.getStationPositions(sosURL, startIdx, interval, boundingBox, callback);
    }

    /**
     * Request export pdf.
     * 
     * @param timeseries
     *        the timeseries
     */
    public void requestExportPDF(Collection<TimeSeries> timeseries) {
        try {
            getPDF(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    /**
     * Gets the pDF.
     * 
     * @param timeseries
     *        the timeseries
     * @throws TimeoutException
     *         the timeout exception
     */
    private void getPDF(Collection<TimeSeries> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getPDF(req, new FileCallback(SOSRequestManager.this));
    }

    private TimeSeriesDataRequest createTimeSeriesDataRequest(Collection<TimeSeries> tsCollection) {
        ArrayList<TimeSeriesProperties> series = new ArrayList<TimeSeriesProperties>();
        for (TimeSeries timeSeries : tsCollection) {
            timeSeries.getProperties().setLanguage(PropertiesManager.language);
			series.add(timeSeries.getProperties());
		}
        boolean grid = DataStoreTimeSeriesImpl.getInst().isGridEnabled();
        long begin = TimeManager.getInst().getBegin();
        long end = TimeManager.getInst().getEnd();
        DesignOptions options = new DesignOptions(series, begin, end, grid);
        TimeSeriesDataRequest req = new TimeSeriesDataRequest(options);
        return req;
    }

    public void requestExportXLS(Collection<TimeSeries> timeseries) {
        try {
            getXLS(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getXLS(Collection<TimeSeries> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);

        this.fileDataService.getXLS(req, new FileCallback(SOSRequestManager.this));

    }

    public void requestExportCSV(Collection<TimeSeries> timeseries) {
        try {
            getCSV(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getCSV(Collection<TimeSeries> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getCSV(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportPDFzip(Collection<TimeSeries> timeseries) {
        try {
            getPDFzip(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getPDFzip(Collection<TimeSeries> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);

        this.fileDataService.getPDFzip(req, new FileCallback(SOSRequestManager.this));
    }
    
    

    public void requestExportXLSzip(Collection<TimeSeries> timeseries) {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getXLSzip(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportCSVzip(Collection<TimeSeries> timeseries) {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getCSVzip(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportPDFallInOne(Collection<TimeSeries> timeseries) {
        try {
            getPDF(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public void requestPhenomenons(String sosURL) {

        // prepare callback
        GetPhenomenaCallback callback = new GetPhenomenaCallback(this, "Could not request phenomena.") {
            @Override
            public void onSuccess(GetPhenomenonResponse result) {
                removeRequest();
                try {
                    String url = result.getSosURL();
                    HashMap<String, String> phenomenons = result.getPhenomenons();
                    StorePhenomenaEvent event = new StorePhenomenaEvent(url, null, phenomenons);
                    EventBus.getMainEventBus().fireEvent(event);
                }
                catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                }
            }
        };

        // request
        addRequest();
        this.serviceMetadataService.getPhen4SOS(sosURL, callback);
    }

	public void requestProcedureDetailsUrl(String serviceURL, String procedure) {

		// prepare callback
		GetProcedureDetailsUrlCallback callback = new GetProcedureDetailsUrlCallback(this, "Could not get procedure details url") {

			@Override
			public void onSuccess(GetProcedureDetailsUrlResponse result) {
				removeRequest();
				try {
					String url = result.getUrl();
					StoreProcedureDetailsUrlEvent event = new StoreProcedureDetailsUrlEvent(url);
					EventBus.getMainEventBus().fireEvent(event);
				} catch (Exception e) {
					ExceptionHandler.handleUnexpectedException(e);
				}
			}
		};
		
		// request
		addRequest();
		this.sensorMetadataService.getProcedureDetailsUrl(serviceURL, procedure, callback);
	}

	public void requestProcedure(String serviceURL, String procedureID) {
		
		GetProcedureCallback callback = new GetProcedureCallback(this, "Could not get the procedure with ID: " + procedureID) {
			
			@Override
			public void onSuccess(GetProcedureResponse result) {
				removeRequest();
				try {
					StoreProcedureEvent event = new StoreProcedureEvent(result.getServiceURL(), result.getProcedure());
					EventBus.getMainEventBus().fireEvent(event);
				} catch (Exception e) {
					ExceptionHandler.handleUnexpectedException(e);
				}
			}
		};
		
		addRequest();
		this.serviceMetadataService.getProcedure(serviceURL, procedureID, callback);
	}

	public void requestOffering(String serviceURL, String offeringID) {

		GetOfferingCallback callback = new GetOfferingCallback(this, "Could not get the offering with ID: " + offeringID) {
			
			@Override
			public void onSuccess(GetOfferingResponse result) {
				removeRequest();
				try {
					StoreOfferingEvent event = new StoreOfferingEvent(result.getServiceURL(), result.getOffering());
					EventBus.getMainEventBus().fireEvent(event);
				} catch (Exception e) {
					ExceptionHandler.handleUnexpectedException(e);
				}
			}
		};
	
		addRequest();
		this.serviceMetadataService.getOffering(serviceURL, offeringID, callback);
	}

	public void requestFeature(String serviceURL, String featureID) {

		GetFeatureCallback callback = new GetFeatureCallback(this, "Could not get the feature with ID: " + featureID) {
			
			@Override
			public void onSuccess(GetFeatureResponse result) {
				removeRequest();
				try {
					StoreFeatureEvent event = new StoreFeatureEvent(result.getServiceURL(), result.getFeature());
					EventBus.getMainEventBus().fireEvent(event);
				} catch (Exception e) {
					ExceptionHandler.handleUnexpectedException(e);
				}
			}
		};
		
		addRequest();
		this.serviceMetadataService.getFeature(serviceURL, featureID, callback);
	}

	public void requestStation(String serviceURL, String offeringID, String procedureID, String phenomenonID, String featureID) {

		GetStationCallback callback = new GetStationCallback(this, "Could not get the station") {
			
			@Override
			public void onSuccess(GetStationResponse result) {
				removeRequest();
				try {
					StoreStationEvent event = new StoreStationEvent(result.getServiceURL(), result.getStation());
					EventBus.getMainEventBus().fireEvent(event);
				} catch (Exception e) {
					ExceptionHandler.handleUnexpectedException(e);
				}
				
			}
		};

		addRequest();
		this.serviceMetadataService.getStation(serviceURL, offeringID, procedureID, phenomenonID, featureID, callback);
	}
	
}
