/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.serializable.pojos.sos;

import org.n52.io.crs.BoundingBox;

public class SOSMetadataBuilder {

    private String serviceURL;

    private String serviceVersion;

    private String serviceName;

    private String sosMetadataHandler;

    private String adapter;

    private boolean waterML = false;

    private boolean eventing = false;

    private boolean autoZoom = true;

    private boolean forceXYAxisOrder = false;

    private boolean supportsFirstLatest = false;

    private boolean gdaPrefinal = false;

    private int httpConnectionPoolSize = 50;

    private int requestChunk = 100;

    private int timeout = 10000;

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

	public SOSMetadataBuilder addSupportsFirstLatest(boolean supportsFirstLatest) {
        this.supportsFirstLatest = supportsFirstLatest;
        return this;
    }

    public SOSMetadataBuilder setWaterML(boolean waterML) {
        this.waterML = waterML;
        return this;
    }

    public SOSMetadataBuilder setEnableEventing(boolean enableEventing) {
        this.eventing = enableEventing;
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

    public SOSMetadataBuilder setGdaPrefinal(boolean gdaPrefinal) {
        this.gdaPrefinal = gdaPrefinal;
        return this;
    }

    public SOSMetadataBuilder setHttpConnectionPoolSize(int httpConnectionPoolSize) {
        this.httpConnectionPoolSize = httpConnectionPoolSize;
        return this;
    }

    public SOSMetadataBuilder setRequestChunk(int requestChunk) {
        if (requestChunk > 0) {
            this.requestChunk = requestChunk;
        }
        return this;
    }

    public SOSMetadataBuilder setTimeout(int timeout) {
        this.timeout = timeout;
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

    public boolean isEventing() {
        return this.eventing;
    }

    public String getSosMetadataHandler() {
        return this.sosMetadataHandler;
    }

    public String getAdapter() {
        return this.adapter;
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

    public boolean isGdaPrefinal() {
        return gdaPrefinal;
    }

    public int getHttpConnectionPoolSize() {
        return httpConnectionPoolSize;
    }

    public int getRequestChunk() {
        return this.requestChunk;
    }

    public int getTimeout() {
        return timeout;
    }

    public BoundingBox getConfiguredServiceExtent() {
        return extent;
    }


}
