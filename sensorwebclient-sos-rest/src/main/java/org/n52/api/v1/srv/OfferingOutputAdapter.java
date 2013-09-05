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

import org.n52.api.v1.io.OfferingConverter;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class OfferingOutputAdapter implements ParameterService<OfferingOutput> {

	@Override
	public OfferingOutput[] getExpandedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
		List<OfferingOutput> allOfferings = new ArrayList<OfferingOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    OfferingConverter converter = new OfferingConverter(metadata);
		    allOfferings.addAll(converter.convertExpanded(filter(metadata, query)));
        }
		return allOfferings.toArray(new OfferingOutput[0]);
	}

	@Override
    public OfferingOutput[] getCondensedParameters(QueryMap map) {
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
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
          allOfferings.add(timeseries.getOffering());
        }
        return allOfferings.toArray(new Offering[0]);
    }

	@Override
    public OfferingOutput[] getParameters(String[] offeringIds) {
	    return getParameters(offeringIds, QueryMap.createDefaults());
	}

    @Override
    public OfferingOutput[] getParameters(String[] offeringIds, QueryMap query) {

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
		return getParameter(offeringId, QueryMap.createDefaults());
	}

    @Override
    public OfferingOutput getParameter(String offeringId, QueryMap query) {
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
