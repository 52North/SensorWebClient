/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.series.api.proxy.v1.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.n52.io.response.v1.ServiceOutput;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public abstract class OutputConverter<T,E> {

    private SOSMetadata metadata;

    public OutputConverter(SOSMetadata metadata) {
        this.metadata = metadata;
    }
    
    protected SOSMetadata getMetadata() {
        return metadata;
    }
    
    protected TimeseriesParametersLookup getLookup() {
        return metadata.getTimeseriesParametersLookup();
    }

    protected ServiceOutput convertCondensedService() {
        ServiceOutput convertedService = new ServiceOutput();
        convertedService.setId(metadata.getGlobalId());
        convertedService.setLabel(metadata.getTitle());
        return convertedService;
    }
    
    public Collection<E> convertCondensed(T... toConvert) {
        List<E> allConverted = new ArrayList<E>();
        for (T element : toConvert) {
            allConverted.add(convertCondensed(element));
        }
        return allConverted;
    }

    public Collection<E> convertExpanded(T... toConvert) {
        List<E> allConverted = new ArrayList<E>();
        for (T element : toConvert) {
            allConverted.add(convertExpanded(element));
        }
        return allConverted;
    }
    
    public abstract E convertExpanded(T toConvert);
    
    public abstract E convertCondensed(T toConvert);

}
