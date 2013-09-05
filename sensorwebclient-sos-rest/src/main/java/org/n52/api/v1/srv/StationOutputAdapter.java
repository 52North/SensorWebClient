package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.StationConverter;
import org.n52.io.crs.BoundingBox;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class StationOutputAdapter implements ParameterService<StationOutput> {

    @Override
    public StationOutput[] getExpandedParameters(QueryMap map) {
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
    public StationOutput[] getCondensedParameters(QueryMap map) {
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
        return getParameters(stationIds, QueryMap.createDefaults());
    }
    
    @Override
    public StationOutput[] getParameters(String[] stationIds, QueryMap query) {
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
        return getParameter(stationId, QueryMap.createDefaults());
    }

    @Override
    public StationOutput getParameter(String stationId, QueryMap query) {
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
