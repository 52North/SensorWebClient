package org.n52.api.v1.io;

<<<<<<< HEAD
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

=======
import java.util.ArrayList;
import java.util.List;

import org.n52.io.geojson.GeojsonPoint;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class StationConverter {
    
    /**
     * Creates a full view on the given stations.
     * 
     * @param stations
     *        the stations to create an output for.
     * @return the output data.
     */
    public StationOutput[] createExpandedOutput(Station... stations) {
        List<StationOutput> stationOutput = new ArrayList<StationOutput>();
        for (Station station : stations) {
            stationOutput.add(createExpandedStation(station));
        }
        return stationOutput.toArray(new StationOutput[0]);
    }

    private StationOutput createExpandedStation(Station station) {
        StationOutput stationOutput = createMinimalStation(station);
        stationOutput.addProperty("timeseries", createFlatTimeseriesList(station));
        
        // TODO add service
        
        return stationOutput;
    }

    /**
     * Creates a condensed view on the given stations.
     * 
     * @param stations
     *        the stations to create an output for.
     * @return the output data.
     */
    public StationOutput[] createCondensedOutput(Station... stations) {
        List<StationOutput> stationOutput = new ArrayList<StationOutput>();
        for (Station station : stations) {
            stationOutput.add(createCondensedStation(station));
        }
        return stationOutput.toArray(new StationOutput[0]);
    }

    private StationOutput createCondensedStation(Station station) {
        return createMinimalStation(station);
    }

    /**
     * @param station
     *        the station to create an output view for.
     * @return the output data with minimal values set.
     */
    private StationOutput createMinimalStation(Station station) {
        StationOutput stationOutput = new StationOutput();
        stationOutput.setGeometry(getCoordinates(station));
        stationOutput.addProperty("id", station.getId());
        
        // TODO make station identifiable
//        stationOutput.addProperty("label", station.getStationId());
        
        return stationOutput;
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
        String x = Double.toString(location.getEasting());
        String y = Double.toString(location.getNorthing());
        return GeojsonPoint.create(new String[] {x, y});
    }
>>>>>>> branch 'master' of ssh://git@github.com/ridoo/SensorWebClient.git
}
