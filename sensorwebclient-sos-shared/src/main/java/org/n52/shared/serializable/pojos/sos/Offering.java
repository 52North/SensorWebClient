/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

public class Offering extends TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = -544290033391799572L;

    // TODO store responseFormat as it may vary for each offering

    Offering() {
        // for serialization
    }

    public Offering(String parameterId, String serviceUrl) {
        super(parameterId, new String[]{parameterId, serviceUrl});
    }
    
    @Override
    protected String getGlobalIdPrefix() {
        return "off_";
    }

    @JsonProperty("id")
    public String getOfferingId() {
        return getParameterId();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("offeringId: '").append(getOfferingId());
        sb.append("', ").append("internalId: '").append(getGlobalId());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getOfferingId() == null) ? 0 : getOfferingId().hashCode());
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
        Offering other = (Offering) obj;
        if (getOfferingId() == null) {
            if (other.getOfferingId() != null)
                return false;
        }
        else if ( !getOfferingId().equals(other.getOfferingId()))
            return false;
        return true;
    }

}
