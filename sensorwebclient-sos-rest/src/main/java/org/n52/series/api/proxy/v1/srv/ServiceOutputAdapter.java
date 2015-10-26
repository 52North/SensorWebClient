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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.series.api.proxy.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.series.api.proxy.v1.io.ServiceConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.response.v1.ServiceOutput;
import org.n52.sensorweb.spi.ServiceParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ServiceOutputAdapter implements ServiceParameterService {

    @Override
    public boolean isKnownTimeseries(String timeseriesId) {
        for (SOSMetadata metadatas : getSOSMetadatas()) {
            if (metadatas.getStationByTimeSeriesId(timeseriesId) != null) {
                return true;
            }
        }
        return false;
    }

	@Override
	public ServiceOutput[] getExpandedParameters(IoParameters map) {
	    QueryParameters query = createQueryParameters(map);
		List<ServiceOutput> allServices = new ArrayList<ServiceOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
            if (matchesQuery(query, metadata)) {
    		    ServiceConverter converter = new ServiceConverter(metadata);
    			allServices.add(converter.convertExpanded(metadata));
            }
		}
		return allServices.toArray(new ServiceOutput[0]);
	}

	@Override
    public ServiceOutput[] getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        List<ServiceOutput> allServices = new ArrayList<ServiceOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (matchesQuery(query, metadata)) {
                ServiceConverter converter = new ServiceConverter(metadata);
                allServices.add(converter.convertCondensed(metadata));
            }
        }
        return allServices.toArray(new ServiceOutput[0]);
    }

    private boolean matchesQuery(QueryParameters query, SOSMetadata metadata) {
        return metadata.getMatchingTimeseries(query).length != 0;
    }

	public ServiceOutput[] getParameters(String[] serviceIds) {
        return getParameters(serviceIds, IoParameters.createDefaults());
    }

	@Override
    public ServiceOutput[] getParameters(String[] serviceIds, IoParameters query) {
	    List<ServiceOutput> selectedServices = new ArrayList<ServiceOutput>();
        for (String serviceId : serviceIds) {
            ServiceOutput serivce = getParameter(serviceId);
            if (serivce != null) {
                selectedServices.add(serivce);
            }
        }
        return selectedServices.toArray(new ServiceOutput[0]);
    }

    @Override
	public ServiceOutput getParameter(String serviceId) {
		return getParameter(serviceId, IoParameters.createDefaults());
	}

    @Override
    public ServiceOutput getParameter(String serviceId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.getGlobalId().equals(serviceId)) {
                ServiceConverter converter = new ServiceConverter(metadata);
                return converter.convertExpanded(metadata);
            }
        }
        return null;
    }



}
