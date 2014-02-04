/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.api.v1.srv;

import static org.n52.api.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.OfferingConverter;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.ParameterService;

public class OfferingOutputAdapter implements ParameterService<OfferingOutput> {

	@Override
	public OfferingOutput[] getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
		List<OfferingOutput> allOfferings = new ArrayList<OfferingOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    OfferingConverter converter = new OfferingConverter(metadata);
		    allOfferings.addAll(converter.convertExpanded(filter(metadata, query)));
        }
		return allOfferings.toArray(new OfferingOutput[0]);
	}

	@Override
    public OfferingOutput[] getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        List<OfferingOutput> allOfferings = new ArrayList<OfferingOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            OfferingConverter converter = new OfferingConverter(metadata);
            allOfferings.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allOfferings.toArray(new OfferingOutput[0]);
    }

    private Offering[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Offering> allOfferings = new HashSet<Offering>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allOfferings.add(timeseries.getOffering());
        }
        return allOfferings.toArray(new Offering[0]);
    }

	@Override
    public OfferingOutput[] getParameters(String[] offeringIds) {
	    return getParameters(offeringIds, IoParameters.createDefaults());
	}

    @Override
    public OfferingOutput[] getParameters(String[] offeringIds, IoParameters query) {

        // TODO consider query
        
        List<OfferingOutput> selectedOfferings = new ArrayList<OfferingOutput>();
        for (String offeringId : offeringIds) {
            OfferingOutput offering = getParameter(offeringId);
            if (offering != null) {
                selectedOfferings.add(offering);
            }
        }
        return selectedOfferings.toArray(new OfferingOutput[0]);
    }

    @Override
	public OfferingOutput getParameter(String offeringId) {
		return getParameter(offeringId, IoParameters.createDefaults());
	}

    @Override
    public OfferingOutput getParameter(String offeringId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            for (Offering offering : lookup.getOfferings()) {
                if(offering.getGlobalId().equals(offeringId)) {
                    OfferingConverter converter = new OfferingConverter(metadata);
                    return converter.convertExpanded(offering);
                }
            }
        }
        return null;
    }

}
