/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

public class Phenomenon extends TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = 207874913321466876L;

    private String unitOfMeasure;

    Phenomenon() {
        // for serialization
    }

    public Phenomenon(String parameterId, String serviceUrl) {
        super(parameterId, new String[]{parameterId, serviceUrl});
    }
    
    @Override
    protected String getGlobalIdPrefix() {
        return "phe_";
    }

    @JsonProperty("id")
    public String getPhenomenonId() {
        return getParameterId();
    }

    public String getUnitOfMeasure() {
        return this.unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("phenomenonId: '").append(getPhenomenonId());
        sb.append("', ").append("internalId: '").append(getGlobalId());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getPhenomenonId() == null) ? 0 : getPhenomenonId().hashCode());
        result = prime * result + ( (getGlobalId() == null) ? 0 : getGlobalId().hashCode());
        result = prime * result + ( (getUnitOfMeasure() == null) ? 0 : getUnitOfMeasure().hashCode());
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
        Phenomenon other = (Phenomenon) obj;
        if (getPhenomenonId() == null) {
            if (other.getPhenomenonId() != null)
                return false;
        }
        else if ( !getPhenomenonId().equals(other.getPhenomenonId()))
            return false;
        if (getUnitOfMeasure() == null) {
            if (other.getUnitOfMeasure() != null)
                return false;
        }
        else if ( !getUnitOfMeasure().equals(other.getUnitOfMeasure()))
            return false;
        return true;
    }

}
