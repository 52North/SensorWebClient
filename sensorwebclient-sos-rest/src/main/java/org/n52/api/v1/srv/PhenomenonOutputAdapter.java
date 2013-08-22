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

import org.n52.api.v1.io.PhenomenonConverter;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class PhenomenonOutputAdapter implements ParameterService<PhenomenonOutput> {

	@Override
	public PhenomenonOutput[] getExpandedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
		List<PhenomenonOutput> allPhenomenons = new ArrayList<PhenomenonOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    PhenomenonConverter converter = new PhenomenonConverter(metadata);
			allPhenomenons.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allPhenomenons.toArray(new PhenomenonOutput[0]);
	}
	
	@Override
    public PhenomenonOutput[] getCondensedParameters(QueryMap map) {
	    QueryParameters query = createQueryParameters(map);
        List<PhenomenonOutput> allPhenomenons = new ArrayList<PhenomenonOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            PhenomenonConverter converter = new PhenomenonConverter(metadata);
            allPhenomenons.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allPhenomenons.toArray(new PhenomenonOutput[0]);
    }

    private Phenomenon[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Phenomenon> allPhenomena = new HashSet<Phenomenon>();
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
          allPhenomena.add(timeseries.getPhenomenon());
        }
        return allPhenomena.toArray(new Phenomenon[0]);
    }

	@Override
    public PhenomenonOutput[] getParameters(String[] phenomenonIds) {
        List<PhenomenonOutput> selectedPhenomenons = new ArrayList<PhenomenonOutput>();
        for (String phenomenonId : phenomenonIds) {
            PhenomenonOutput phenomenon = getParameter(phenomenonId);
            if (phenomenon != null) {
                selectedPhenomenons.add(phenomenon);
            }
        }
        return selectedPhenomenons.toArray(new PhenomenonOutput[0]);
    }
	
	@Override
	public PhenomenonOutput getParameter(String phenomenonId) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			for (Phenomenon phenomenon : lookup.getPhenomenons()) {
				if(phenomenon.getGlobalId().equals(phenomenonId)) {
					PhenomenonConverter converter = new PhenomenonConverter(metadata);
					return converter.convertExpanded(phenomenon);
				}
			}
		}
		return null;
	}

}
