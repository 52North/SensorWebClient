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

import org.n52.io.crs.BoundingBox;

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
    
    private boolean supportsFirstLatest = false;

    private int requestChunk = 100;
    
    private BoundingBox extent;
    
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
	
	public SOSMetadataBuilder addSupportsFirstLatest(boolean supportsFirstLatest) {
        this.supportsFirstLatest = supportsFirstLatest;
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
    
    public SOSMetadataBuilder withExtent(BoundingBox bbox) {
        this.extent = bbox;
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
    
    public boolean isSupportsFirstLatest() {
        return this.supportsFirstLatest;
    }

    public int getRequestChunk() {
        return this.requestChunk;
    }
    
    public BoundingBox getConfiguredServiceExtent() {
        return extent;
    }

}
