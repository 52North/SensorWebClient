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

import static org.n52.shared.Constants.DISPLAY_PROJECTION;
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
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;

import com.google.gwt.core.client.GWT;

public class StationSelectorMap extends OpenLayersMapWrapper {

    private final MapController controller;

    private BoundingBox defaultExtent;

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
                PropertiesManager propertiesMgr = PropertiesManager.getInstance();
                double lleftX = new Double(propertiesMgr.getParameterAsString("lleftX"));
                double lleftY = new Double(propertiesMgr.getParameterAsString("lleftY"));
                double urightX = new Double(propertiesMgr.getParameterAsString("urightX"));
                double urightY = new Double(propertiesMgr.getParameterAsString("urightY"));
                EastingNorthing ll = new EastingNorthing(lleftX, lleftY);
                EastingNorthing ur = new EastingNorthing(urightX, urightY);
                defaultExtent = new BoundingBox(ll, ur, DISPLAY_PROJECTION);
            }
            else {
                GWT.log("No global extent configured. Zooming to: " + Constants.FALLBACK_EXTENT);
                defaultExtent = Constants.FALLBACK_EXTENT;
            }
        }
        catch (NumberFormatException e) {
            GWT.log("Error while parsing configured bounding box. Zooming to: " + Constants.FALLBACK_EXTENT);
            defaultExtent = Constants.FALLBACK_EXTENT;
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
            addStationToMap(station);
        }
    }

    public void addStationToMap(final Station station) {
        InfoMarker infoMarker = InfoMarker.createInfoMarker(station, controller);
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
            if (marker.getStation().getStationCategory().equals(filterCategory)) {
                markerLayer.addMarker(marker);
            }
        }
    }

    public void zoomToExtent(BoundingBox bbox) {
        String srs = bbox.getSrs();
        String destSrs = getMapProjection();
        EastingNorthing ll = bbox.getLowerLeftCorner();
        EastingNorthing ur = bbox.getUpperRightCorner();
        LonLat lowerleft = new LonLat(ll.getEasting(), ll.getNorthing());
        LonLat upperright = new LonLat(ur.getEasting(), ur.getNorthing());
        if ( !srs.equalsIgnoreCase(destSrs)) {
            lowerleft.transform(srs, destSrs);
            upperright.transform(srs, destSrs);
        }
        map.zoomToExtent(new Bounds(lowerleft.lon(), lowerleft.lat(), upperright.lon(), upperright.lat()));
    }

    /**
     * @return the default extent if configured. If no extent is configured the default extent probably has
     *         been set to {@link Constants#FALLBACK_EXTENT}.
     */
    public BoundingBox getDefaultExtent() {
        return defaultExtent;
    }

}