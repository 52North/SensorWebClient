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

package org.n52.client.view.gui.elements.legend;

import java.util.ArrayList;
import java.util.Collection;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.control.ZoomIn;
import org.gwtopenmaps.openlayers.client.control.ZoomOut;
import org.gwtopenmaps.openlayers.client.layer.Markers;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.event.InitEvent;
import org.n52.client.sos.event.handler.InitEventHandler;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.view.gui.widgets.mapping.Coordinate;
import org.n52.client.view.gui.widgets.mapping.OpenLayersMapWrapper;
import org.n52.client.view.gui.widgets.mapping.OpenlayersMarker;
import org.n52.shared.Constants;

import com.google.gwt.core.client.GWT;

public class OverviewMap extends OpenLayersMapWrapper {

	private OverviewMapController controller;

    private ArrayList<Marker> markersOnMap = new ArrayList<Marker>();

    private Markers markerLayer = new Markers("markers");

	private Popup popup;

    public OverviewMap(OverviewMapController controller) {
        super("270px"); // XXX map needs explicit px height
    	this.controller = controller;
        map.addLayer(this.markerLayer);
        new OverviewMapEventBroker();
    }
    
    @Override
	protected void addMapControls(MapOptions mapOptions) {
    	// FIXME does not work .. navigation panel is still drawn
		mapOptions.removeDefaultControls();
		map.addControl(new ZoomIn());
		map.addControl(new ZoomOut());
	}

	public void addPopup(Popup popup) {
        this.map.addPopup(popup);
    }

    public void addMarker(Marker m) {
        this.markersOnMap.add(m);
        this.markerLayer.addMarker(m);
    }

    public void removePopup() {
        map.removePopup(popup);
    }

    public boolean isShownPopup() {
    	return popup != null;
    }
    
    public ArrayList<Marker> getMarkers() {
        return markersOnMap;
    }

    public void removeAllMarkers() {
        for (Marker m : this.markersOnMap) {
            this.markerLayer.removeMarker(m);
        }
        this.markersOnMap.clear();
    }

    public void removeMarker(Marker m) {
        this.markersOnMap.remove(m);
        this.markerLayer.removeMarker(m);
    }

    public void zoomToMarkers() {
        Bounds bbox = markerLayer.getDataExtent();
        if (bbox != null) {
            map.setCenter(bbox.getCenterLonLat());
            if (markersOnMap.size() > 1) {
                map.zoomToExtent(bbox);
            } else {
                map.zoomTo(DEFAULT_ZOOM_LEVEL);
            }
        }
    }

    public void zoomToMarkers(Markers markerlist) {
        map.zoomToExtent(markerlist.getDataExtent());
    }
    
    public void updateMapContent() {
    	DataStoreTimeSeriesImpl dataStore = DataStoreTimeSeriesImpl.getInst();
		Collection<TimeSeries> ts = dataStore.getDataItems().values();
    	boolean updateExtent = isTimeSeriesAddedUpdate(ts); // check before update
        updateMarkersOnMap(ts);
        if(updateExtent){
        	zoomToMarkers();
        }
        if (ts.isEmpty()) {
			initMap();
		}
        markSelectedMarker();
    }

	private void updateMarkersOnMap(Collection<TimeSeries> ts) {
        removeAllMarkers();
		for (TimeSeries timeSeries : ts) {
            Coordinate coords = timeSeries.getCoords();
            if (coords == null) {
                GWT.log("TimeSeries has no coordinates; skip creating map button.");
                continue;
            }

            OpenlayersMarker olMarker = new OpenlayersMarker(coords, timeSeries);
            ArrayList<Marker> markers = getMarkers();
            
            if (markers.isEmpty()) {
                addMarker(olMarker);
                olMarker.createInfoPopup(getMap()); // TODO create popup
            }
            else {
                int size = markers.size(); // avoid CMException
                for (int i = 0; i < size; i++) {
                    Marker marker = markers.get(i);

                    if ( ! (marker instanceof OpenlayersMarker)) {
                        continue; // ignore non OL markers
                    }

                    // XXX necessary to differentiate between OLmarker and "normal" marker?
                    if (isMarkerAlreadyShown(olMarker)) {
                        extendOLMarkerInfo(timeSeries.getId(), marker, olMarker.getInfoTxt());
                    }
                    else {
                        addMarker(olMarker);
                        olMarker.createInfoPopup(getMap());
                    }
                }
            }
        }
	}

