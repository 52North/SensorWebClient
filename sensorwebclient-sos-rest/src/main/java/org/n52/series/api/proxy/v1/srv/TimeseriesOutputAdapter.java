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

import org.n52.series.api.proxy.v1.io.TimeseriesConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.TimeseriesMetadataOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.sensorweb.spi.SeriesDataService;

public class TimeseriesOutputAdapter implements SeriesDataService, ParameterService<TimeseriesMetadataOutput> {

    private GetDataService dataService;

    private OutputCollection<TimeseriesMetadataOutput> createOutputCollection() {
        return new OutputCollection<TimeseriesMetadataOutput>() {
                @Override
                protected Comparator<TimeseriesMetadataOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
	@Override
	public TvpDataCollection getSeriesData(RequestSimpleParameterSet parameters) {
		return dataService.getTimeSeriesFromParameterSet(parameters);
	}

    @Override
    public OutputCollection<TimeseriesMetadataOutput> getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<TimeseriesMetadataOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            SosTimeseries[] timeseriesToConvert = filter(metadata, query);
            for (SosTimeseries sosTimeseries : timeseriesToConvert) {
                TimeseriesMetadataOutput converted = converter.convertExpanded(sosTimeseries);
                if (metadata.isSupportsFirstLatest() && map.isForceLatestValueRequests()) {
                    // setting last values must be declared explicitly to avoid thousands of requests
                    converted.setLastValue(dataService.getLastValue(sosTimeseries));
                }
                outputCollection.addItem(converted);
            }
        }
        return outputCollection;
    }

    @Override
    public OutputCollection<TimeseriesMetadataOutput> getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        OutputCollection<TimeseriesMetadataOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            outputCollection.addItems(converter.convertCondensed(filter(metadata, query)));
        }
        return outputCollection;
    }

    private SosTimeseries[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<SosTimeseries> allTimeseries = new HashSet<SosTimeseries>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
            allTimeseries.add(timeseries);
        }
        return allTimeseries.toArray(new SosTimeseries[0]);
    }

    @Override
    public OutputCollection<TimeseriesMetadataOutput> getParameters(String[] timeseriesIds) {
        return getParameters(timeseriesIds, org.n52.io.request.QueryParameters.createDefaults());
    }

    @Override
    public OutputCollection<TimeseriesMetadataOutput> getParameters(String[] timeseriesIds, IoParameters query) {
        OutputCollection<TimeseriesMetadataOutput> outputCollection = createOutputCollection();
        for (String timeseriesId : timeseriesIds) {
            /*
             * TODO we may not want to invoke getLatest requests for all timeseriesIds here
             */
            TimeseriesMetadataOutput timeseries = getParameter(timeseriesId);
            if (timeseries != null) {
                outputCollection.addItem(timeseries);
            }
        }
        return outputCollection;
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String timeseriesId) {
        return getParameter(timeseriesId, IoParameters.createDefaults());
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String timeseriesId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            Station station = metadata.getStationByTimeSeriesId(timeseriesId);
            if (station != null) {
                TimeseriesConverter converter = new TimeseriesConverter(metadata);
                SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
                TimeseriesMetadataOutput convertExpanded = converter.convertExpanded(timeseries);
                if (metadata.isSupportsFirstLatest()) {
                    convertExpanded.setFirstValue(dataService.getFirstValue(timeseries));
                    convertExpanded.setLastValue(dataService.getLastValue(timeseries));
                }
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
