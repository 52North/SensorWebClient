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
import java.util.HashSet;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.PhenomenonConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.PhenomenonOutput;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class PhenomenonOutputAdapter extends ParameterService<PhenomenonOutput> {

    private OutputCollection<PhenomenonOutput> createOutputCollection() {
        return new OutputCollection<PhenomenonOutput>() {
                @Override
                protected Comparator<PhenomenonOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
	@Override
	public OutputCollection<PhenomenonOutput> getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<PhenomenonOutput> outputCollection = createOutputCollection();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    PhenomenonConverter converter = new PhenomenonConverter(metadata);
			outputCollection.addItems(converter.convertExpanded(filter(metadata, query)));
		}
		return outputCollection;
	}

	@Override
    public OutputCollection<PhenomenonOutput> getCondensedParameters(IoParameters map) {
	    QueryParameters query = createQueryParameters(map);
        OutputCollection<PhenomenonOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            PhenomenonConverter converter = new PhenomenonConverter(metadata);
            outputCollection.addItems(converter.convertCondensed(filter(metadata, query)));
        }
        return outputCollection;
    }

    private Phenomenon[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Phenomenon> allPhenomena = new HashSet<Phenomenon>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allPhenomena.add(timeseries.getPhenomenon());
        }
        return allPhenomena.toArray(new Phenomenon[0]);
    }

	@Override
    public OutputCollection<PhenomenonOutput> getParameters(String[] phenomenonIds) {
        return getParameters(phenomenonIds, IoParameters.createDefaults());
    }

	@Override
    public OutputCollection<PhenomenonOutput> getParameters(String[] phenomenonIds, IoParameters query) {
        OutputCollection<PhenomenonOutput> outputCollection = createOutputCollection();
        for (String phenomenonId : phenomenonIds) {
            PhenomenonOutput phenomenon = getParameter(phenomenonId);
            if (phenomenon != null) {
                outputCollection.addItem(phenomenon);
            }
        }
        return outputCollection;
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
