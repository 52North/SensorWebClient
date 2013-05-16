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

package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;

/**
 * A shared metadata representation for an SOS instance. An {@link SOSMetadata} is used from both (!) Client
 * side and Server side. Depending on if the SOS metadata representation is used on either Client sider or
 * Server side, attributes have to be set differently (see constructor notes) ! It is the developer's
 * responsibility to keep them in sync.
 * 
 * TODO this above fact is based on historical reasons and have to refactored!
 */
public class SOSMetadata implements Serializable {

    private static final long serialVersionUID = -3721927620888635622L;

    private String id; // mandatory

    private String version; // mandatory

    private String title = "NA";

    private String sosMetadataHandler;

    private String adapter;

    private boolean initialized = false;

    private String sensorMLVersion;

    private String omVersion;
    
    private TimeseriesParametersLookup timeseriesParamtersLookup;

    private HashMap<String, Station> stations = new HashMap<String, Station>();

    private boolean hasDonePositionRequest = false;

    private String configuredItemName;

    private String srs;

    private boolean canGeneralize = false; // default

    private boolean waterML = false; // default

    private boolean autoZoom = true; // default

    private int requestChunk = 100; // default

    private boolean forceXYAxisOrder = false; // default

    private BoundingBox configuredExtent;

    @SuppressWarnings("unused")
    private SOSMetadata() {
        // for serialization
    }

    public SOSMetadata(String url, String sosVersion, String sensorMLVersion, String omVersion, String title) {
        this(url);
        this.title = title;
        this.version = sosVersion;
        this.sensorMLVersion = sensorMLVersion;
        this.omVersion = omVersion;
    }

    @Deprecated
    public SOSMetadata(String id, String title) {
        this(id);
        this.title = title;
    }

    /**
     * @deprecated use {@link #SOSMetadata(String, String)}
     * 
     * @see {@link #SOSMetadata(String, String)} to explicitly set version
     */
    @Deprecated
    public SOSMetadata(String id) {
        this.id = id;
    }

    /**
     * Use this constructor only for non-configurated SOS instances! Prefer using
     * {@link SOSMetadata#SOSMetadata(SOSMetadataBuilder)}.
     * 
     * @param url
     *        the service URL
     * @param title
     *        A service title
     * @param version
     *        the supported version
     */
    public SOSMetadata(String url, String title, String version) {
        this(url, title);
        this.version = version;
    }

    public SOSMetadata(SOSMetadataBuilder builder) {
        this(builder.getServiceURL(), builder.getServiceName());
        this.version = builder.getServiceVersion();
        this.configuredItemName = builder.getServiceName();
        this.waterML = builder.isWaterML();
        this.autoZoom = builder.isAutoZoom();
        this.forceXYAxisOrder = builder.isForceXYAxisOrder();
        this.requestChunk = builder.getRequestChunk();
        this.configuredExtent = builder.getConfiguredServiceExtent();
        this.setSosMetadataHandler(builder.getSosMetadataHandler());
        this.setAdapter(builder.getAdapter());
    }

    public String getId() {
        return id;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Indicates that the metadata has been filled with data requested from service.
     */
    public boolean isInitialized() {
        return initialized;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the configured SOS metadata handler or <code>null</code> when called from client side.
     */
    public String getSosMetadataHandler() {
        return sosMetadataHandler;
    }

    public void setSosMetadataHandler(String handler) {
        // is null when used on client side
        this.sosMetadataHandler = handler != null ? handler.trim() : null;
    }

    /**
     * @return the configured SOS adapter or <code>null</code> when called from client side.
     */
    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        // is null when used on client side
        this.adapter = adapter != null ? adapter.trim() : null;
    }

    public String getServiceUrl() {
        return getId();
    }

    public String getSrs() {
        return this.srs;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfiguredItemName() {
        return configuredItemName;
    }

    public String getSosVersion() {
        return getVersion();
    }

    public String getSensorMLVersion() {
        return this.sensorMLVersion;
    }

    public String getOmVersion() {
        return this.omVersion;
    }

    public void setSosVersion(String sosVersion) {
        setVersion(sosVersion);
    }

    public void setSensorMLVersion(String sensorMLVersion) {
        this.sensorMLVersion = sensorMLVersion;
    }

    public void setOmVersion(String omVersion) {
        this.omVersion = omVersion;
    }

    public boolean hasDonePositionRequest() {
        return this.hasDonePositionRequest;
    }

    public void setHasDonePositionRequest(boolean hasDonePositionRequest) {
        this.hasDonePositionRequest = hasDonePositionRequest;
    }

    
    public boolean canGeneralize() {
        return this.canGeneralize;
    }

    public void setCanGeneralize(boolean canGeneralize) {
        this.canGeneralize = canGeneralize;
    }

    public boolean isWaterML() {
        return waterML;
    }

    public boolean isAutoZoom() {
        return autoZoom;
    }

    public boolean isForceXYAxisOrder() {
        return forceXYAxisOrder;
    }

    public int getRequestChunk() {
        return requestChunk;
    }

    /**
     * @return the service's extent or the {@link Constants#FALLBACK_EXTENT} if it was not configured.
     */
    public BoundingBox getConfiguredExtent() {
        return configuredExtent;
    }

    public void addStation(Station station) {
        stations.put(station.getId(), station);
    }

    public Collection<Station> getStations() {
        return new ArrayList<Station>(this.stations.values());
    }
    
    public Station getStationByParameterConstellation(ParameterConstellation parameterConstellation) {
        for (Station station : stations.values()) {
            if (station.contains(parameterConstellation)) {
                return station;
            }
        }
        return null;
    }

    public boolean containsTimeseriesWith(ParameterConstellation parameterConstellation) {
        for (Station station : stations.values()) {
            if (station.contains(parameterConstellation)) {
                return true;
            }
        }
        return false;
    }

    public Station getStation(String id) {
        return stations.get(id);
    }

    /**
     * @return a lookup helper for timeseries parameters.
     */
    public TimeseriesParametersLookup getTimeseriesParamtersLookup() {
        timeseriesParamtersLookup = timeseriesParamtersLookup == null 
                ? new TimeseriesParametersLookup()
                : timeseriesParamtersLookup;
        return timeseriesParamtersLookup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SOSMetadata [ ");
        sb.append("parameterId: ").append(id).append(", ");
        sb.append("initialized: ").append(initialized).append(", ");
        sb.append("version: ").append(version);
        sb.append(" ]");
        return sb.toString();
    }

}
