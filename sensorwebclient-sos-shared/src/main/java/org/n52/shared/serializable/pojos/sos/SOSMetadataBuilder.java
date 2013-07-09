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

import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;

public class SOSMetadataBuilder {

    private String serviceURL;

    private String serviceVersion;

    private String serviceName;

    private String sosMetadataHandler;

    private String adapter;

    private boolean waterML = false;

    private boolean autoZoom = true;
    
    private boolean forceXYAxisOrder = false;
    
    private boolean protectedService = false;

    private int requestChunk = 100;
    
    private double llEasting = Double.NaN;

    private double llNorthing = Double.NaN;

    private double urEasting = Double.NaN;

    private double urNorthing = Double.NaN;

    private boolean sosSpecificBboxConfigured;

    public SOSMetadataBuilder() {
        // default
    }
    
    public SOSMetadata build() {
        return new SOSMetadata(this);
    }

    public SOSMetadataBuilder addServiceURL(String serviceURL) {
        if (serviceURL == null || serviceURL.isEmpty()) {
            throw new NullPointerException("serviceURL parameter must not be null or empty.");
        }
        this.serviceURL = serviceURL.trim();
        return this;
    }

    public SOSMetadataBuilder addServiceVersion(String serviceVersion) {
        if (serviceVersion == null || serviceVersion.isEmpty()) {
            throw new NullPointerException("serviceVersion parameter must not be null or empty.");
        }
        this.serviceVersion = serviceVersion.trim();
        return this;
    }

    public SOSMetadataBuilder addServiceName(String serviceName) {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new NullPointerException("serviceName parameter must not be null or empty.");
        }
        this.serviceName = serviceName.trim();
        return this;
    }

    public SOSMetadataBuilder addSosMetadataHandler(String handler) {
        if (handler == null || handler.isEmpty()) {
            throw new NullPointerException("sosMetadataHandler parameter must not be null or empty.");
        }
        this.sosMetadataHandler = handler.trim();
        return this;
    }

    public SOSMetadataBuilder addAdapter(String adapter) {
        if (adapter == null || adapter.isEmpty()) {
            throw new NullPointerException("adapter parameter must not be null or empty.");
        }
        this.adapter = adapter.trim();
        return this;
    }
    
	public SOSMetadataBuilder addProtectedService(boolean protectedService) {
		this.protectedService = protectedService;
		return this;
	}

    public SOSMetadataBuilder setWaterML(boolean waterML) {
        this.waterML = waterML;
        return this;
    }

    public SOSMetadataBuilder setAutoZoom(boolean autoZoom) {
        this.autoZoom = autoZoom;
        return this;
    }

    public SOSMetadataBuilder setForceXYAxisOrder(boolean forceXYAxisOrder) {
        this.forceXYAxisOrder = forceXYAxisOrder;
        return this;
    }
    
    public SOSMetadataBuilder setRequestChunk(int requestChunk) {
        if (requestChunk > 0) {
            this.requestChunk = requestChunk;
        }
        return this;
    }

    public SOSMetadataBuilder addLowerLeftEasting(Double llEasting) {
        if (llEasting == null) {
            throw new NullPointerException("llEasting parameter must not be null or NaN.");
        }
        if (llEasting.isNaN()) {
            throw new IllegalArgumentException("llEasting parameter must be valid Double: " + llEasting);
        }
        this.sosSpecificBboxConfigured = true;
        this.llEasting = llEasting;
        return this;
    }

    public SOSMetadataBuilder addLowerLeftNorthing(Double llNorthing) {
        if (llNorthing == null) {
            throw new NullPointerException("llNorthing parameter must not be null or NaN.");
        }
        if (llNorthing.isNaN()) {
            throw new IllegalArgumentException("llNorthing parameter must be valid Double: " + llNorthing);
        }
        this.sosSpecificBboxConfigured = true;
        this.llNorthing = llNorthing;
        return this;
    }

    public SOSMetadataBuilder addUpperRightEasting(Double urEasting) {
        if (urEasting == null) {
            throw new NullPointerException("urEasting parameter must not be null or NaN.");
        }
        if (urEasting.isNaN()) {
            throw new IllegalArgumentException("urEasting parameter must be valid Double: " + urEasting);
        }
        this.sosSpecificBboxConfigured = true;
        this.urEasting = urEasting;
        return this;
    }

    public SOSMetadataBuilder addUpperRightNorthing(Double urNorthing) {
        if (urNorthing == null) {
            throw new NullPointerException("urNorthing parameter must not be null or NaN.");
        }
        if (urNorthing.isNaN()) {
            throw new IllegalArgumentException("urNorthing parameter must be valid Double: " + urNorthing);
        }
        this.sosSpecificBboxConfigured = true;
        this.urNorthing = urNorthing;
        return this;
    }
    
    /* --------------------------
     *  Builder's Getter methods
     */

    public String getServiceURL() {
        return this.serviceURL;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public boolean isWaterML() {
        return this.waterML;
    }

    public String getSosMetadataHandler() {
        return this.sosMetadataHandler;
    }

    public String getAdapter() {
        return this.adapter;
    }
    
    public boolean isProctectedService() {
    	return this.protectedService;
    }

    public boolean isAutoZoom() {
        return this.autoZoom;
    }
    
    public boolean isForceXYAxisOrder() {
        return this.forceXYAxisOrder;
    }

    public int getRequestChunk() {
        return this.requestChunk;
    }
    
    /**
     * @return the configured SOS specific extent or {@link Constants#FALLBACK_EXTENT} if no one was configured.
     */
    public BoundingBox getConfiguredServiceExtent() {
        if ( !sosSpecificBboxConfigured) {
            return Constants.FALLBACK_EXTENT;
        }
        EastingNorthing ll = new EastingNorthing(llEasting, llNorthing, "EPSG:4326");
        EastingNorthing ur = new EastingNorthing(urEasting, urNorthing, "EPSG:4326");
        return new BoundingBox(ll, ur);
    }

}
