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

    private String connector;

    private String adapter;

    private boolean waterML;

    private boolean autoZoom;

    private int requestChunk;

    private double llEasting;

    private double llNorthing;

    private double urEasting;

    private double urNorthing;

    private boolean sosSpecificBboxConfigured;

    public SOSMetadataBuilder() {
        // default
    }

    public SOSMetadata build() {
        return new SOSMetadata(this);
    }

    public SOSMetadataBuilder addServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
        return this;
    }

    public SOSMetadataBuilder addServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
        return this;
    }

    public SOSMetadataBuilder addServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public SOSMetadataBuilder addConnector(String connector) {
        this.connector = connector;
        return this;
    }

    public SOSMetadataBuilder addAdapter(String adapter) {
        this.adapter = adapter;
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
    
    public SOSMetadataBuilder setRequestChunk(int requestChunk) {
        this.requestChunk = requestChunk;
        return this;
    }

    public SOSMetadataBuilder addLowerLeftEasting(Double llEasting) {
        this.sosSpecificBboxConfigured = llEasting != null;
        this.llEasting = llEasting;
        return this;
    }

    public SOSMetadataBuilder addLowerLeftNorthing(Double llNorthing) {
        this.sosSpecificBboxConfigured = llNorthing != null;
        this.llNorthing = llNorthing;
        return this;
    }

    public SOSMetadataBuilder addUpperRightEasting(Double urEasting) {
        this.sosSpecificBboxConfigured = urEasting != null;
        this.urEasting = urEasting;
        return this;
    }

    public SOSMetadataBuilder addUpperRightNorthing(Double urNorthing) {
        this.sosSpecificBboxConfigured = urNorthing != null;
        this.urNorthing = urNorthing;
        return this;
    }

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

    public String getConnector() {
        return this.connector;
    }

    public String getAdapter() {
        return this.adapter;
    }

    public boolean isAutoZoom() {
        return this.autoZoom;
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
        EastingNorthing ll = new EastingNorthing(llEasting, llNorthing);
        EastingNorthing ur = new EastingNorthing(urEasting, urNorthing);
        return new BoundingBox(ll, ur, "EPSG:4326");
    }
}
