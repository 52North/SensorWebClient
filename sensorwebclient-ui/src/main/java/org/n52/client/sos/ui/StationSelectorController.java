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
package org.n52.client.sos.ui;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;
import static org.n52.client.sos.ui.StationSelectorMap.FALLBACK_EXTENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.bus.EventBus;
import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.client.sos.event.AddMarkerEvent;
import org.n52.client.sos.event.data.GetFeatureEvent;
import org.n52.client.sos.event.data.GetOfferingEvent;
import org.n52.client.sos.event.data.GetPhenomenonsEvent;
import org.n52.client.sos.event.data.GetProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.GetProcedureEvent;
import org.n52.client.sos.event.data.GetProcedurePositionsFinishedEvent;
import org.n52.client.sos.event.data.GetStationsWithinBBoxEvent;
import org.n52.client.sos.event.data.NewPhenomenonsEvent;
import org.n52.client.sos.event.data.NewStationPositionsEvent;
import org.n52.client.sos.event.data.PropagateOfferingsFullEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.StoreSOSMetadataEvent;
import org.n52.client.sos.event.data.handler.GetProcedurePositionsFinishedEventHandler;
import org.n52.client.sos.event.data.handler.NewPhenomenonsEventHandler;
import org.n52.client.sos.event.data.handler.NewStationPositionsEventHandler;
import org.n52.client.sos.event.data.handler.PropagateOfferingFullEventHandler;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureDetailsUrlEventHandler;
import org.n52.client.sos.event.data.handler.StoreSOSMetadataEventHandler;
import org.n52.client.sos.event.handler.AddMarkerEventHandler;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.map.InfoMarker;
import org.n52.client.ui.map.MapController;
import org.n52.io.crs.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

import com.google.gwt.core.client.GWT;

class StationSelectorController implements MapController {

    private StationSelectorMap map;

    private StationSelector stationSelector;

    private String selectedServiceUrl;

    private Map<String, String> selectedStationFilterByServiceUrl;

    private Station selectedStation;

    private String selectedCategory;

    public StationSelectorController() {
        map = new StationSelectorMap(this);
        new StationSelectorControllerEventBroker(this);
        this.selectedStationFilterByServiceUrl = new HashMap<String, String>();
    }

    void setStationPicker(StationSelector stationSelector) {
        this.stationSelector = stationSelector;
    }

    public MapWidget createMap() {
        return map.getMapWidget();
    }

    public void updateMapContent() {
        if (selectedServiceUrl != null) {
            GWT.log("update procedures in map");
            map.updateStations(getCurrentMetadata());
        }
    }

    void updateStationFilter() {
        if (selectedServiceUrl != null) {
            GWT.log("update phenomenon selector");
            stationSelector.updateStationFilters(getCurrentMetadata());
            String filter = selectedStationFilterByServiceUrl.get(selectedServiceUrl);
            stationSelector.setSelectedFilter(selectedServiceUrl, filter);
        }
    }

    void updateContentUponStationFilter() {
        String filter = selectedStationFilterByServiceUrl.get(selectedServiceUrl);
        if (selectedServiceUrl != null && filter != null) {
            map.applyFilterToStationsOnMap(filter);
        }
    }

    public String getMapProjection() {
        return map.getMapProjection();
    }

    /**
     * Zooms to the extent of all markers on the map if 'autoZoom' is configured to <code>true</code>.
     * Otherwise controller zooms to configured extent (either SERVICES instance specific or global). If
     * nothing was configured a fallback extent is being zoomed.
     */
    public void zoomToConfiguredExtent() {
        final SOSMetadata metadata = getCurrentMetadata();
        if (metadata.isAutoZoom()) {
            GWT.log("Perform auto zooming.");
            map.zoomToMarkers();
        }
        else {
            
            // TODO refactor here, unclear where the configured extent is being used .. perhaps just the markers are being used?
            
            BoundingBox boundingBox = metadata.getConfiguredExtent();
            if (boundingBox == null) {
                GWT.log("Zoom to SERVICES preconfigured bounding box: " + boundingBox);
                map.zoomToExtent(FALLBACK_EXTENT);
            }
            else {
                GWT.log("Zoom to map's default extent: " + boundingBox);
                map.zoomToExtent(map.getDefaultExtent());
            }
        }
    }
    
