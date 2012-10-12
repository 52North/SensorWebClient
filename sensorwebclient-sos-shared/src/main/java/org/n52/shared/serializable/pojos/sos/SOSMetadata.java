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
/**********************************************************************************
 Copyright (C) 2010
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk 
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under the
 terms of the GNU General Public License version 2 as published by the Free
 Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with this 
 program (see gnu-gplv2.txt). If not, write to the Free Software Foundation, Inc., 
 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or visit the Free Software
 Foundation web page, http://www.fsf.org.

 Created on: 12.05.2010
 *********************************************************************************/

package org.n52.shared.serializable.pojos.sos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.ServiceMetadata;

/**
 * A shared metadata representation for an SOS instance. An {@link SOSMetadata} is used from both (!) Client
 * side and Server side. Depending on if the SOS metadata representation is used on either Client sider or
 * Server side, attributes have to be set differently (see constructor notes) ! It is the developer's
 * responsibility to keep them in sync.
 * 
 * TODO this above fact is based on historical reasons and have to refactored!
 */
public class SOSMetadata extends ServiceMetadata {
    
    private static final long serialVersionUID = -3721927620888635622L;

    private String sensorMLVersion;

    private String omVersion;

    private HashMap<String, FeatureOfInterest> features = new HashMap<String, FeatureOfInterest>();

    private HashMap<String, Phenomenon> phenomenons = new HashMap<String, Phenomenon>();

    private HashMap<String, Procedure> procedures = new HashMap<String, Procedure>();

    private HashMap<String, Offering> offerings = new HashMap<String, Offering>();

    private HashMap<String, Station> stations = new HashMap<String, Station>();

    private boolean hasDonePositionRequest = false;

    private String title;
    
    private String configuredItemName;

    private String srs;

    private boolean canGeneralize = false;

    private boolean waterML = false;

    private boolean autoZoom = true;

    private int requestChunk;

    private BoundingBox configuredExtent;

    @SuppressWarnings("unused")
    private SOSMetadata() {
        // do nothing
    }

    public SOSMetadata(String url, String sosVersion, String sensorMLVersion, String omVersion, String title) {
        super(url, sosVersion);
        this.title = title;
        this.sensorMLVersion = sensorMLVersion;
        this.omVersion = omVersion;
    }

    @Deprecated
    public SOSMetadata(String id, String title) {
        super(id);
        this.title = title;
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
        super(url, version);
        this.title = title;
    }

    public SOSMetadata(SOSMetadataBuilder builder) {
        super(builder.getServiceURL(), builder.getServiceVersion().trim());
        this.configuredItemName = builder.getServiceName();
        this.waterML = builder.isWaterML();
        this.autoZoom = builder.isAutoZoom();
        this.requestChunk = builder.getRequestChunk();
        this.configuredExtent = builder.getConfiguredServiceExtent();
        this.setConnector(builder.getConnector());
        this.setAdapter(builder.getAdapter());
    }

    public String getServiceUrl() {
        return getId();
    }

    // public SOSMetadata copy() {
    // SOSMetadata meta = new SOSMetadata(getId(), getSosVersion(), sensorMLVersion, omVersion, title);
    // meta.features = (HashMap<String, FeatureOfInterest>) this.features.clone();
    // meta.procedures = (HashMap<String, Procedure>) this.procedures.clone();
    // meta.phenomenons = (HashMap<String, Phenomenon>) this.phenomenons.clone();
    // meta.offerings = (HashMap<String, Offering>) this.offerings.clone();
    // meta.stations = this.stations;
    // meta.setConnector(this.getConnector());
    // meta.autoZoom = this.autoZoom;
    // meta.sosSpecificBboxConfigured = this.sosSpecificBboxConfigured;
    // meta.configuredExtent = this.configuredExtent;
    // meta.hasDonePositionRequest = this.hasDonePositionRequest;
    // return meta;
    // }

