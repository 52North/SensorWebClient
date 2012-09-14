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
package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public abstract class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = 3525302152538871886L;

    private String id;
    
    private String version = "1.0.0"; // default
    
    private String connector;
    
    private String adapter;

    private boolean initialized = false;

    protected ServiceMetadata() {
        // do nothing
    }
     
    /**
     * @see {@link #ServiceMetadata(String, String)} to explicitly set version 
     */
    @Deprecated
    public ServiceMetadata(String id) {
        this.id = id;
    }

    public ServiceMetadata(String id, String version) {
        this(id);
        this.version = version;
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

    public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
        // is null when used on client side
		this.connector = connector != null ? connector.trim() : null;
	}

	public String getAdapter() {
		return adapter;
	}

	public void setAdapter(String adapter) {
	    // is null when used on client side
		this.adapter = adapter != null ? adapter.trim() : null;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceMetadata [ ");
        sb.append("parameterId: ").append(id).append(", ");
        sb.append("initialized: ").append(initialized).append(", ");
        sb.append("version: ").append(version);
        sb.append(" ]");
        return sb.toString();
    }
}