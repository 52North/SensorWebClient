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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.series.api.proxy.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.Comparator;

import org.n52.series.api.proxy.v1.io.ServiceConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.ServiceOutput;
import org.n52.sensorweb.spi.ServiceParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ServiceOutputAdapter implements ServiceParameterService {

    private OutputCollection<ServiceOutput> createOutputCollection() {
        return new OutputCollection<ServiceOutput>() {
                @Override
                protected Comparator<ServiceOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
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
	public OutputCollection<ServiceOutput> getExpandedParameters(IoParameters map) {
	    QueryParameters query = createQueryParameters(map);
        OutputCollection<ServiceOutput> outputCollection = createOutputCollection();
		for (SOSMetadata metadata : getSOSMetadatas()) {
            if (matchesQuery(query, metadata)) {
    		    ServiceConverter converter = new ServiceConverter(metadata);
    			outputCollection.addItem(converter.convertExpanded(metadata));
            }
		}
		return outputCollection;
	}

	@Override
    public OutputCollection<ServiceOutput> getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<ServiceOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (matchesQuery(query, metadata)) {
                ServiceConverter converter = new ServiceConverter(metadata);
                outputCollection.addItem(converter.convertCondensed(metadata));
            }
        }
        return outputCollection;
    }

    private boolean matchesQuery(QueryParameters query, SOSMetadata metadata) {
        return metadata.getMatchingTimeseries(query).length != 0;
    }

    @Override
	public OutputCollection<ServiceOutput> getParameters(String[] serviceIds) {
        return getParameters(serviceIds, IoParameters.createDefaults());
    }

	@Override
    public OutputCollection<ServiceOutput> getParameters(String[] serviceIds, IoParameters query) {
        OutputCollection<ServiceOutput> outputCollection = createOutputCollection();
        for (String serviceId : serviceIds) {
            ServiceOutput service = getParameter(serviceId);
            if (service != null) {
                outputCollection.addItem(service);
            }
        }
        return outputCollection;
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
