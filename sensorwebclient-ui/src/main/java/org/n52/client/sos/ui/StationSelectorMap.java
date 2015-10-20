/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.client.ui.map.InfoMarker.createInfoMarker;
import static org.n52.shared.Constants.EPSG_4326;

import java.util.ArrayList;
import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.layer.Markers;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ui.map.InfoMarker;
import org.n52.client.ui.map.MapController;
import org.n52.client.ui.map.OpenLayersMapWrapper;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;

import com.google.gwt.core.client.GWT;

public class StationSelectorMap extends OpenLayersMapWrapper {
    
    /**
     * A fall back extent if no other extent was configured (data source instance, or global).
     */
    static final Bounds FALLBACK_EXTENT = new Bounds(-180d, -90d, 180d, 90d);

    private final MapController controller;

    private Bounds defaultExtent;

    private ArrayList<InfoMarker> markersOnMap = new ArrayList<InfoMarker>();

    private HashMap<Number, Number> markersPositions = new HashMap<Number, Number>();

    private Markers markerLayer = new Markers("markers");

    public StationSelectorMap(MapController controller) {
        super("523px"); // XXX map needs explicit height in px
        getMapWidget().setStylePrimaryName("n52-sensorwebclient-stationselector-map");
        this.controller = controller;
        map.addLayer(markerLayer);
        try {
            if (isDefinedGlobalExtent()) {
                PropertiesManager propertiesMgr = getPropertiesManager();
                double lleftX = new Double(propertiesMgr.getParameterAsString("lleftX"));
                double lleftY = new Double(propertiesMgr.getParameterAsString("lleftY"));
                double urightX = new Double(propertiesMgr.getParameterAsString("urightX"));
                double urightY = new Double(propertiesMgr.getParameterAsString("urightY"));
                defaultExtent = new Bounds(lleftX, lleftY, urightX, urightY);
            }
            else {
                GWT.log("No global extent configured. Zooming to: " + FALLBACK_EXTENT);
                defaultExtent = FALLBACK_EXTENT;
            }
        }
        catch (NumberFormatException e) {
            GWT.log("Error while parsing configured bounding box. Zooming to: " + FALLBACK_EXTENT);
            defaultExtent = FALLBACK_EXTENT;
        }
        zoomToExtent(defaultExtent);
    }
    
    @Override
    protected void addMapControls(MapOptions mapOptions) {
        super.addMapControls(mapOptions);
        MousePositionOptions options = new MousePositionOptions();
        options.setFormatOutput(new MousePositionOutput() {
            
            @Override
            public String format(LonLat lonLat, Map map) {
                lonLat.transform(getMapProjection(), EPSG_4326);
                StringBuilder sb = new StringBuilder();
                sb.append("Lon: ").append(lonLat.lon()).append(", ");
                sb.append("Lat: ").append(lonLat.lat());
                sb.append(" (").append(map.getProjection()).append(")");
                return sb.toString();
                
            }
        });
        map.addControl(new MousePosition(options));
    }



    public void addMarker(final InfoMarker marker) {
        double easting = marker.getLonLat().lon();
        double northing = marker.getLonLat().lat();
        markersPositions.put(easting, northing);
        markerLayer.addMarker(marker);
        markersOnMap.add(marker);
    }

    public Markers getMarkerLayer() {
        return markerLayer;
    }

    /**
     * Clears markers from map.
     */
    public void clearMap() {
        removeAllMarkers();
    }

    public ArrayList<InfoMarker> getMarkersOnMap() {
        return this.markersOnMap;
    }

    public void removeAllMarkers() {
        clearMarkerLayer();
        this.markersPositions.clear();
        this.markersOnMap.clear();

    }

    public boolean containsMarker(LonLat coords) {
        return (this.markersPositions.containsKey(coords.lon()) && this.markersPositions.containsValue(coords.lat()));
    }

    public void removeMarker(Marker newMarker) {
        this.markersPositions.remove(newMarker.getLonLat().lon());
        this.markersOnMap.remove(newMarker);
        this.markerLayer.removeMarker(newMarker);
    }

    public void zoomToMarkers() {
        try {
            if (markerLayer != null) {
                Bounds bbox = markerLayer.getDataExtent();
                if (bbox != null) {
                    int z = map.getZoomForExtent(bbox, false);
                    map.zoomToExtent(bbox);
                    map.zoomTo(z);
                }
            }
        }
        catch (Exception e) {
            if ( !GWT.isProdMode()) {
                GWT.log("", e);
            }
        }
    }

    public void zoomToMarkers(Markers markers) {
        map.zoomToExtent(markers.getDataExtent());
    }

    public void updateStations(final SOSMetadata metadata) {
        clearMap();
        for (Station station : metadata.getStations()) {
        	if (station.getObservedTimeseries().size() >= 0) {
        		addStationToMap(station);
			}
        }
    }

    public void addStationToMap(final Station station) {
        InfoMarker infoMarker = createInfoMarker(station, controller);
        infoMarker.registerHoverHandler();
        addMarker(infoMarker);
    }

    public void selectMarker(InfoMarker infoMarker) {
        unmarkAllMarkers();
        infoMarker.select();
    }

    public void unmarkAllMarkers() {
        for (InfoMarker marker : markersOnMap) {
            marker.deselect();
        }
    }

    private void clearMarkerLayer() {
        unmarkAllMarkers();
        for (Marker m : this.markersOnMap) {
            this.markerLayer.removeMarker(m);
        }
    }

    public void applyFilterToStationsOnMap(String filterCategory) {
        clearMarkerLayer();
        for (InfoMarker marker : markersOnMap) {
            if (marker.getStation().hasStationCategoryLabel(filterCategory)) {
                markerLayer.addMarker(marker);
            }
        }
    }

    /**
     * @param bounds an extent to zoom to.
     */
    public void zoomToExtent(Bounds bounds) {
        map.zoomToExtent(bounds);
    }

    /**
     * @return the default extent if configured. If no extent is configured the default extent probably has
     *         been set to {@link FALLBACK_EXTENT}.
     */
    public Bounds getDefaultExtent() {
        return defaultExtent;
    }

}