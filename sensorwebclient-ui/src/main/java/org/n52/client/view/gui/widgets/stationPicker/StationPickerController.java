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

    private String selectedServiceURL;

    private Map<String, String> selectedPhenomenonByServiceURL;

    private Station selectedStation;

    public StationPickerController() {
        map = new StationPickerMap(this);
        new StationPickerControllerEventBroker(this);
        this.selectedPhenomenonByServiceURL = new HashMap<String, String>();
    }

    void setStationPicker(StationPicker stationPicker) {
        this.stationPicker = stationPicker;
    }

    public MapWidget createMap() {
        return map.getMapWidget();
    }

    public void updateMapContent() {
        if (selectedServiceURL != null) {
            GWT.log("update procedures in map");
            map.updateStations(getCurrentMetadata());
        }
    }

    void updatePhenomenonSelector() {
        if (selectedServiceURL != null) {
            GWT.log("update phenomenon selector");
            stationPicker.updatePhenomenonSelector(getCurrentMetadata());
            stationPicker.setSelectedPhenomenon(selectedServiceURL, selectedPhenomenonByServiceURL.get(selectedServiceURL));
        }
    }

    void updateContentUponPhenomenonSelection() {
        if (this.selectedServiceURL != null && this.selectedPhenomenonByServiceURL.get(selectedServiceURL) != null) {
            map.applyPhenomenonFilterToProceduresOnMap(this.selectedPhenomenonByServiceURL.get(selectedServiceURL));
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
        if (selectedServiceURL == null || this.selectedPhenomenonByServiceURL.get(selectedServiceURL) == null) {
            // TODO inform user to first select a phenomenon from radio buttons
            return;
        }

        selectedStation = infoMarker.getStation();

        GetProcedureEvent getProcEvent = new GetProcedureEvent(selectedServiceURL, selectedStation.getProcedure());
        EventBus.getMainEventBus().fireEvent(getProcEvent);
        GetOfferingEvent getOffEvent = new GetOfferingEvent(selectedServiceURL, selectedStation.getOffering());
        EventBus.getMainEventBus().fireEvent(getOffEvent);
        GetFeatureEvent getFoiEvent = new GetFeatureEvent(selectedServiceURL, selectedStation.getFeature());
        EventBus.getMainEventBus().fireEvent(getFoiEvent);

        // Get procedure details
        GetProcedureDetailsUrlEvent getProcDetailsEvent = new GetProcedureDetailsUrlEvent(selectedServiceURL,
                                                                                          selectedStation.getProcedure());
        EventBus.getMainEventBus().fireEvent(getProcDetailsEvent);

        map.selectMarker(infoMarker);

        // open info window for the marker
        stationPicker.showInfoWindow(infoMarker);
    }

    void clearMarkerSelection() {
        map.unmarkAllMarkers();
    }

    public void setSelectedServiceURL(String serviceURL) {
        this.selectedServiceURL = serviceURL;
    }

    public void setSelectedPhenomenon(String phenomenonId) {
        if (this.selectedServiceURL != null) {
            this.selectedPhenomenonByServiceURL.put(this.selectedServiceURL, phenomenonId);
        }
    }

    public String getSelectedServiceURL() {
        return selectedServiceURL;
    }

    public String getSelectedPhenomenonId() {
        return selectedPhenomenonByServiceURL.get(selectedServiceURL);
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
        return getCurrentMetadata().getPhenomenon(getSelectedPhenomenonId());
    }

    public FeatureOfInterest getSelectedFeature() {
        return getCurrentMetadata().getFeature(getSelectedFeatureId());
    }

    public BoundingBox getCurrentExtent() {
        return map.getCurrentExtent();
    }

    public SOSMetadata getCurrentMetadata() {
        return DataManagerSosImpl.getInst().getServiceMetadata(selectedServiceURL);
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
            controller.updatePhenomenonSelector();
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
            if (selectedPhenomenonByServiceURL.get(selectedServiceURL) == null) {
                String mostCommonPhenomenon = getMostCommonPhenomenon(evt.getStations());
                if (mostCommonPhenomenon != null) {
                    controller.setSelectedPhenomenon(mostCommonPhenomenon);
                    stationPicker.setSelectedPhenomenon(selectedServiceURL, mostCommonPhenomenon);
                }
            }
            controller.updateContentUponPhenomenonSelection();
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
     * The most common used phenomenon in the Service.
     * 
     * @param procedures
     * 
     * @return
     */
    public String getMostCommonPhenomenon(List<Station> stations) {
        Map<String, Integer> phenomenonsCounter = new HashMap<String, Integer>();
        String mostCommonPhenomenon = null;
        int mostCommonCounter = 0;
        for (Station station : stations) {
            countPhenomenonUp(phenomenonsCounter, station.getPhenomenon());
        }
        for (Entry<String, Integer> phenomsCounterEntry : phenomenonsCounter.entrySet()) {
            Integer count = phenomsCounterEntry.getValue();
            if (count >= mostCommonCounter) {
                mostCommonPhenomenon = phenomsCounterEntry.getKey();
                mostCommonCounter = count;
            }
        }
        return mostCommonPhenomenon;
    }

    private void countPhenomenonUp(Map<String, Integer> phenomenonsCounter, String phenomenon) {
        if (phenomenonsCounter.containsKey(phenomenon)) {
            Integer counter = phenomenonsCounter.get(phenomenon);
            phenomenonsCounter.put(phenomenon, ++counter);
        }
        else {
            phenomenonsCounter.put(phenomenon, 1);
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