    public String getSrs() {
        return this.srs;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public void removeProcedure(String ID) {
        Set<Entry<String, Station>> entrySet = stations.entrySet();
        for (Entry<String, Station> entry : entrySet) {
            if ( !entry.getValue().isProcedureEqual(ID)) {
                stations.remove(entry.getKey());
                return;
            }
        }
    }

    public HashMap<String, Procedure> getProceduresHashMap() {
        return this.procedures;
    }

    public HashMap<String, FeatureOfInterest> getFeatureHashMap() {
        return this.features;
    }

    public HashMap<String, Phenomenon> getPhenomenonHashMap() {
        return this.phenomenons;
    }

    public SOSMetadata getOfferingChunk() {
        SOSMetadata meta = new SOSMetadata(getId(), getSosVersion(), sensorMLVersion, omVersion, title);

        // insert only offerings
        for (Offering o : offerings.values()) {
            Offering off = new Offering(o.getId());
            meta.addOffering(off);
        }
        return meta;
    }

    public SOSMetadata getFeatureChunk() {
        SOSMetadata meta = new SOSMetadata(getId(), getSosVersion(), sensorMLVersion, omVersion, title);

        // insert only features references
        for (Offering o : this.offerings.values()) {
            Offering off = new Offering(o.getId());
            meta.addOffering(off);
        }

        return meta;
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSOS URL: ").append(getId()).append("\n");
        sb.append("\tversion: ").append(getSosVersion()).append("\n");
        sb.append("\tsensorML version: ").append(this.sensorMLVersion).append("\n");
        int offs = 10;
        sb.append("\tFirst ").append(offs).append(" Offerings of ").append(this.offerings.size()).append(" :\n");
        for (Offering off : this.offerings.values()) {
            sb.append("\t\t").append(off.getLabel()).append("\n");
            offs--;
            if (offs == 0) {
                break;
            }
        }
        sb.append("bbox: ").append(getConfiguredExtent());
        return sb.toString();
    }

    public void addOffering(Offering off) {
        if ( !this.offerings.containsKey(off.getId())) {
            this.offerings.put(off.getId(), off);
        }
    }

    public Procedure getProcedure(String ID) {
        return this.procedures.get(ID);
    }

    public ArrayList<Offering> getOfferings() {
        ArrayList<Offering> offs = new ArrayList<Offering>(this.offerings.values());
        return offs;
    }

    public HashMap<String, Offering> getOfferingHashMap() {
        return this.offerings;
    }

    public Offering getOffering(String ID) {
        return this.offerings.get(ID);
    }

    public FeatureOfInterest getFeature(String ID) {
        return this.features.get(ID);
    }

    public Collection<FeatureOfInterest> getFeatures() {
        ArrayList<FeatureOfInterest> features = new ArrayList<FeatureOfInterest>(this.features.values());
        return features;
    }

    public Collection<Phenomenon> getPhenomenons() {
        return new ArrayList<Phenomenon>(this.phenomenons.values());
    }

    public Phenomenon getPhenomenon(String ID) {
        return this.phenomenons.get(ID);
    }

    public ArrayList<Procedure> getProcedures() {
        ArrayList<Procedure> procs = new ArrayList<Procedure>();
        procs.addAll(this.procedures.values());
        return procs;
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

    public void addProcedure(Procedure p) {
        this.procedures.put(p.getId(), p);
    }

    public void addPhenomenon(Phenomenon phenomenon) {
        this.phenomenons.put(phenomenon.getId(), phenomenon);
    }

    public void addFeature(FeatureOfInterest f) {
        this.features.put(f.getId(), f);
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

    /**
     * Used to set from Client side if a map shall auto zoom to the provided station markers.
     * 
     * @param autoZoom
     */
    public void setAutoZoom(boolean autoZoom) {
        this.autoZoom = autoZoom;
    }

    public boolean isAutoZoom() {
        return autoZoom;
    }
    
    public int getRequestChunk() {
        return requestChunk;
    }

    /**
     * Used to set the sos extent from Client side.
     * 
     * @param configuredExtent
     *        the parsed extent.
     */
    public void setConfiguredExtent(BoundingBox configuredExtent) {
        this.configuredExtent = configuredExtent;
    }

    /**
     * @return the service's extent or the {@link Constants#FALLBACK_EXTENT} if it was not configured.
     */
    public BoundingBox getConfiguredExtent() {
        return configuredExtent;
    }

    @Override
    public String toString() {
        return "SOSMetadata [url: '" + getId() + "']";
    }

    public void addStation(Station station) {
        this.stations.put(station.getId(), station);
    }

    public Collection<Station> getStations() {
        return new ArrayList<Station>(this.stations.values());
    }

    public Set<Station> getStationsByProcedure(String procedureID) {
        Set<Station> result = new HashSet<Station>();
        for (Station station : stations.values()) {
            if (station.isProcedureEqual(procedureID)) {
                result.add(station);
            }
        }
        return result;
    }

    public Set<Station> getStationsByFeatureID(String foi) {
        Set<Station> result = new HashSet<Station>();
        for (Station station : stations.values()) {
            if (station.isFeatureEqual(foi)) {
                result.add(station);
            }
        }
        return result;
    }

    public Set<Station> getStations(String phenomenon, String feature) {
        Set<Station> result = new HashSet<Station>();
        for (Station station : stations.values()) {
            if (station.isPhenomenonEqual(phenomenon) && station.isFeatureEqual(feature)) {
                result.add(station);
            }
        }
        return result;
    }

    public Station getStation(String offeringId, String featureId, String procedureId, String phenomenonId) {
        for (Station station : stations.values()) {
            if (station.isOfferingEqual(offeringId) && station.isFeatureEqual(featureId)
                    && station.isProcedureEqual(procedureId) && station.isPhenomenonEqual(phenomenonId)) {
                return station;
            }
        }
        return null;
    }

    public Station getStation(String id) {
        return stations.get(id);
    }

    public void removeStations(Set<Station> stations) {
        for (Station station : stations) {
            this.stations.remove(station.getId());
        }
    }

    public void removeStation(Station station) {
        this.stations.remove(station.getId());
    }

}
