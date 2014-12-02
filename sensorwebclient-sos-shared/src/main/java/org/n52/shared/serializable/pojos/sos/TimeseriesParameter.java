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

import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = -6244226109934637660L;

    private String parameterId;

    private String globalId;

    private String label;

    TimeseriesParameter() {
        // for serialization
    }

    TimeseriesParameter(String parameterId, String[] parametersToGenerateId) {
        if (parameterId == null || parameterId.isEmpty()) {
            throw new IllegalArgumentException("parameterId must not be null.");
        }
        this.parameterId = parameterId;
        this.label = SosTimeseries.createLabelFromUri(parameterId);
        this.globalId = generateGlobalId(parameterId, parametersToGenerateId);
    }

    protected abstract String getGlobalIdPrefix();

    private String generateGlobalId(String id, String[] parametersToGenerateId) {
        IdGenerator idGenerator = new MD5HashGenerator(getGlobalIdPrefix());
        return idGenerator.generate(parametersToGenerateId);
    }

    @JsonIgnore
    public String getGlobalId() {
		return globalId;
	}

    protected String getParameterId() {
        return parameterId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

//    protected String parseLabel(String parameterId) {
//        if (parameterId.startsWith("urn")) {
//            return parameterId.substring(parameterId.lastIndexOf(":") + 1);
//        } else if (parameterId.startsWith("http")) {
//            if (!parameterId.contains("#")) {
//                return parameterId.substring(parameterId.lastIndexOf("/") + 1);
//            } else {
//                return parameterId.substring(parameterId.lastIndexOf("#") + 1);
//            }
//        } else {
//            return parameterId;
//        }
//    }
}
