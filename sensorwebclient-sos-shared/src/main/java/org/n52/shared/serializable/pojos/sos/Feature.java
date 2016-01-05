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
