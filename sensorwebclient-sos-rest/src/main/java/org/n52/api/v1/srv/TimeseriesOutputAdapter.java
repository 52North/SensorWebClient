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

import org.n52.api.v1.io.TimeseriesConverter;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;

public class TimeseriesOutputAdapter implements TimeseriesDataService, TimeseriesMetadataService {

    private GetDataService dataService;

	@Override
	public TvpDataCollection getTimeseriesData(UndesignedParameterSet parameters) {
		return dataService.getTimeSeriesFromParameterSet(parameters);
	}

    @Override
    public TimeseriesMetadataOutput[] getExpandedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
        List<TimeseriesMetadataOutput> allProcedures = new ArrayList<TimeseriesMetadataOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            allProcedures.addAll(converter.convertExpanded(filter(metadata, query)));
        }
        return allProcedures.toArray(new TimeseriesMetadataOutput[0]);
    }
    
    @Override
    public TimeseriesMetadataOutput[] getCondensedParameters(QueryMap map) {
        QueryParameters query = createQueryParameters(map);
        List<TimeseriesMetadataOutput> allProcedures = new ArrayList<TimeseriesMetadataOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            allProcedures.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allProcedures.toArray(new TimeseriesMetadataOutput[0]);
    }

    private SosTimeseries[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<SosTimeseries> allTimeseries = new HashSet<SosTimeseries>();
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
            allTimeseries.add(timeseries);
        }
        return allTimeseries.toArray(new SosTimeseries[0]);
    }

    @Override
    public TimeseriesMetadataOutput[] getParameters(String[] timeseriesIds) {
        List<TimeseriesMetadataOutput> selectedTimeseries = new ArrayList<TimeseriesMetadataOutput>();
        for (String timeseriesId : timeseriesIds) {
            TimeseriesMetadataOutput timeseries = getParameter(timeseriesId);
            if (timeseries != null) {
                selectedTimeseries.add(timeseries);
            }
        }
        return selectedTimeseries.toArray(new TimeseriesMetadataOutput[0]);
    }


    @Override
    public TimeseriesMetadataOutput getParameter(String timeseriesId) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            Station station = metadata.getStationByTimeSeriesId(timeseriesId);
            if (station != null) {
                TimeseriesConverter converter = new TimeseriesConverter(metadata);
                SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
                TimeseriesMetadataOutput convertExpanded = converter.convertExpanded(timeseries);
                /*
                 * We have to ensure that first and last values are only set when an item
                 * is being requested! Calling first/last value for a whole timeseries
                 * collection would trigger thousands of requests otherwise
                 */
                convertExpanded.setFirstValue(dataService.getFirstValue(timeseries));
                convertExpanded.setLastValue(dataService.getLastValue(timeseries));
                return convertExpanded;
            }
        }
        return null;
    }


    public GetDataService getDataService() {
        return dataService;
    }

    public void setDataService(GetDataService dataService) {
        this.dataService = dataService;
    }

}
