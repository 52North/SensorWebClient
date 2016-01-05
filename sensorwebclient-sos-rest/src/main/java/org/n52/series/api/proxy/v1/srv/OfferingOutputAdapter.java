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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.OfferingConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.OfferingOutput;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class OfferingOutputAdapter implements ParameterService<OfferingOutput> {
    
    private OutputCollection<OfferingOutput> createOutputCollection() {
        return new OutputCollection<OfferingOutput>() {
                @Override
                protected Comparator<OfferingOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
	@Override
	public OutputCollection<OfferingOutput> getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<OfferingOutput> outputCollection = createOutputCollection();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    OfferingConverter converter = new OfferingConverter(metadata);
		    outputCollection.addItems(converter.convertExpanded(filter(metadata, query)));
        }
		return outputCollection;
	}

	@Override
    public OutputCollection<OfferingOutput> getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<OfferingOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            OfferingConverter converter = new OfferingConverter(metadata);
            outputCollection.addItems(converter.convertCondensed(filter(metadata, query)));
        }
        return outputCollection;
    }

    private Offering[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Offering> allOfferings = new HashSet<Offering>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allOfferings.add(timeseries.getOffering());
        }
        return allOfferings.toArray(new Offering[0]);
    }

	@Override
    public OutputCollection<OfferingOutput> getParameters(String[] offeringIds) {
	    return getParameters(offeringIds, IoParameters.createDefaults());
	}

    @Override
    public OutputCollection<OfferingOutput> getParameters(String[] offeringIds, IoParameters query) {

        // TODO consider query

        OutputCollection<OfferingOutput> outputCollection = createOutputCollection();
        for (String offeringId : offeringIds) {
            OfferingOutput offering = getParameter(offeringId);
            if (offering != null) {
                outputCollection.addItem(offering);
            }
        }
        return outputCollection;
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
