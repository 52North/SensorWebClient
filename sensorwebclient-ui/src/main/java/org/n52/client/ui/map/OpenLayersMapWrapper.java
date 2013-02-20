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

package org.n52.client.ui.map;

import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.shared.Constants.DISPLAY_PROJECTION;
import static org.n52.shared.Constants.EPSG_4326;
import static org.n52.shared.Constants.GOOGLE_PROJECTION;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapUnits;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.layer.LayerOptions;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ui.Toaster;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;

import com.google.gwt.core.client.GWT;

public abstract class OpenLayersMapWrapper {
    
    protected static int DEFAULT_ZOOM_LEVEL = 13; // default

    protected MapOptions defaultMapOptions = new MapOptions();

    public static String currentMapProjection;

    private String spatialReference = Constants.EPSG_4326;

    private MapWidget mapWidget;

    private int width = 300;

    private int height = 350;

    protected Map map;

    protected OSM osm;

    public OpenLayersMapWrapper() {
        this("100%");
    }

    public OpenLayersMapWrapper(String cssHeight) {
        initializeMapWidget(cssHeight);
        initializeBackgroundMapLayer();
        map.zoomTo(DEFAULT_ZOOM_LEVEL);
    }

    private void initializeMapWidget(String cssheight) {
        initializeDefaultMapOptions();
        mapWidget = new MapWidget("100%", cssheight, defaultMapOptions);
        map = mapWidget.getMap();
        addMapControls(defaultMapOptions);
    }

    private void initializeDefaultMapOptions() {
        defaultMapOptions.setMaxResolutionToAuto();
        defaultMapOptions.setNumZoomLevels(18);
        defaultMapOptions.setUnits(MapUnits.DEGREES);
        defaultMapOptions.setDisplayProjection(new Projection(DISPLAY_PROJECTION));
    }

    /**
     * Override, if you want custom controls on the map. Per default the {@link Navigation} control is added
     * only.
     * 
     * @param mapOptions
     *        the map options where to set the map controls
     */
    protected void addMapControls(MapOptions mapOptions) {
        mapOptions.removeDefaultControls();
        map.addControl(new Navigation());
    }

    private void initializeBackgroundMapLayer() {
        PropertiesManager properties = getPropertiesManager();
        spatialReference = properties.getParameterAsString("mapSrs");
        String url = properties.getParameterAsString("mapUrl");
        url = url == null ? "OSM" : url; // if not set in config
        if ("OSM".equalsIgnoreCase(url)) {
            map.addLayer(initializeOSMLayer());
        }
        else {
            try {
                map.addLayer(initializeWMSLayer(url));
            }
            catch (Exception e) {
                // fallback to default
                String message = "Could not create WMS layer.";
                Toaster.getToasterInstance().addErrorMessage(message);
                map.addLayer(initializeOSMLayer());
            }
        }
    }

    private WMS initializeWMSLayer(String url) {
        PropertiesManager properties = getPropertiesManager();
        defaultMapOptions.setProjection(DISPLAY_PROJECTION);
        currentMapProjection = DISPLAY_PROJECTION;

        String format = properties.getParameterAsString("wmsFormat");
        String styles = properties.getParameterAsString("wmsStyles");
        String layer = properties.getParameterAsString("wmsLayerName");
        String bgColor = properties.getParameterAsString("wmsBGColor");
        String isTransparent = properties.getParameterAsString("wmsIsTransparent");

        WMSParams wmsParameters = new WMSParams();
        wmsParameters.setFormat(format);
        wmsParameters.setLayers(layer);
        wmsParameters.setStyles(styles);
        wmsParameters.setIsTransparent(new Boolean(isTransparent));
        wmsParameters.getJSObject().setProperty("BGCOLOR", bgColor);

        WMSOptions wmsOptions = new WMSOptions();
        wmsOptions.setProjection(spatialReference);
        wmsOptions.setDisplayInLayerSwitcher(true);
        wmsOptions.setIsBaseLayer(true);
        return new WMS(layer, url, wmsParameters, wmsOptions);
    }

    private OSM initializeOSMLayer() {
        defaultMapOptions.setProjection(GOOGLE_PROJECTION);
        currentMapProjection = GOOGLE_PROJECTION; // google's mercartor prj

        // osm = OSM.Osmarender("Osmarender");
        osm = OSM.Mapnik("Mapnik");
        osm.setDisplayInLayerSwitcher(true);
        osm.setIsBaseLayer(true);

        LayerOptions layerOptions = new LayerOptions();
        layerOptions.setProjection(spatialReference);
        osm.addOptions(layerOptions);
        return osm;
    }

    public BoundingBox getCurrentExtent() {
        Bounds bbox = map.getExtent();
        LonLat ll = new LonLat(bbox.getLowerLeftX(), bbox.getLowerLeftY());
        LonLat ur = new LonLat(bbox.getUpperRightX(), bbox.getUpperRightY());
        
        if (!GWT.isProdMode()) {
            StringBuilder sb = new StringBuilder("Transforming: \n");
            sb.append("ll: ").append(getAsString(ll)).append(", ");
            sb.append("ur: ").append(getAsString(ur));
            GWT.log(sb.append("...").toString());
        }

        ll.transform(getMapProjection(), EPSG_4326);
        ur.transform(getMapProjection(), EPSG_4326);
        
        if (!GWT.isProdMode()) {
            StringBuilder sb = new StringBuilder("... transformed to: \n");
            sb.append("ll: ").append(getAsString(ll)).append(", ");
            sb.append("ur: ").append(getAsString(ur));
            GWT.log(sb.toString());
        }
        
        EastingNorthing ll2 = new EastingNorthing(ll.lon(), ll.lat());
        EastingNorthing ur2 = new EastingNorthing(ur.lon(), ur.lat());
        return new BoundingBox(ll2, ur2, EPSG_4326);
    }

    private String getAsString(LonLat lonlat) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(lonlat.lon());
        sb.append(",").append(lonlat.lat()).append(")");
        return sb.toString();
    }

    public MapOptions getDefaultMapOptions() {
        return defaultMapOptions;
    }

    public void setDefaultMapOptions(MapOptions defaultMapOptions) {
        this.defaultMapOptions = defaultMapOptions;
    }

    public MapWidget getMapWidget() {
        return mapWidget;
    }

    public void setMapWidget(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }

    public Map getMap() {
        return map;
    }

    public LonLat getCenter() {
        return map.getCenter();
    }

    public void setCenter(LonLat center) {
        map.setCenter(center);
    }
    
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMapProjection() {
        return currentMapProjection;
    }

    public String getDisplayProjection() {
        return DISPLAY_PROJECTION;
    }

    public void setCenter(LonLat coords, int zoom) {
        map.setCenter(coords, zoom);
    }

    public void destroy() {
        map.destroy();
    }

    public void resizeTo(int width, int height) {
        mapWidget.setHeight(height + "px");
        mapWidget.setWidth(width + "px");
        this.width = width;
        this.height = height;
    }

    /**
     * @return <code>true</code> when client's properties configure a default (global) extent,
     *         <code>false</code> if no extent was configured in the global properties file.
     */
    protected boolean isDefinedGlobalExtent() {
        PropertiesManager propertiesMgr = getPropertiesManager();
        return propertiesMgr.getParameterAsString("defaultExtent") != null;
    }

}
