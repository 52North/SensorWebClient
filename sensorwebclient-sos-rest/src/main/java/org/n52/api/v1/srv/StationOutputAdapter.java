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

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.StationConverter;
import org.n52.io.IoParameters;
import org.n52.io.crs.BoundingBox;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.ParameterService;

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
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
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
