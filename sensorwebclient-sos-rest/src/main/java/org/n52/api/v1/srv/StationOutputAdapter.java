package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.StationConverter;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.ParameterService;

public class StationOutputAdapter implements ParameterService<StationOutput> {

    @Override
    public StationOutput[] getExpandedParameters(int offset, int size) {
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            Station[] stationsAsArray = getStationsAsArray(metadata);
            allStations.addAll(converter.convertExpanded(stationsAsArray));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput[] getCondensedParameters(int offset, int size) {
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            Station[] stationsAsArray = getStationsAsArray(metadata);
            allStations.addAll(converter.convertCondensed(stationsAsArray));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput getParameter(String stationId) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.getStation(stationId) != null) {
                StationConverter converter = new StationConverter(metadata);
                return converter.convertCondensed(metadata.getStation(stationId));
            }
        }
        return null;
    }

    private Station[] getStationsAsArray(SOSMetadata metadata) {
        return metadata.getStations().toArray(new Station[0]);
    }

}
