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
package org.n52.series.api.proxy.v0.out;

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
