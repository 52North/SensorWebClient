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

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ctrl.ExceptionHandler.handleException;
import static org.n52.client.ctrl.ExceptionHandler.handleUnexpectedException;
import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;
import static org.n52.client.sos.data.TimeseriesDataStore.getTimeSeriesDataStore;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.shared.serializable.pojos.DesignOptions.SOS_PARAM_FIRST;
import static org.n52.shared.serializable.pojos.DesignOptions.SOS_PARAM_LAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.n52.client.Application;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ctrl.RequestFailedException;
import org.n52.client.ctrl.RequestManager;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.ctrl.callbacks.EESDataCallback;
import org.n52.client.ctrl.callbacks.FileCallback;
import org.n52.client.ctrl.callbacks.GetProcedureDetailsUrlCallback;
import org.n52.client.ctrl.callbacks.QueryCallback;
import org.n52.client.ctrl.callbacks.SensorMetadataCallback;
import org.n52.client.ctrl.callbacks.TimeSeriesDataCallback;
import org.n52.client.sos.data.TimeseriesDataStore;
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
import org.n52.client.sos.event.data.StoreSOSMetadataEvent;
import org.n52.client.sos.event.data.StoreStationEvent;
import org.n52.client.sos.event.data.StoreStationsEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesLastValueEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesPropsEvent;
import org.n52.client.sos.event.data.TimeSeriesHasDataEvent;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.sos.ui.DiagramTab;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.View;
import org.n52.client.ui.legend.LegendElement;
import org.n52.io.crs.BoundingBox;
import org.n52.shared.exceptions.CompatibilityException;
import org.n52.shared.exceptions.ServerException;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.requests.EESDataRequest;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.ResultPage;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.FeatureQueryResponse;
import org.n52.shared.requests.query.responses.OfferingQueryResponse;
import org.n52.shared.requests.query.responses.PhenomenonQueryResponse;
import org.n52.shared.requests.query.responses.ProcedureQueryResponse;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.requests.query.responses.StationQueryResponse;
import org.n52.shared.responses.EESDataResponse;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.shared.service.rpc.RpcEESDataService;
import org.n52.shared.service.rpc.RpcEESDataServiceAsync;
import org.n52.shared.service.rpc.RpcFileDataService;
import org.n52.shared.service.rpc.RpcFileDataServiceAsync;
import org.n52.shared.service.rpc.RpcQueryService;
import org.n52.shared.service.rpc.RpcQueryServiceAsync;
import org.n52.shared.service.rpc.RpcSensorMetadataService;
import org.n52.shared.service.rpc.RpcSensorMetadataServiceAsync;
import org.n52.shared.service.rpc.RpcTimeSeriesDataService;
import org.n52.shared.service.rpc.RpcTimeSeriesDataServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SOSRequestManager extends RequestManager {
	
	private static SOSRequestManager instance;

    private RpcTimeSeriesDataServiceAsync timeSeriesDataService;

    private RpcSensorMetadataServiceAsync sensorMetadataService;

    private RpcQueryServiceAsync queryService;

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
        this.queryService = GWT.create(RpcQueryService.class);
        this.sensorMetadataService = GWT.create(RpcSensorMetadataService.class);
        this.timeSeriesDataService = GWT.create(RpcTimeSeriesDataService.class);
        this.fileDataService = GWT.create(RpcFileDataService.class);
        this.eesDataService = GWT.create(RpcEESDataService.class);
    }

    private void getSensorData(TimeseriesProperties properties, final boolean requestSensorData) throws Exception {
        SensorMetadataCallback callback = new SensorMetadataCallback(this, "Could not get sensor data.") {
            @Override
            public void onSuccess(SensorMetadataResponse result) {
                removeRequest();
                try {
                    String tsID = result.getProps().getTimeseriesId();
                    TimeseriesDataStore dataManager = TimeseriesDataStore.getTimeSeriesDataStore();
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
        TimeseriesProperties props = createTimeseriesProperties(evt, evt.getServiceUrl());
        
        TimeseriesLegendData timeSeries = new TimeseriesLegendData(evt.getTimeseries().getTimeseriesId(), props);

        try {
            getMainEventBus().fireEvent(new StoreTimeSeriesEvent(timeSeries));
        }
        catch (Exception e) {
            handleUnexpectedException(e);
        }
        finally {
            try {
                requestLastValueOf(timeSeries);
                requestFirstValueOf(timeSeries);
                getSensorData(props, evt.requestSensordata());
            }
            catch (Exception e) {
                handleException(new RequestFailedException("Server did not respond!", e));
            }
        }
    }

    private TimeseriesProperties createTimeseriesProperties(NewTimeSeriesEvent evt, String serviceUrl) {
        int width = evt.getWidth();
        int height = evt.getHeight();
        Station station = evt.getStation();
        SosTimeseries timeseries = evt.getTimeseries();
        TimeseriesProperties properties = new TimeseriesProperties(timeseries, station, width, height);
        TimeseriesRenderingOptions renderingOptions = evt.getRenderingOptions();
        properties.setRenderingOptions(renderingOptions);
        return properties;
    }

    private TimeseriesParametersLookup getTimeseriesParameterLookupFor(String serviceUrl) {
        SOSMetadata meta = getDataManager().getServiceMetadata(serviceUrl);
        return meta.getTimeseriesParametersLookup();
    }

    public void requestFirstValueOf(TimeseriesLegendData timeSeries) {
        try {
            ArrayList<TimeseriesProperties> series = new ArrayList<TimeseriesProperties>();
            series.add(timeSeries.getProperties());

            boolean grid = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();
            long begin = TimeManager.getInst().getBegin();
            long end = TimeManager.getInst().getEnd();
            DesignOptions options = new DesignOptions(series, begin, end, SOS_PARAM_FIRST, grid);
            requestFirstValueFromTimeSeries(new TimeSeriesDataRequest(options), timeSeries);
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
        }
    }

    public void requestLastValueOf(TimeseriesLegendData timeSeries) {
        try {
            ArrayList<TimeseriesProperties> series = new ArrayList<TimeseriesProperties>();
            series.add(timeSeries.getProperties());
            boolean grid = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();
            long begin = TimeManager.getInst().getBegin();
            long end = TimeManager.getInst().getEnd();
            DesignOptions options = new DesignOptions(series, begin, end, SOS_PARAM_LAST, grid);
            requestLastTimeSeriesData(new TimeSeriesDataRequest(options), timeSeries);
        }
        catch (TimeoutException ex) {
            ExceptionHandler.handleException(ex);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new RequestFailedException("Request failed", e));
        }
    }

    public void requestSensorData(TimeseriesLegendData[] timeSeries, String id) {
        try {
            ArrayList<TimeseriesProperties> series = new ArrayList<TimeseriesProperties>();
            for (TimeseriesLegendData timeSerie : timeSeries) {
                if (timeSerie.getId().equals(id)) {
                    timeSerie.getProperties().setHeight(View.getView().getDataPanelHeight());
                    timeSerie.getProperties().setWidth(View.getView().getDataPanelWidth());
                    series.add(timeSerie.getProperties());
                    break;
                }
            }
            boolean grid = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();
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

    private void requestFirstValueFromTimeSeries(TimeSeriesDataRequest request, final TimeseriesLegendData timeSeries) throws Exception {
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
                HashMap<String, HashMap<Long, Double>> payloadData = response.getPayloadData();
                try {
                    if (payloadData.isEmpty()) {
                        return;
                    }
                    String id = timeSeries.getId();
                    HashMap<Long, Double> timeSeriesData = payloadData.get(id);
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

    private void requestLastTimeSeriesData(TimeSeriesDataRequest request, final TimeseriesLegendData timeSeries) throws Exception {
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
                HashMap<String, HashMap<Long, Double>> payloadData = response.getPayloadData();

                try {
                    if (payloadData.isEmpty()) {
                        return; // nothing returned from server
                    }

                    String id = timeSeries.getId();
                    HashMap<Long, Double> timeSeriesData = payloadData.get(id);
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

    public void requestSensorData(TimeseriesLegendData[] timeseriesArray) {
        if (timeseriesArray.length > 0) {
            ArrayList<TimeseriesProperties> series = new ArrayList<TimeseriesProperties>();
            for (int i = 0; i < timeseriesArray.length; i++) {
                TimeseriesLegendData timeseries = timeseriesArray[i];
                series.add(timeseries.getProperties());
            }
            try {
                boolean gridEnabled = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();
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
        TimeseriesLegendData[] timeSeries = getTimeSeriesDataStore().getTimeSeriesSorted();
        if (timeSeries.length == 0) {
            // reset diagram to blank image
            EventBus.getMainEventBus().fireEvent(new SetImageUrlEvent("img/blank.gif"));
            EventBus.getOverviewChartEventBus().fireEvent(new SetImageUrlEvent("img/blank.gif"));
            EventBus.getOverviewChartEventBus().fireEvent(new SetDomainBoundsEvent(new Bounds(0d, null, 0d, null)));
            return;
        }

        ArrayList<TimeseriesProperties> properties = new ArrayList<TimeseriesProperties>();
        for (TimeseriesLegendData timeSerie : timeSeries) {
            timeSerie.getProperties().setHeight(DiagramTab.getPanelHeight());
            timeSerie.getProperties().setWidth(DiagramTab.getPanelWidth());
            properties.add(timeSerie.getProperties());
        }

        long begin = TimeManager.getInst().getBegin();
        long end = TimeManager.getInst().getEnd();
        boolean grid = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();

        try {
            DesignOptions o1 = new DesignOptions(properties, begin, end, grid);
            getDiagram(new EESDataRequest(o1));

            long interval = end - begin;
            long middle = (long) (end - (interval * 0.5));
            long timeRangeOverview = TimeManager.getInst().getOverviewInterval();

            long ovBegin = middle - timeRangeOverview / 2;
            long ovEnd = middle + timeRangeOverview / 2;

            ArrayList<TimeseriesProperties> copySeries = new ArrayList<TimeseriesProperties>();
            for (TimeseriesProperties pc : properties) {
                TimeseriesProperties copy = pc.copy();
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

    private void setDefaultValues(TimeseriesProperties tsProperties) {
        
        /*
         * TODO refactor default rendering properties for a phenomenon
         */
        
        PropertiesManager properties = PropertiesManager.getPropertiesManager();
        ArrayList<String> mappings = properties.getParameters("phenomenon");
        for (String mapping : mappings) {
            String[] values = mapping.split(",");
            if (tsProperties.getPhenomenon().equals(values[0])) {
                try {
                    tsProperties.setLineStyle(values[1]);
                    tsProperties.setSeriesType(values[2]);
                    if (RegExp.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").test(values[3])) {
                        tsProperties.setHexColor(values[3]);
                    } else {
                        throw new Exception("Pattern for hex color do not match");
                    }
                } catch (Exception e) {
                    Toaster.getToasterInstance().addErrorMessage(i18n.errorPhenomenonProperties());
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
                    GWT.log("For Viewportsize: " + DiagramTab.getPanelWidth() + "w x " + DiagramTab.getPanelHeight() + "h");
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
                        GWT.log("For Viewportsize: " + DiagramTab.getPanelWidth() + "w x " + DiagramTab.getPanelHeight() + "h");
                    }
                    for (String key : result.getAxis().keySet()) {
                        EventBus.getMainEventBus().fireEvent(new StoreAxisDataEvent(key, result.getAxis().get(key)));
                    }

                    for (TimeseriesProperties prop : result.getPropertiesList()) {
                        TimeSeriesHasDataEvent hasDataEvent = new TimeSeriesHasDataEvent(prop.getTimeseriesId(), prop.hasData());
                        EventBus.getMainEventBus().fireEvent(hasDataEvent);
                    }

                    EventBus.getMainEventBus().fireEvent(new SetImageUrlEvent(result.getImageUrl()));

                    // inner pixel bounds = plotarea
                    EventBus.getMainEventBus().fireEvent(new SetDataAreaPixelBoundsEvent(result.getPlotArea()));

                    Double left = new Double(0);
                    Double right = new Double(new Date().getTime());
                    Bounds maxDomainBounds = new Bounds(left, right, null, null);
                    EventBus.getMainEventBus().fireEvent(new SetMaxDomainBoundsEvent(maxDomainBounds));

                    SetDomainBoundsEventHandler[] blocked = {TimeseriesDataStore.getTimeSeriesDataStore().getEventBroker()};
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

    public void requestStations(String serviceUrl, BoundingBox boundingBox) {
        try {
            EventBus.getMainEventBus().fireEvent(new DeleteMarkersEvent());
            SOSMetadata meta = getDataManager().getServiceMetadata(serviceUrl);
            int chunkSize = meta.getRequestChunk() > 0 ? meta.getRequestChunk() : 300;
            getPositions(serviceUrl, 0, chunkSize, boundingBox);
        }
        catch (Exception e) {
            ExceptionHandler.handleException(new ServerException("could not get procedures", e));
        }
    }

    public void requestExportPDF(Collection<TimeseriesLegendData> timeseries) {
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
    private void getPDF(Collection<TimeseriesLegendData> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getPDF(req, new FileCallback(SOSRequestManager.this));
    }

    private TimeSeriesDataRequest createTimeSeriesDataRequest(Collection<TimeseriesLegendData> tsCollection) {
        ArrayList<TimeseriesProperties> series = new ArrayList<TimeseriesProperties>();
        for (TimeseriesLegendData timeSeries : tsCollection) {
            timeSeries.getProperties().setLanguage(PropertiesManager.language);
			series.add(timeSeries.getProperties());
		}
        boolean grid = TimeseriesDataStore.getTimeSeriesDataStore().isGridEnabled();
        long begin = TimeManager.getInst().getBegin();
        long end = TimeManager.getInst().getEnd();
        DesignOptions options = new DesignOptions(series, begin, end, grid);
        TimeSeriesDataRequest req = new TimeSeriesDataRequest(options);
        return req;
    }

    public void requestExportXLS(Collection<TimeseriesLegendData> timeseries) {
        try {
            getXLS(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getXLS(Collection<TimeseriesLegendData> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);

        this.fileDataService.getXLS(req, new FileCallback(SOSRequestManager.this));

    }

    public void requestExportCSV(Collection<TimeseriesLegendData> timeseries) {
        try {
            getCSV(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getCSV(Collection<TimeseriesLegendData> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getCSV(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportPDFzip(Collection<TimeseriesLegendData> timeseries) {
        try {
            getPDFzip(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void getPDFzip(Collection<TimeseriesLegendData> timeseries) throws TimeoutException {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);

        this.fileDataService.getPDFzip(req, new FileCallback(SOSRequestManager.this));
    }
    
    

    public void requestExportXLSzip(Collection<TimeseriesLegendData> timeseries) {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getXLSzip(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportCSVzip(Collection<TimeseriesLegendData> timeseries) {
        addRequest();
        TimeSeriesDataRequest req = createTimeSeriesDataRequest(timeseries);
        this.fileDataService.getCSVzip(req, new FileCallback(SOSRequestManager.this));
    }

    public void requestExportPDFallInOne(Collection<TimeseriesLegendData> timeseries) {
        try {
            getPDF(timeseries);
        }
        catch (TimeoutException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public void requestProcedureDetailsUrl(SosTimeseries timeseries) {

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
		this.sensorMetadataService.getProcedureDetailsUrl(timeseries, callback);
	}

	public void requestPhenomenons(String serviceUrl) {
	    QueryCallback callback = createQueryCallback("Could not request phenomena.");
	    QueryFactory factory = createQueryFactoryFor(serviceUrl);
	    QueryRequest request = factory.createAllPhenomenonsQuery();
	    this.queryService.doQuery(request, callback);
	}

	public void requestProcedure(String serviceUrl, String procedureId) {
		QueryCallback callback = createQueryCallback("Could not get the procedure with ID: " + procedureId);
        QueryFactory factory = createQueryFactoryFor(serviceUrl);
        QueryParameters parameters = new QueryParameters().setProcedure(procedureId);
		QueryRequest request = factory.createFilteredProcedureQuery(parameters);
		this.queryService.doQuery(request, callback);
	}

	public void requestOffering(String serviceUrl, String offeringId) {
		QueryCallback callback = createQueryCallback("Could not get the offering with ID: " + offeringId);
		QueryFactory factory = createQueryFactoryFor(serviceUrl);
        QueryParameters parameters = new QueryParameters().setOffering(offeringId); 
		QueryRequest request = factory.createFilteredOfferingQuery(parameters);
		this.queryService.doQuery(request, callback);
	}

	public void requestFeature(String serviceUrl, String featureId) {
		QueryCallback callback = createQueryCallback("Could not get the feature with ID: " + featureId); 
        QueryFactory factory = createQueryFactoryFor(serviceUrl);
        QueryParameters parameters = new QueryParameters().setFeature(featureId); 
		QueryRequest request = factory.createFilteredFeaturesQuery(parameters);
		this.queryService.doQuery(request, callback);
	}

	public void requestStationWith(SosTimeseries timeseries) {
		QueryCallback callback = createQueryCallback("Could not get the timeseries: " + timeseries);
        QueryFactory factory = createQueryFactoryFor(timeseries.getServiceUrl());
        QueryParameters parameters = new QueryParameters()
                 .setOffering(timeseries.getOfferingId())
                 .setFeature(timeseries.getFeatureId())
                 .setPhenomenon(timeseries.getPhenomenonId())
                 .setProcedure(timeseries.getProcedureId());
        QueryRequest request = factory.createFilteredStationQuery(parameters);
		doQuery(request, callback);
	}

	public void requestUpdateSOSMetadata() {
		addRequest();
		this.sensorMetadataService.getUpdatedSOSMetadata(new AsyncCallback<SOSMetadataResponse>() {
			@Override
			public void onSuccess(SOSMetadataResponse response) {
				removeRequest();
				Map<String, SOSMetadata> metadatas = response.getServiceMetadata();
				for (String metadataKey : metadatas.keySet()) {
					SOSMetadata sosMetadata = metadatas.get(metadataKey);
					EventBus.getMainEventBus().fireEvent(new StoreSOSMetadataEvent(sosMetadata));
					Toaster.getToasterInstance().addMessage("Update service with url " + sosMetadata.getServiceUrl());
				}
			}
			@Override
			public void onFailure(Throwable error) {
				GWT.log("Error occured while updating SOS metadata.", error);
			}
		});
	}

	private QueryCallback createQueryCallback(String errorMessage) {
		QueryCallback callback = new QueryCallback(this, errorMessage) {
			@Override
			public void onSuccess(QueryResponse<?> result) {
				removeRequest();
				try {
					if (result instanceof StationQueryResponse) {
						handleStationQuery(result);
					} else if (result instanceof PhenomenonQueryResponse) {
						handlePhenomenonQuery(result);
					} else if (result instanceof ProcedureQueryResponse) {
						handleProcedureQuery(result);
					} else if (result instanceof OfferingQueryResponse) {
						handleOfferingQuery(result);
					} else if (result instanceof FeatureQueryResponse) {
						handleFeatureQuery(result);
					}
				} catch (Exception e) {
					handleUnexpectedException(e);
				}
			}
		};
		return callback;
	}
	
	
	protected void handlePhenomenonQuery(QueryResponse<?> response) {
		Phenomenon[] phenomenons = (Phenomenon[]) response.getResults();
        StorePhenomenaEvent event = new StorePhenomenaEvent(response.getServiceUrl(), null, phenomenons);
        EventBus.getMainEventBus().fireEvent(event);
	}

	private void getPositions(final String serviceUrl, int offset, final int pageSize, final BoundingBox boundingBox) throws Exception {
	    final long begin = System.currentTimeMillis();
	    QueryCallback callback = new QueryCallback(this, "Could not get positions.") {
	        @Override
	        public void onSuccess(final QueryResponse<?> queryResponse) {
	            try {
	                ResultPage< ? > resultPage = queryResponse.getPagedResults();
                    String url = queryResponse.getServiceUrl();
                    if (resultPage.isLastPage()) {
	                    requestMgr.removeRequest(System.currentTimeMillis() - begin);
	                    getMainEventBus().fireEvent(new GetProcedurePositionsFinishedEvent());
	                } else {
	                    int nextOffset = resultPage.getOffset() + pageSize;
	                    getNextChunk(serviceUrl, nextOffset, pageSize, boundingBox);
	                }
                    Station[] stations = (Station[]) resultPage.getResults();
	                StoreStationsEvent event = new StoreStationsEvent(url, stations);
	                getMainEventBus().fireEvent(event);
	            }
	            catch (Exception e) {
	                handleUnexpectedException(e);
	                removeRequest();
	            }
	        }
	
            private void getNextChunk(final String sosURL, final int start, final int interval, final BoundingBox boundingBox) {
	            try {
	                getPositions(sosURL, start, interval, boundingBox);
	            } catch (Exception e) {
	                handleUnexpectedException(e);
	                removeRequest();
	            }
	        }
	    };
	    
	    QueryFactory factory = createQueryFactoryFor(serviceUrl);
	    QueryParameters parameters = new QueryParameters()
	            .setSpatialFilter(boundingBox)
	            .setPageSize(pageSize)
	            .setOffset(offset);
	    QueryRequest request = factory.createFilteredStationQuery(parameters);
	    this.queryService.doQuery(request, callback);
        addRequest();
	}

	protected void handleStationQuery(QueryResponse<?> response) {
		Station station = (Station) response.getResults()[0]; // TODO fragile!
		StoreStationEvent event = new StoreStationEvent(response.getServiceUrl(), station);
		EventBus.getMainEventBus().fireEvent(event);
	}
	
	protected void handleProcedureQuery(QueryResponse<?> response) {
		Procedure procedure = (Procedure) response.getResults()[0]; // TODO fragile!
		StoreProcedureEvent event = new StoreProcedureEvent(response.getServiceUrl(), procedure);
		EventBus.getMainEventBus().fireEvent(event);
	}

	protected void handleOfferingQuery(QueryResponse<?> response) {
		Offering offering = (Offering) response.getResults()[0]; // TODO fragile!
		StoreOfferingEvent event = new StoreOfferingEvent(response.getServiceUrl(), offering);
		EventBus.getMainEventBus().fireEvent(event);
	}
	
	protected void handleFeatureQuery(QueryResponse<?> response) {
		Feature feature = (Feature) response.getResults()[0]; // TODO fragile!
		StoreFeatureEvent event = new StoreFeatureEvent(response.getServiceUrl(), feature);
		EventBus.getMainEventBus().fireEvent(event);
	}
	
	private void doQuery(QueryRequest request, QueryCallback callback) {
		this.queryService.doQuery(request, callback);
        addRequest();
	}
	

    private QueryFactory createQueryFactoryFor(String serviceUrl) {
        SOSMetadata metadata = getDataManager().getServiceMetadata(serviceUrl);
        return new QueryFactory(metadata);
    }

}
