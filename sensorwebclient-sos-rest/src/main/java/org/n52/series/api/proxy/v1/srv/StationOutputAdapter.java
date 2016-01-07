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

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.StationConverter;
import org.n52.io.IoParameters;
import org.n52.io.crs.BoundingBox;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.sensorweb.v1.spi.ParameterService;

public class StationOutputAdapter implements ParameterService<StationOutput> {

    @Override
    public StationOutput[] getExpandedParameters(IoParameters map) {
        QueryParameters query = QueryParameterAdapter.createQueryParameters(map);
        query.setSpatialFilter(map.getSpatialFilter());
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            allStations.addAll(converter.convertExpanded(filter(metadata, query)));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput[] getCondensedParameters(IoParameters map) {
        QueryParameters query = QueryParameterAdapter.createQueryParameters(map);
        query.setSpatialFilter(map.getSpatialFilter());
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            allStations.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput[] getParameters(String[] stationIds) {
        return getParameters(stationIds, IoParameters.createDefaults());
    }

    @Override
    public StationOutput[] getParameters(String[] stationIds, IoParameters query) {
        List<StationOutput> selectedStations = new ArrayList<StationOutput>();
        for (String stationId : stationIds) {
            StationOutput station = getParameter(stationId);
            if (station != null) {
                selectedStations.add(station);
            }
        }
        return selectedStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput getParameter(String stationId) {
        return getParameter(stationId, org.n52.io.QueryParameters.createDefaults());
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
