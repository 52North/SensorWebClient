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

package org.n52.api.v0.out;

import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.geojson.GeojsonFeature;
import org.n52.io.geojson.GeojsonPoint;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

import com.vividsolutions.jts.geom.Point;

/**
 * {@link Station} output data to be used for de-/marshalling web views.
 */
public class StationOutput extends GeojsonFeature {

    private static final long serialVersionUID = 7780608582026293480L;

    StationOutput() {
        // for serialization
    }

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
        stationOutput.addProperty("station", station.getLabel());
        return stationOutput;
    }

    private static TimeseriesOutput[] createCompleteTimeseriesList(Station station) {
        List<TimeseriesOutput> timeseriesIds = new ArrayList<TimeseriesOutput>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            timeseriesIds.add(new TimeseriesOutput(timeseries));
        }
        return timeseriesIds.toArray(new TimeseriesOutput[0]);
    }
    
    private static String[] createSimpleTimeseriesList(Station station) {
        List<String> timeseriesIds = new ArrayList<String>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            timeseriesIds.add(timeseries.getTimeseriesId());
        }
        return timeseriesIds.toArray(new String[0]);
    }

    private static GeojsonPoint getCoordinates(Station station) {
        Point location = station.getLocation();

        // TODO
        
        return createWithCoordinates(new Double[] {location.getX(), location.getY()});
    }

}
