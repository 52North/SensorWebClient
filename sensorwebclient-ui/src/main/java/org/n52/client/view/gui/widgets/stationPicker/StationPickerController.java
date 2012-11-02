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

package org.n52.client.view.gui.widgets.stationPicker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.AddMarkerEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetFeatureEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetOfferingEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetProcedureDetailsUrlEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetProcedureEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetProcedurePositionsFinishedEvent;
import org.n52.client.eventBus.events.dataEvents.sos.NewPhenomenonsEvent;
import org.n52.client.eventBus.events.dataEvents.sos.NewStationPositionsEvent;
import org.n52.client.eventBus.events.dataEvents.sos.PropagateOfferingsFullEvent;
import org.n52.client.eventBus.events.dataEvents.sos.StoreFeatureEvent;
import org.n52.client.eventBus.events.dataEvents.sos.StoreProcedureDetailsUrlEvent;
import org.n52.client.eventBus.events.dataEvents.sos.handler.GetProcedurePositionsFinishedEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.NewPhenomenonsEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.NewStationPositionsEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.PropagateOfferingFullEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.StoreFeatureEventHandler;
import org.n52.client.eventBus.events.dataEvents.sos.handler.StoreProcedureDetailsUrlEventHandler;
import org.n52.client.eventBus.events.handler.AddMarkerEventHandler;
import org.n52.client.model.data.dataManagers.DataManagerSosImpl;
import org.n52.client.view.gui.widgets.mapping.InfoMarker;
import org.n52.client.view.gui.widgets.mapping.MapController;
import org.n52.client.view.gui.widgets.mapping.StationPickerMap;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;

import com.google.gwt.core.client.GWT;

public class StationPickerController implements MapController {

    private StationPickerMap map;

    private StationPicker stationPicker;

    private String selectedServiceUrl;

    private Map<String, String> selectedStationFilterByServiceUrl;

    private Station selectedStation;

    public StationPickerController() {
        map = new StationPickerMap(this);
        new StationPickerControllerEventBroker(this);
        this.selectedStationFilterByServiceUrl = new HashMap<String, String>();
    }

