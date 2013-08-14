package org.n52.api.v1.io;

import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.crs.EastingNorthing;
import org.n52.io.geojson.GeojsonPoint;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class StationConverter extends OutputConverter<Station, StationOutput> {

    public StationConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public StationOutput convertExpanded(Station station) {
        StationOutput convertedStation = convertCondensed(station);
        convertedStation.addProperty("timeseries", createFlatTimeseriesList(station));
        convertedStation.addProperty("service", convertCondensedService());
        return convertedStation;
        
    }

    @Override
    public StationOutput convertCondensed(Station station) {
        StationOutput convertedStation = new StationOutput();
        convertedStation.setGeometry(getCoordinates(station));
        convertedStation.addProperty("label", station.getLabel());
        
        // TODO make station identifiable
//        stationOutput.addProperty("label", station.getStationId());
        
        return convertedStation;
    }
    
    
    private String[] createFlatTimeseriesList(Station station) {
        List<String> timeseriesIds = new ArrayList<String>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            timeseriesIds.add(timeseries.getTimeseriesId());
        }
        return timeseriesIds.toArray(new String[0]);
    }

    private GeojsonPoint getCoordinates(Station station) {
        EastingNorthing location = station.getLocation();
        Double x = location.getEasting();
        Double y = location.getNorthing();
        return createWithCoordinates(new Double[] {x, y});
    }
}
