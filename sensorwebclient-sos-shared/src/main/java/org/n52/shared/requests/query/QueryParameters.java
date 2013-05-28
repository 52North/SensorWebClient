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
package org.n52.shared.requests.query;

import java.io.Serializable;

import org.n52.shared.serializable.pojos.BoundingBox;


public class QueryParameters implements Serializable {
    
    private static final long serialVersionUID = 4433261855040379401L;

    private String station;

	private String offering;

	private String procedure;

	private String phenomenon;

	private String featureOfInterest;

    private BoundingBox spatialFilter;

	private int offset;

	private int pageSize;
	
	public static QueryParameters createEmptyFilterQuery() {
	    return new QueryParameters();
	}
	
	public boolean hasParameterFilter() {
	    return station != null || featureOfInterest != null 
	            || procedure != null || offering != null  
	            || phenomenon != null;
	}
	
	public boolean hasSpatialFilter() {
	    return spatialFilter != null;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("QueryParameters [");
        sb.append("station: ").append(station).append(", ");
        sb.append("offering: ").append(offering).append(", ");
        sb.append("procedure: ").append(procedure).append(", ");
        sb.append("phenomenon: ").append(phenomenon).append(", ");
        sb.append("feature: ").append(featureOfInterest).append(", ");
        sb.append("spatialFilter: ").append(spatialFilter).append(", ");
        sb.append("offset: ").append(offset).append(", ");
        sb.append("size: ").append(pageSize).append("]");
        return sb.toString();
    }

}
