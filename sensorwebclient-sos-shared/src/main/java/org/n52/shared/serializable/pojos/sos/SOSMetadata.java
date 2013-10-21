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
import java.util.HashMap;
import java.util.List;

import org.n52.io.crs.BoundingBox;
import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashIdGenerator;
import org.n52.shared.requests.query.QueryParameters;

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

    private String serviceUrl; // mandatory

    private String version; // mandatory

    private String title = "NA";

    private String sosMetadataHandler;

    private String adapter;

    private boolean initialized = false;

    private String sensorMLVersion;

    private String omVersion;

    private TimeseriesParametersLookup timeseriesParametersLookup;

    private HashMap<String, Station> stations = new HashMap<String, Station>();

    private boolean hasDonePositionRequest = false;

    private String configuredItemName;

    private String srs;

    private boolean canGeneralize = false; // default

    private boolean waterML = false; // default

    private boolean autoZoom = true; // default

    private boolean protectedService = false; // default

    private int requestChunk = 300; // default

    private boolean forceXYAxisOrder = false; // default

    private boolean supportsFirstLatest = false; // default

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
    public SOSMetadata(String serviceUrl) {
        this.serviceUrl = serviceUrl;
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
        this.supportsFirstLatest = builder.isSupportsFirstLatest();
        this.requestChunk = builder.getRequestChunk();
        this.configuredExtent = builder.getConfiguredServiceExtent();
        this.protectedService = builder.isProctectedService();
        this.setSosMetadataHandler(builder.getSosMetadataHandler());
        this.setAdapter(builder.getAdapter());
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
        return serviceUrl;
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

    public boolean isSupportsFirstLatest() {
        return supportsFirstLatest;
    }

    public boolean isProtectedService() {
        return protectedService;
    }

    public int getRequestChunk() {
        return requestChunk;
    }

    /**
     * @return the service's extent.
     */
    public BoundingBox getConfiguredExtent() {
        return configuredExtent;
    }

    public void addStation(Station station) {
        stations.put(station.getLabel(), station);
    }

    public ArrayList<Station> getStations() {
        return new ArrayList<Station>(stations.values());
    }

    public SosTimeseries[] getMatchingTimeseries(QueryParameters parameters) {
        List<SosTimeseries> matchingTimeseries = new ArrayList<SosTimeseries>();
        for (Station station : stations.values()) {
            for (SosTimeseries timeseries : station.getObservedTimeseries()) {
                if (timeseries.matchesGlobalIds(parameters)) {
                    matchingTimeseries.add(timeseries);
                }
            }
        }
        return matchingTimeseries.toArray(new SosTimeseries[0]);
    }

    public Station getStationByTimeSeriesId(String timeseriesId) {
        for (Station station : stations.values()) {
            if (station.contains(timeseriesId)) {
                return station;
            }
        }
        return null;
    }

    public boolean containsStationWithTimeseriesId(String timeseriesId) {
        for (Station station : stations.values()) {
            if (station.contains(timeseriesId)) {
                return true;
            }
        }
        return false;
    }

    public Station getStationByTimeSeries(SosTimeseries timeseries) {
        for (Station station : stations.values()) {
            if (station.contains(timeseries)) {
                return station;
            }
        }
        return null;
    }

    public boolean containsTimeseriesWith(SosTimeseries timeseries) {
        for (Station station : stations.values()) {
            if (station.contains(timeseries)) {
                return true;
            }
        }
        return false;
    }

    public Station getStation(String stationId) {
        return stations.get(stationId);
    }

    /**
     * @return a lookup helper for timeseries parameters.
     */
    public TimeseriesParametersLookup getTimeseriesParametersLookup() {
        timeseriesParametersLookup = timeseriesParametersLookup == null
            ? new TimeseriesParametersLookup()
            : timeseriesParametersLookup;
        return timeseriesParametersLookup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SOSMetadata [ ");
        sb.append("parameterId: ").append(serviceUrl).append(", ");
        sb.append("initialized: ").append(initialized).append(", ");
        sb.append("version: ").append(version);
        sb.append(" ]");
        return sb.toString();
    }

    public SOSMetadata clone() {
        SOSMetadata clone = new SOSMetadata(this.serviceUrl,
                                            this.version,
                                            this.sensorMLVersion,
                                            this.omVersion,
                                            this.title);
        clone.waterML = this.waterML;
        clone.autoZoom = this.autoZoom;
        clone.forceXYAxisOrder = this.forceXYAxisOrder;
        clone.requestChunk = this.requestChunk;
        clone.configuredExtent = this.configuredExtent;
        clone.protectedService = this.protectedService;
        clone.setSosMetadataHandler(this.getSosMetadataHandler());
        clone.setAdapter(this.getAdapter());
        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (serviceUrl == null) ? 0 : serviceUrl.hashCode());
        result = prime * result + ( (version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SOSMetadata other = (SOSMetadata) obj;
        if (serviceUrl == null) {
            if (other.serviceUrl != null)
                return false;
        }
        else if ( !serviceUrl.equals(other.serviceUrl))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        }
        else if ( !version.equals(other.version))
            return false;
        return true;
    }

    public String getGlobalId() {
        String[] parameters = new String[] {serviceUrl, version};
        IdGenerator idGenerator = new MD5HashIdGenerator("srv_");
        return idGenerator.generate(parameters);
    }
}
