/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.PhenomenonConverter;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.sensorweb.v1.spi.ParameterService;

public class PhenomenonOutputAdapter implements ParameterService<PhenomenonOutput> {

	@Override
	public PhenomenonOutput[] getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
		List<PhenomenonOutput> allPhenomenons = new ArrayList<PhenomenonOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    PhenomenonConverter converter = new PhenomenonConverter(metadata);
			allPhenomenons.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allPhenomenons.toArray(new PhenomenonOutput[0]);
	}

	@Override
    public PhenomenonOutput[] getCondensedParameters(IoParameters map) {
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
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allPhenomena.add(timeseries.getPhenomenon());
        }
        return allPhenomena.toArray(new Phenomenon[0]);
    }

	@Override
    public PhenomenonOutput[] getParameters(String[] phenomenonIds) {
        return getParameters(phenomenonIds, IoParameters.createDefaults());
    }

	@Override
    public PhenomenonOutput[] getParameters(String[] phenomenonIds, IoParameters query) {
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
		return getParameter(phenomenonId, IoParameters.createDefaults());
	}

    @Override
    public PhenomenonOutput getParameter(String phenomenonId, IoParameters query) {
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
