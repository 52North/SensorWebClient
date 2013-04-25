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

package org.n52.client.sos.ui;

import static org.n52.client.sos.ctrl.DataManagerSosImpl.getDataManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.bus.EventBus;
import org.n52.client.sos.ctrl.DataManagerSosImpl;
import org.n52.client.sos.event.AddMarkerEvent;
import org.n52.client.sos.event.data.GetFeatureEvent;
import org.n52.client.sos.event.data.GetOfferingEvent;
import org.n52.client.sos.event.data.GetProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.GetProcedureEvent;
import org.n52.client.sos.event.data.GetProcedurePositionsFinishedEvent;
import org.n52.client.sos.event.data.NewPhenomenonsEvent;
import org.n52.client.sos.event.data.NewStationPositionsEvent;
import org.n52.client.sos.event.data.PropagateOfferingsFullEvent;
import org.n52.client.sos.event.data.StoreFeatureEvent;
import org.n52.client.sos.event.data.StoreProcedureDetailsUrlEvent;
import org.n52.client.sos.event.data.handler.GetProcedurePositionsFinishedEventHandler;
import org.n52.client.sos.event.data.handler.NewPhenomenonsEventHandler;
import org.n52.client.sos.event.data.handler.NewStationPositionsEventHandler;
import org.n52.client.sos.event.data.handler.PropagateOfferingFullEventHandler;
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.client.sos.event.data.handler.StoreProcedureDetailsUrlEventHandler;
import org.n52.client.sos.event.handler.AddMarkerEventHandler;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.map.InfoMarker;
import org.n52.client.ui.map.MapController;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.ParameterConstellation;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
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
        new StationPickerControllerEventBroker(this);
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
     * Otherwise controller zooms to configured extent (either SERVICES instance specific or global). If nothing
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
                GWT.log("Zoom to SERVICES preconfigured bounding box: " + boundingBox);
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
        if (isServiceSelected()) {
        	Toaster.getToasterInstance().addErrorMessage("No service selected");
            return;
        }

        selectedStation = infoMarker.getStation();
        
        String category = getSelectedStationFilter();
        if(category != null) {
        	loadParameterConstellationByCategory(category);
        }

        map.selectMarker(infoMarker);

        // open info window for the marker
        stationSelector.showInfoWindow(infoMarker, selectedStation.getId());
    }
    
    public void loadParameterConstellationByCategory(String category) {
    	selectedCategory = category;
    	ParameterConstellation paramConst = selectedStation.getParameterConstellationByCategory(selectedCategory);
    	if (paramConst != null) {
    		fireGetParameterConstellation(paramConst);
    	}
    }

	private void fireGetParameterConstellation(ParameterConstellation paramConst) {
		GetProcedureEvent getProcEvent = new GetProcedureEvent(
				selectedServiceUrl,
				paramConst.getProcedure());
		EventBus.getMainEventBus().fireEvent(getProcEvent);
		GetOfferingEvent getOffEvent = new GetOfferingEvent(selectedServiceUrl,
				paramConst.getOffering());
		EventBus.getMainEventBus().fireEvent(getOffEvent);
		GetFeatureEvent getFoiEvent = new GetFeatureEvent(selectedServiceUrl,
				paramConst.getFeatureOfInterest());
		EventBus.getMainEventBus().fireEvent(getFoiEvent);

		// Get procedure details
		GetProcedureDetailsUrlEvent getProcDetailsEvent = new GetProcedureDetailsUrlEvent(
				selectedServiceUrl,
				paramConst.getProcedure());
		EventBus.getMainEventBus().fireEvent(getProcDetailsEvent);
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
    
    public ParameterConstellation getSelectedParameterConstellation() {
		return selectedStation.getParameterConstellationByCategory(selectedCategory);
	}

    public Phenomenon getSelectedPhenomenon() {
        return getParametersLookup().getPhenomenon(selectedCategory);
    }

    public FeatureOfInterest getSelectedFeature() {
        return getParametersLookup().getFeature(getSelectedFeatureId());
    }

    public BoundingBox getCurrentExtent() {
        return map.getCurrentExtent();
    }
    
    private TimeseriesParametersLookup getParametersLookup() {
        final SOSMetadata metadata = getCurrentMetadata();
        return metadata.getTimeseriesParamtersLookup();
    }

    public SOSMetadata getCurrentMetadata() {
        return getDataManager().getServiceMetadata(selectedServiceUrl);
    }
    

    private class StationPickerControllerEventBroker implements
            NewPhenomenonsEventHandler,
            NewStationPositionsEventHandler,
            PropagateOfferingFullEventHandler,
            StoreProcedureDetailsUrlEventHandler,
            StoreFeatureEventHandler,
            AddMarkerEventHandler,
            GetProcedurePositionsFinishedEventHandler {

        private StationSelectorController controller;

        public StationPickerControllerEventBroker(StationSelectorController controller) {
            this.controller = controller;
            EventBus bus = EventBus.getMainEventBus();
            bus.addHandler(NewPhenomenonsEvent.TYPE, this);
            bus.addHandler(NewStationPositionsEvent.TYPE, this);
            bus.addHandler(PropagateOfferingsFullEvent.TYPE, this);
            bus.addHandler(StoreProcedureDetailsUrlEvent.TYPE, this);
            bus.addHandler(GetProcedurePositionsFinishedEvent.TYPE, this);
            bus.addHandler(StoreFeatureEvent.TYPE, this);
            bus.addHandler(AddMarkerEvent.TYPE, this);
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
    }

    /**
     * @param stations
     *        the stations determine most common category
     * @return The category most commonly used by all stations.
     */
    public String getMostCommonStationCategory(List<Station> stations) {
        Map<String, Integer> countResults = new HashMap<String, Integer>();
//        for (Station station : stations) {
//            increaseAmountOf(station.getStationCategory(), countResults);
//        }
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