    public void handleInfoMarkerClicked(InfoMarker infoMarker) {
        if (isServiceSelected()) {
            Toaster.getToasterInstance().addErrorMessage("No service selected");
            return;
        }

        selectedStation = infoMarker.getStation();

        String category = getSelectedStationFilter();
        if (category != null) {
            loadTimeseriesByCategory(category);
        }

        map.selectMarker(infoMarker);

        // open info window for the marker
        stationSelector.showInfoWindow(infoMarker, selectedStation.getLabel());
    }

    public void loadTimeseriesByCategory(String category) {
        selectedCategory = category;
        SosTimeseries timeseries = selectedStation.getTimeseriesByCategory(selectedCategory);
        if (timeseries != null) {
            fireGetTimeseries(timeseries);
        }
        else {
            GWT.log("Timeseries to load was null!");
        }
    }

    private void fireGetTimeseries(SosTimeseries timeseries) {
        getMainEventBus().fireEvent(new GetProcedureEvent(selectedServiceUrl, timeseries.getProcedureId()));
        getMainEventBus().fireEvent(new GetOfferingEvent(selectedServiceUrl, timeseries.getOfferingId()));
        getMainEventBus().fireEvent(new GetFeatureEvent(selectedServiceUrl, timeseries.getFeatureId()));
        getMainEventBus().fireEvent(new GetProcedureDetailsUrlEvent(timeseries));
    }

    private boolean isServiceSelected() {
        return selectedServiceUrl == null;
    }

    void clearMarkerSelection() {
        map.unmarkAllMarkers();
    }

    public void setSelectedServiceURL(String serviceURL) {
        this.selectedServiceUrl = serviceURL;
    }

    public void setStationFilter(String stationFilter) {
        if (selectedServiceUrl != null) {
            selectedStationFilterByServiceUrl.put(selectedServiceUrl, stationFilter);
        }
    }

    public String getSelectedServiceURL() {
        return selectedServiceUrl;
    }

    public String getSelectedStationFilter() {
        return selectedStationFilterByServiceUrl.get(selectedServiceUrl);
    }

    public void removeSelectedStationFilter(String url) {
        selectedStationFilterByServiceUrl.remove(url);
    }

    public String getSelectedFeatureId() {
        return selectedCategory;
    }

    public Station getSelectedStation() {
        return selectedStation;
    }

    public SosTimeseries getSelectedTimeseries() {
        return selectedStation.getTimeseriesByCategory(selectedCategory);
    }

    public Phenomenon getSelectedPhenomenon() {
        return getParametersLookup().getPhenomenon(selectedCategory);
    }

    public Feature getSelectedFeature() {
        return getParametersLookup().getFeature(getSelectedFeatureId());
    }

    private TimeseriesParametersLookup getParametersLookup() {
        final SOSMetadata metadata = getCurrentMetadata();
        return metadata.getTimeseriesParametersLookup();
    }

    public SOSMetadata getCurrentMetadata() {
        return getDataManager().getServiceMetadata(selectedServiceUrl);
    }