	private boolean isTimeSeriesAddedUpdate(Collection<TimeSeries> newTimeSeries) {
		return getMarkers().size() != newTimeSeries.size();
	}
    
    private void markSelectedMarker() {

        if (controller.isSelectedTimeSeries()) {
            for (Marker m : getMarkers()) {
                if (m instanceof OpenlayersMarker) {
                    OpenlayersMarker olm = (OpenlayersMarker) m;
                    if (olm.containsTS(controller.getSelectedTimeSeriesId())) {
                        olm.mark();
                    }
                    else {
                        olm.unmark();
                    }
                }
            }
        }
    }

    private void extendOLMarkerInfo(String id, Marker marker, String olInfo) {

        OpenlayersMarker olm = (OpenlayersMarker) marker;
        if ( !olm.availableTimeseriesIds().contains(id)) {
            removeMarker(marker);
            olm.addToInfoTxt("---------</br>" + olInfo);
            olm.addTimeseriesId(id);
            addMarker(olm);
        }
    }
    
    /**
     * Checks if given marker is already shown on the map.
     * 
     * @param olMarker
     *        the open layers marker to check.
     * @return <code>true</code> if marker is already shown, and <code>false</code> otherwise.
     */
    private boolean isMarkerAlreadyShown(OpenlayersMarker olMarker) {
        for (Marker marker : getMarkers()) {
            if (olMarker.getLonLat().equals(marker.getLonLat())) {
                return true;
            }
        }
        return false;
    }
    
    public void initMap() {
        if (isDefinedGlobalExtent()) {
            PropertiesManager propertiesMgr = PropertiesManager.getInstance();
            String lleftX = propertiesMgr.getParameterAsString("lleftX");
            String lleftY = propertiesMgr.getParameterAsString("lleftY");
            String urightX = propertiesMgr.getParameterAsString("urightX");
            String urightY = propertiesMgr.getParameterAsString("urightY");
            zoomToMaxExtent(lleftX, lleftY, urightX, urightY);
        }
    }
    
    /**
     * Zooms to maximum zoom extent available from the bounding coordinates of the given
     * {@link PropertiesManager} instance. If parameters are not parsable to {@link Double}
     * {@link #setDefaultMapExtent()} will reset the map to default extent/zoom-level.
     * 
     * @param llX
     *        lower left of X-axis.
     * @param llY
     *        lower left of Y-axis.
     * @param urX
     *        upper right of X-axis.
     * @param urY
     *        upper right of Y-axis.
     */
    private void zoomToMaxExtent(String llX, String llY, String urX, String urY) {
        try {
            Bounds b = new Bounds(new Double(llX), new Double(llY), new Double(urX), new Double(urY));
            String mapProjection = OverviewMap.this.getMapProjection();
            LonLat centerLonLat = b.getCenterLonLat();
            if (!mapProjection.equals(Constants.DISPLAY_PROJECTION)) {
                centerLonLat.transform(Constants.DISPLAY_PROJECTION, mapProjection);
                LonLat lowerleft = new LonLat(new Double(llX), new Double(llY));
                LonLat upperright = new LonLat(new Double(urX), new Double(urY));
                lowerleft.transform(Constants.DISPLAY_PROJECTION, mapProjection);
                upperright.transform(Constants.DISPLAY_PROJECTION, mapProjection);
                b = new Bounds(lowerleft.lon(),lowerleft.lat(),upperright.lon(),upperright.lat());
            }
            OverviewMap.this.map.setCenter(centerLonLat);
            OverviewMap.this.map.zoomToExtent(b);
        }
        catch (NumberFormatException e) {
            if ( !GWT.isProdMode()) {
                StringBuilder sb = new StringBuilder("Could not parse bbox coordinates: ");
                sb.append("llX=" + llX + ", ");
                sb.append("llY=" + llY + ", ");
                sb.append("urX=" + urX + ", ");
                sb.append("urY=" + urY);
                GWT.log(sb.toString(), e);
            }
        }
    }

    public class OverviewMapEventBroker implements InitEventHandler {

        public OverviewMapEventBroker() {
            EventBus.getMainEventBus().addHandler(InitEvent.TYPE, this);
        }

        public void onInit(InitEvent evt) {
            OverviewMap.this.initMap();
        }

    }
}
