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
package org.n52.api.v1.srv;

import static org.n52.api.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.ServiceConverter;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ServiceParameterService;

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
	public ServiceOutput[] getExpandedParameters(QueryMap map) {
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
    public ServiceOutput[] getCondensedParameters(QueryMap map) {
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
        return metadata.getTimeseriesRelatedWith(query).length != 0;
    }

	public ServiceOutput[] getParameters(String[] serviceIds) {
        return getParameters(serviceIds, QueryMap.createDefaults());
    }
	
	@Override
    public ServiceOutput[] getParameters(String[] serviceIds, QueryMap query) {
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
		return getParameter(serviceId, QueryMap.createDefaults());
	}

    @Override
    public ServiceOutput getParameter(String serviceId, QueryMap query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.getGlobalId().equals(serviceId)) {
                ServiceConverter converter = new ServiceConverter(metadata);
                return converter.convertExpanded(metadata);
            }
        }
        return null;
    }
    
    

}