    private class StationSelectorControllerEventBroker implements
            NewPhenomenonsEventHandler,
            NewStationPositionsEventHandler,
            PropagateOfferingFullEventHandler,
            StoreProcedureDetailsUrlEventHandler,
            StoreFeatureEventHandler,
            AddMarkerEventHandler,
            GetProcedurePositionsFinishedEventHandler,
            StoreSOSMetadataEventHandler {

        private StationSelectorController controller;

        public StationSelectorControllerEventBroker(StationSelectorController controller) {
            this.controller = controller;
            EventBus bus = EventBus.getMainEventBus();
            bus.addHandler(NewPhenomenonsEvent.TYPE, this);
            bus.addHandler(NewStationPositionsEvent.TYPE, this);
            bus.addHandler(PropagateOfferingsFullEvent.TYPE, this);
            bus.addHandler(StoreProcedureDetailsUrlEvent.TYPE, this);
            bus.addHandler(GetProcedurePositionsFinishedEvent.TYPE, this);
            bus.addHandler(StoreFeatureEvent.TYPE, this);
            bus.addHandler(AddMarkerEvent.TYPE, this);
            bus.addHandler(StoreSOSMetadataEvent.TYPE, this);
        }

        @Override
        public void onNewPhenomenons(NewPhenomenonsEvent evt) {
            if ( !GWT.isProdMode()) {
                TimeseriesParametersLookup lookup = controller.getParametersLookup();
                Collection<Phenomenon> phenomenons = lookup.getPhenomenons();
                GWT.log("#" + phenomenons.size() + " new Phenomenons");
            }
        }

        @Override
        public void onNewStationPositions(NewStationPositionsEvent evt) {
            if ( !GWT.isProdMode()) {
                TimeseriesParametersLookup lookup = controller.getParametersLookup();
                ArrayList<Procedure> procedures = lookup.getProcedures();
                int proceduresSize = procedures.size();
                GWT.log("#" + proceduresSize + " new Procedures");
            }
            controller.updateMapContent();
            controller.zoomToConfiguredExtent();
            controller.updateStationFilter();
        }

        @Override
        public void onNewFullOfferings(PropagateOfferingsFullEvent evt) {
            if ( !GWT.isProdMode()) {
                TimeseriesParametersLookup lookup = controller.getParametersLookup();
                Collection<Offering> offerings = lookup.getOfferings();
                int offeringsSize = offerings.size();
                GWT.log("#" + offeringsSize + " new Offerings");
            }
        }

        @Override
        public void onStore(StoreProcedureDetailsUrlEvent evt) {
            stationSelector.updateProcedureDetailsURL(evt.getUrl());
        }

        @Override
        public void onAddMarker(AddMarkerEvent evt) {
            if (selectedStationFilterByServiceUrl.get(selectedServiceUrl) == null) {
                String filterCategory = getMostCommonStationCategory(evt.getStations());
                if (filterCategory != null) {
                    controller.setStationFilter(filterCategory);
                    stationSelector.setSelectedFilter(selectedServiceUrl, filterCategory);
                }
            }
            controller.updateContentUponStationFilter();
        }

        @Override
        public void onGetProcedurePositionsFinishedEvent(GetProcedurePositionsFinishedEvent evt) {
            loadingStations(false);
        }

        @Override
        public void onStore(StoreFeatureEvent evt) {
            stationSelector.updateInfoLabels();
        }

        @Override
        public void onStore(StoreSOSMetadataEvent evt) {
            if (evt.getMetadata().getServiceUrl().equals(controller.selectedServiceUrl)) {
                controller.performSOSDataRequests(selectedServiceUrl);
            }
        }
    }

    /**
     * @param stations
     *        the stations determine most common category
     * @return The category most commonly used by all stations.
     */
    public String getMostCommonStationCategory(List<Station> stations) {
        Map<String, Integer> countResults = new HashMap<String, Integer>();
        // for (Station station : stations) {
        // increaseAmountOf(station.getStationCategory(), countResults);
        // }
        // TODO get most Common StationCategory
        int maxCount = 0;
        String mostCommonCategory = null;
        for (Entry<String, Integer> currentValue : countResults.entrySet()) {
            Integer count = currentValue.getValue();
            if (count > maxCount) {
                mostCommonCategory = currentValue.getKey();
                maxCount = count;
            }
        }
        return mostCommonCategory;
    }

    public void performSOSDataRequests(String serviceURL) {
        /* XXX Using the current extent would require the client to get missing stations from the server part.
         * this would make neccessary an interaction (zoom, pan) based rendering of stations!
        BoundingBox bbox = controller.getCurrentExtent();*/
        
        SosDataManager dataManager = SosDataManager.getDataManager();
        SOSMetadata metadata = dataManager.getServiceMetadata(serviceURL);
        BoundingBox bbox = metadata.getConfiguredExtent();
        GetStationsWithinBBoxEvent getStations = new GetStationsWithinBBoxEvent(serviceURL, bbox);
        loadingStations(true);
        GetPhenomenonsEvent getPhenomenons = new GetPhenomenonsEvent.Builder(serviceURL).build();
        EventBus.getMainEventBus().fireEvent(getStations);
        EventBus.getMainEventBus().fireEvent(getPhenomenons);
        setSelectedServiceURL(serviceURL);
    }

    @SuppressWarnings("unused")
    private void increaseAmountOf(String category, Map<String, Integer> countResults) {
        if (countResults.containsKey(category)) {
            Integer counter = countResults.get(category);
            countResults.put(category, ++counter);
        }
        else {
            countResults.put(category, 1);
        }
    }

    public void loadingStations(boolean activ) {
        if (activ) {
            stationSelector.showStationLoadingSpinner(true);
        }
        else {
            stationSelector.showStationLoadingSpinner(false);
        }
    }

}
