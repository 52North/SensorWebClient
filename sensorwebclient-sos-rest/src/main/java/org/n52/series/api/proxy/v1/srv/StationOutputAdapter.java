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

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.StationConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.crs.BoundingBox;
import org.n52.io.geojson.old.GeojsonFeature;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.v1.StationOutput;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class StationOutputAdapter implements ParameterService<StationOutput> {

    private OutputCollection<StationOutput> createOutputCollection() {
        return new OutputCollection<StationOutput>() {
                @Override
                protected Comparator<StationOutput> getComparator() {
                    return GeojsonFeature.defaultComparator();
                }
            };
    }
    
    @Override
    public OutputCollection<StationOutput> getExpandedParameters(IoParameters map) {
        QueryParameters query = QueryParameterAdapter.createQueryParameters(map);
        query.setSpatialFilter(map.getSpatialFilter());
        OutputCollection<StationOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            outputCollection.addItems(converter.convertExpanded(filter(metadata, query)));
        }
        return outputCollection;
    }

    @Override
    public OutputCollection<StationOutput> getCondensedParameters(IoParameters map) {
        QueryParameters query = QueryParameterAdapter.createQueryParameters(map);
        query.setSpatialFilter(map.getSpatialFilter());
        OutputCollection<StationOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            outputCollection.addItems(converter.convertCondensed(filter(metadata, query)));
        }
        return outputCollection;
    }

    @Override
    public OutputCollection<StationOutput> getParameters(String[] stationIds) {
        return getParameters(stationIds, IoParameters.createDefaults());
    }

    @Override
    public OutputCollection<StationOutput> getParameters(String[] stationIds, IoParameters query) {
        OutputCollection<StationOutput> outputCollection = createOutputCollection();
        for (String stationId : stationIds) {
            StationOutput station = getParameter(stationId);
            if (station != null) {
                outputCollection.addItem(station);
            }
        }
        return outputCollection;
    }

    @Override
    public StationOutput getParameter(String stationId) {
        return getParameter(stationId, org.n52.io.request.QueryParameters.createDefaults());
    }

    @Override
    public StationOutput getParameter(String stationId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            for (Station station : metadata.getStations()) {
                if (station.getGlobalId().equals(stationId)) {
                    if (isStationWithinBounds(query.getSpatialFilter(), station)) {
                        StationConverter converter = new StationConverter(metadata);
                        return converter.convertExpanded(station);
                    }
                }
            }
        }
        return null;
    }

    private Station[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Station> allStations = new HashSet<Station>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
            Station station = metadata.getStationByTimeSeries(timeseries);
            if (isStationWithinBounds(query.getSpatialFilter(), station)) {
                allStations.add(station);
            }
        }
        return allStations.toArray(new Station[0]);
    }

    private boolean isStationWithinBounds(BoundingBox boundingBox, Station station) {
        return boundingBox == null || boundingBox.contains(station.getLocation());
    }

}
