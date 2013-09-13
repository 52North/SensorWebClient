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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feature extends TimeseriesParameter implements Serializable {

	private static final long serialVersionUID = 693946840349140532L;

	Feature() {
		// for serialization
	}

	public Feature(String parameterId, String serviceUrl) {
	    super(parameterId, new String[]{parameterId, serviceUrl});
	}
	
	@Override
    protected String getGlobalIdPrefix() {
        return "foi_";
    }

    @JsonProperty("id")
    public String getFeatureId() {
	    return getParameterId();
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("featureId: '").append(getFeatureId());
        sb.append("',").append("internalId: '").append(getGlobalId());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getFeatureId() == null) ? 0 : getFeatureId().hashCode());
        result = prime * result + ( (getGlobalId() == null) ? 0 : getGlobalId().hashCode());
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
        Feature other = (Feature) obj;
        if (getFeatureId() == null) {
            if (other.getFeatureId() != null)
                return false;
        }
        else if ( !getFeatureId().equals(other.getFeatureId()))
            return false;
        return true;
    }

}
