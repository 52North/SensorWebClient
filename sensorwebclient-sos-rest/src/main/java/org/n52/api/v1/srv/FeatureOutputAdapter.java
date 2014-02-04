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

import org.n52.api.v1.io.FeatureConverter;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.ParameterService;

public class FeatureOutputAdapter implements ParameterService<FeatureOutput> {

	@Override
	public FeatureOutput[] getExpandedParameters(IoParameters map) {
	    QueryParameters query = createQueryParameters(map);
        List<FeatureOutput> allFeatures = new ArrayList<FeatureOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
	        FeatureConverter converter = new FeatureConverter(metadata);
		    allFeatures.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allFeatures.toArray(new FeatureOutput[0]);
	}

    @Override
    public FeatureOutput[] getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        List<FeatureOutput> allFeatures = new ArrayList<FeatureOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            FeatureConverter converter = new FeatureConverter(metadata);
            allFeatures.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allFeatures.toArray(new FeatureOutput[0]);
    }

    private Feature[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Feature> allFeatures = new HashSet<Feature>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allFeatures.add(timeseries.getFeature());
        }
        return allFeatures.toArray(new Feature[0]);
    }

	@Override
    public FeatureOutput[] getParameters(String[] featureIds) {
	    return getParameters(featureIds, IoParameters.createDefaults());
    }


    @Override
    public FeatureOutput[] getParameters(String[] featureIds, IoParameters query) {

        // TODO consider query
        
        List<FeatureOutput> selectedFeatures = new ArrayList<FeatureOutput>();
        for (String featureId : featureIds) {
            FeatureOutput feature = getParameter(featureId);
            if (feature != null) {
                selectedFeatures.add(feature);
            }
        }
        return selectedFeatures.toArray(new FeatureOutput[0]);
    }

    @Override
	public FeatureOutput getParameter(String featureId) {
		return getParameter(featureId, IoParameters.createDefaults());
	}

    @Override
    public FeatureOutput getParameter(String featureId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            for (Feature feature : lookup.getFeatures()) {
                if (feature.getGlobalId().equals(featureId)) {
                    FeatureConverter converter = new FeatureConverter(metadata);
                    return converter.convertExpanded(feature);
                }
            }
        }
        return null;
    }

}
