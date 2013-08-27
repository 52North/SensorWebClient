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
package org.n52.api.v1.io;

import java.util.Collection;

import org.n52.io.v1.data.ServiceOutput;
import org.n52.io.v1.data.ServiceOutput.ParameterCount;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class ServiceConverter extends OutputConverter<SOSMetadata, ServiceOutput> {

    public ServiceConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public ServiceOutput convertExpanded(SOSMetadata metadata) {
        ServiceOutput convertedService = new ServiceOutput();
        convertedService.setId(metadata.getGlobalId());
        convertedService.setServiceUrl(metadata.getServiceUrl());
        convertedService.setVersion(metadata.getVersion());
        convertedService.setLabel(metadata.getTitle());
        convertedService.setType("SOS");
        
        convertedService.setQuantities(countParameters(metadata, convertedService));
        return convertedService;
    }

    private ParameterCount countParameters(SOSMetadata metadata, ServiceOutput convertedService) {
        ParameterCount parameterCount = convertedService.new ParameterCount();
        parameterCount.setFeaturesSize(getLookup().getFeatures().size());
        parameterCount.setOfferingsSize(getLookup().getOfferings().size());
        parameterCount.setPhenomenaSize(getLookup().getPhenomenons().size());
        parameterCount.setProceduresSize(getLookup().getProcedures().size());
        Collection<Station> stations = metadata.getStations();
        parameterCount.setStationsSize(stations.size());
        parameterCount.setTimeseriesSize(countTimeseries(stations));
        return parameterCount;
    }

    private int countTimeseries(Collection<Station> stations) {
        int size = 0;
        for (Station station : stations) {
            size += station.getObservedTimeseries().size();
        }
        return size;
    }

    @Override
    public ServiceOutput convertCondensed(SOSMetadata toConvert) {
        return convertCondensedService();
    }

}
