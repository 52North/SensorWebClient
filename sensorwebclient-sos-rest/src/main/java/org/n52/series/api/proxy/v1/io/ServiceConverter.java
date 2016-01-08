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
package org.n52.series.api.proxy.v1.io;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.n52.io.response.v1.ServiceOutput;
import org.n52.io.response.v1.ServiceOutput.ParameterCount;

import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class ServiceConverter extends OutputConverter<SOSMetadata, ServiceOutput> {

    public ServiceConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public ServiceOutput convertExpanded(SOSMetadata metadata) {
        ServiceOutput convertedService = convertCondensed(metadata);
        convertedService.setQuantities(countParameters(metadata, convertedService));
        convertedService.setSupportsFirstLatest(metadata.isSupportsFirstLatest());
        convertedService.setServiceUrl(metadata.getServiceUrl());
        convertedService.setVersion(metadata.getVersion());
        convertedService.setType("SOS");
        return convertedService;
    }

    private ParameterCount countParameters(SOSMetadata metadata, ServiceOutput convertedService) {
        ParameterCount parameterCount = new ServiceOutput.ParameterCount();
        parameterCount.setFeaturesSize(getLookup().getFeatures().size());
        parameterCount.setOfferingsSize(getLookup().getOfferings().size());
        parameterCount.setPhenomenaSize(getLookup().getPhenomenons().size());
        parameterCount.setProceduresSize(getLookup().getProcedures().size());
        Collection<Station> stations = metadata.getStations();
        parameterCount.setStationsSize(stations.size());
        parameterCount.setTimeseriesSize(countTimeseries(stations));
        parameterCount.setCategoriesSize(countCategories(stations));
        return parameterCount;
    }

    private int countTimeseries(Collection<Station> stations) {
        int size = 0;
        for (Station station : stations) {
            size += station.getObservedTimeseries().size();
        }
        return size;
    }

    private Integer countCategories(Collection<Station> stations) {
        Set<Category> categories = new HashSet<Category>();
        for (Station station : stations) {
            for (SosTimeseries timeseries : station.getObservedTimeseries()) {
                categories.add(timeseries.getCategory());
            }
        }
        return categories.size();
    }

    @Override
    public ServiceOutput convertCondensed(SOSMetadata toConvert) {
        return convertCondensedService();
    }

}