    void setStationPicker(StationPicker stationPicker) {
        this.stationPicker = stationPicker;
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
            stationPicker.updateStationFilters(getCurrentMetadata());
            String filter = selectedStationFilterByServiceUrl.get(selectedServiceUrl);
            stationPicker.setSelectedFilter(selectedServiceUrl, filter);
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
     * Otherwise controller zooms to configured extent (either SOS instance specific or global). If nothing
     * was configured the {@link Constants#FALLBACK_EXTENT} is zoomed.
     */
    public void zoomToConfiguredExtent() {
        final SOSMetadata metadata = getCurrentMetadata();
        if (metadata.isAutoZoom()) {
            GWT.log("Perform auto zooming.");
            map.zoomToMarkers();
        }
        else {
            BoundingBox boundingBox = metadata.getConfiguredExtent();
            if (isSosSpecificExtentConfigured(boundingBox)) {
                GWT.log("Zoom to SOS preconfigured bounding box: " + boundingBox);
                map.zoomToExtent(boundingBox);
            }
            else {
                GWT.log("Zoom to map's default extent: " + boundingBox);
                map.zoomToExtent(map.getDefaultExtent());
            }
        }
    }

    public boolean isSosSpecificExtentConfigured(BoundingBox boundingBox) {
        return !boundingBox.equals(Constants.FALLBACK_EXTENT);
    }

    public void handleInfoMarkerClicked(InfoMarker infoMarker) {
        if (isSelectionRequired()) {
            // TODO inform user to first select a phenomenon from radio buttons
            return;
        }

        selectedStation = infoMarker.getStation();

        GetProcedureEvent getProcEvent = new GetProcedureEvent(selectedServiceUrl, selectedStation.getProcedure());
        EventBus.getMainEventBus().fireEvent(getProcEvent);
        GetOfferingEvent getOffEvent = new GetOfferingEvent(selectedServiceUrl, selectedStation.getOffering());
        EventBus.getMainEventBus().fireEvent(getOffEvent);
        GetFeatureEvent getFoiEvent = new GetFeatureEvent(selectedServiceUrl, selectedStation.getFeature());
        EventBus.getMainEventBus().fireEvent(getFoiEvent);

        // Get procedure details
        GetProcedureDetailsUrlEvent getProcDetailsEvent = new GetProcedureDetailsUrlEvent(selectedServiceUrl,
                                                                                          selectedStation.getProcedure());
        EventBus.getMainEventBus().fireEvent(getProcDetailsEvent);

        map.selectMarker(infoMarker);

        // open info window for the marker
        stationPicker.showInfoWindow(infoMarker);
    }

    private boolean isSelectionRequired() {
        return selectedServiceUrl == null || selectedStationFilterByServiceUrl.get(selectedServiceUrl) == null;
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

    public String getSelectedProcedureId() {
        return selectedStation.getProcedure();
    }

    public String getSelectedOfferingId() {
        return selectedStation.getOffering();
    }

    public String getSelectedFeatureId() {
        return selectedStation.getFeature();
    }

    public Station getSelectedStation() {
        return selectedStation;
    }

    public Phenomenon getSelectedPhenomenon() {
        return getCurrentMetadata().getPhenomenon(getSelectedStationFilter());
    }

    public FeatureOfInterest getSelectedFeature() {
        return getCurrentMetadata().getFeature(getSelectedFeatureId());
    }

    public BoundingBox getCurrentExtent() {
        return map.getCurrentExtent();
    }

    public SOSMetadata getCurrentMetadata() {
        return DataManagerSosImpl.getInst().getServiceMetadata(selectedServiceUrl);
    }

    private class StationPickerControllerEventBroker implements
            NewPhenomenonsEventHandler,
            NewStationPositionsEventHandler,
            PropagateOfferingFullEventHandler,
            StoreProcedureDetailsUrlEventHandler,
            StoreFeatureEventHandler,
            AddMarkerEventHandler,
            GetProcedurePositionsFinishedEventHandler {

        private StationPickerController controller;

        public StationPickerControllerEventBroker(StationPickerController controller) {
            this.controller = controller;
            EventBus bus = EventBus.getMainEventBus();
            bus.addHandler(NewPhenomenonsEvent.TYPE, this);
            bus.addHandler(NewStationPositionsEvent.TYPE, this);
            bus.addHandler(PropagateOfferingsFullEvent.TYPE, this);
            bus.addHandler(StoreProcedureDetailsUrlEvent.TYPE, this);
            bus.addHandler(StoreFeatureEvent.TYPE, this);
            bus.addHandler(AddMarkerEvent.TYPE, this);
            bus.addHandler(GetProcedurePositionsFinishedEvent.TYPE, this);
        }

        @Override
        public void onNewPhenomenons(NewPhenomenonsEvent evt) {
            if ( !GWT.isProdMode()) {
                final SOSMetadata metadata = controller.getCurrentMetadata();
                Collection<Phenomenon> phenomenons = metadata.getPhenomenons();
                GWT.log("#" + phenomenons.size() + " new Phenomenons");
            }
        }

        @Override
        public void onNewStationPositions(NewStationPositionsEvent evt) {
            final SOSMetadata metadata = controller.getCurrentMetadata();
            if ( !GWT.isProdMode()) {
                ArrayList<Procedure> procedures = metadata.getProcedures();
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
                final SOSMetadata metadata = controller.getCurrentMetadata();
                ArrayList<Offering> offerings = metadata.getOfferings();
                int offeringsSize = offerings.size();
                GWT.log("#" + offeringsSize + " new Offerings");
            }
        }

        @Override
        public void onStore(StoreProcedureDetailsUrlEvent evt) {
            stationPicker.updateProcedureDetailsURL(evt.getUrl());
        }

        @Override
        public void onAddMarker(AddMarkerEvent evt) {
            if (selectedStationFilterByServiceUrl.get(selectedServiceUrl) == null) {
                String filterCategory = getMostCommonStationCategory(evt.getStations());
                if (filterCategory != null) {
                    controller.setStationFilter(filterCategory);
                    stationPicker.setSelectedFilter(selectedServiceUrl, filterCategory);
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
            stationPicker.updateInfoLabels();
        }
    }

    /**
     * @param stations
     *        the stations determine most common category
     * @return The category most commonly used by all stations.
     */
    public String getMostCommonStationCategory(List<Station> stations) {
        Map<String, Integer> countResults = new HashMap<String, Integer>();
        for (Station station : stations) {
            increaseAmountOf(station.getStationCategory(), countResults);
        }
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
            stationPicker.showStationLoadingSpinner(true);
        }
        else {
            stationPicker.showStationLoadingSpinner(false);
        }
    }

}
