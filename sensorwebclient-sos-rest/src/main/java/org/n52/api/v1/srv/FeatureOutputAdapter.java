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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.FeatureConverter;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class FeatureOutputAdapter implements ParameterService<FeatureOutput> {

	@Override
	public FeatureOutput[] getExpandedParameters(QueryMap map) {
	    QueryParameters query = createQueryParameters(map);
        List<FeatureOutput> allFeatures = new ArrayList<FeatureOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
	        FeatureConverter converter = new FeatureConverter(metadata);
		    allFeatures.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allFeatures.toArray(new FeatureOutput[0]);
	}

    @Override
    public FeatureOutput[] getCondensedParameters(QueryMap map) {
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
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
          allFeatures.add(timeseries.getFeature());
        }
        return allFeatures.toArray(new Feature[0]);
    }

	@Override
    public FeatureOutput[] getParameters(String[] featureIds) {
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
