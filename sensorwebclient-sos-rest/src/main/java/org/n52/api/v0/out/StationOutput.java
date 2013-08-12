
package org.n52.api.v0.out;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.geojson.GeojsonFeature;
import org.n52.io.geojson.GeojsonPoint;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

/**
 * {@link Station} output data to be used for de-/marshalling web views.
 */
public class StationOutput extends GeojsonFeature {

    /**
     * Creates a full view on the given stations.
     * 
     * @param stations
     *        the stations to create an output for.
     * @return the output data.
     */
    public static StationOutput[] createCompleteStationOutput(Station[] stations) {
        List<StationOutput> stationOutput = new ArrayList<StationOutput>();
        for (Station station : stations) {
            stationOutput.add(createCompleteStationOutput(station));
        }
        return stationOutput.toArray(new StationOutput[0]);
    }

    /**
     * Creates a full view on the given station.
     * 
     * @param station
     *        the station to create an output for.
     * @return the output data.
     */
    public static StationOutput createCompleteStationOutput(Station station) {
        StationOutput stationOutput = createMinimalStation(station);
        stationOutput.addProperty("timeseries", createCompleteTimeseriesList(station));
        return stationOutput;
    }

    /**
     * Creates a condensed view on the given stations.
     * 
     * @param stations
     *        the stations to create an output for.
     * @return the output data.
     */
    public static StationOutput[] createSimpleStationOutput(Station[] stations) {
        List<StationOutput> stationOutput = new ArrayList<StationOutput>();
        for (Station station : stations) {
            stationOutput.add(createSimpleStationOutput(station));
        }
        return stationOutput.toArray(new StationOutput[0]);
    }

    /**
     * Creates a condensed view on the given station.
     * 
     * @param station
     *        the station to create an output for.
     * @return the output data.
     */
    public static StationOutput createSimpleStationOutput(Station station) {
        StationOutput stationOutput = createMinimalStation(station);
        stationOutput.addProperty("timeseries", createSimpleTimeseriesList(station));
        return stationOutput;
    }

    /**
     * @param station
     *        the station to create an output view for.
     * @return the output data with minimal values set.
     */
    private static StationOutput createMinimalStation(Station station) {
        StationOutput stationOutput = new StationOutput();
        stationOutput.setGeometry(getCoordinates(station));
        stationOutput.addProperty("station", station.getId());
        return stationOutput;
    }

    private static SosTimeseries[] createCompleteTimeseriesList(Station station) {
        List<SosTimeseries> timeseriesIds = new ArrayList<SosTimeseries>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            timeseriesIds.add(timeseries);
        }
        return timeseriesIds.toArray(new SosTimeseries[0]);
    }
    
    private static String[] createSimpleTimeseriesList(Station station) {
        List<String> timeseriesIds = new ArrayList<String>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            timeseriesIds.add(timeseries.getTimeseriesId());
        }
        return timeseriesIds.toArray(new String[0]);
    }

    private static GeojsonPoint getCoordinates(Station station) {
        EastingNorthing location = station.getLocation();
        String x = Double.toString(location.getEasting());
        String y = Double.toString(location.getNorthing());
        return GeojsonPoint.create(new String[] {x, y});
    }

    private StationOutput() {
        // for serialization
    }

}
