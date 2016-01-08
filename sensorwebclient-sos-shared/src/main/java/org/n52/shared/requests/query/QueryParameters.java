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
package org.n52.shared.requests.query;

import java.io.Serializable;

import org.n52.io.crs.BoundingBox;


public class QueryParameters implements Serializable {
    
    private static final long serialVersionUID = 4433261855040379401L;
    
    private String service;

    private String station;

	private String offering;

	private String procedure;

	private String phenomenon;
	
	private String category;

	private String featureOfInterest;

    private BoundingBox spatialFilter;
    
    // matches Global Ids by default
    private boolean matchDomainIds = false;

	private int offset;

	private int pageSize;
	
	public static QueryParameters createEmptyFilterQuery() {
	    return new QueryParameters();
	}
	
	public boolean hasParameterFilter() {
	    return service != null || station != null || featureOfInterest != null 
	            || procedure != null || offering != null  
	            || phenomenon != null || category != null;
	}
	
	public boolean hasSpatialFilter() {
	    return spatialFilter != null;
	}
	
	public String getService() {
	    return service;
	}
	
	public QueryParameters setService(String service) {
	    this.service = service;
	    return this;
	}

	public String getOffering() {
		return offering;
	}
	
	public QueryParameters setOffering(String offering) {
	    this.offering = offering;
	    return this;
	}

	public String getProcedure() {
		return procedure;
	}
	
	public QueryParameters setProcedure(String procedure) {
	    this.procedure = procedure;
	    return this;
	}

	public String getPhenomenon() {
		return phenomenon;
	}
	
	public QueryParameters setPhenomenon(String phenomenon) {
	    this.phenomenon = phenomenon;
	    return this;
	}

	public String getFeature() {
		return featureOfInterest;
	}
	
	public QueryParameters setFeature(String featureOfInterest) {
	    this.featureOfInterest = featureOfInterest;
	    return this;
	}

	public String getCategory() {
        return category;
    }

    public QueryParameters setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getStation() {
	    return station;
	}

    public QueryParameters setStation(String station) {
        this.station = station;
        return this;
    }

    public BoundingBox getSpatialFilter() {
        return spatialFilter;
    }
    
    public QueryParameters setSpatialFilter(BoundingBox spatialFilter) {
        this.spatialFilter = spatialFilter;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public QueryParameters setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

	public int getOffset() {
		return offset;
	}

	public QueryParameters setOffset(int offset) {
		this.offset = offset;
		return this;
	}
    
    public boolean isMatchDomainIds() {
        return matchDomainIds;
    }
    
    public QueryParameters matchDomainIds(boolean matchDomainIds) {
        this.matchDomainIds = matchDomainIds;
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("QueryParameters [");
        sb.append("service: ").append(service).append(", ");
        sb.append("station: ").append(station).append(", ");
        sb.append("offering: ").append(offering).append(", ");
        sb.append("procedure: ").append(procedure).append(", ");
        sb.append("phenomenon: ").append(phenomenon).append(", ");
        sb.append("feature: ").append(featureOfInterest).append(", ");
        sb.append("matchDomainIds: ").append(matchDomainIds).append(", ");
        sb.append("spatialFilter: ").append(spatialFilter).append(", ");
        sb.append("offset: ").append(offset).append(", ");
        sb.append("size: ").append(pageSize).append("]");
        return sb.toString();
    }

}
