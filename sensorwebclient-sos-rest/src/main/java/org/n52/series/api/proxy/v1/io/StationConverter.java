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
package org.n52.series.api.proxy.v1.io;

import static org.n52.io.geojson.old.GeojsonPoint.createWithCoordinates;

import java.util.HashMap;
import java.util.Map;

import org.n52.io.geojson.old.GeojsonPoint;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParameter;

import com.vividsolutions.jts.geom.Point;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.StationOutput;

public class StationConverter extends OutputConverter<Station, StationOutput> {

    public StationConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public StationOutput convertExpanded(Station station) {
        StationOutput convertedStation = convertCondensed(station);
        convertedStation.addProperty("timeseries", createCondensedTimeseriesList(station));
        return convertedStation;
    }

    @Override
    public StationOutput convertCondensed(Station station) {
        StationOutput convertedStation = new StationOutput();
        convertedStation.setGeometry(getCoordinates(station));
        convertedStation.addProperty("id", station.getGlobalId());
        convertedStation.addProperty("label", station.getLabel());
        return convertedStation;
    }
    
    
    private  Map<String, Map<String, ParameterOutput>> createCondensedTimeseriesList(Station station) {
        Map<String, Map<String, ParameterOutput>> timeseriesOutputs = new HashMap<String, Map<String, ParameterOutput>>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            Map<String, ParameterOutput> timeseriesOutput = new HashMap<String, ParameterOutput>();
            timeseriesOutput.put("service", createOutputValue(timeseries.getSosService()));
            timeseriesOutput.put("offering", createOutputValue(timeseries.getOffering()));
            timeseriesOutput.put("procedure", createOutputValue(timeseries.getProcedure()));
            timeseriesOutput.put("phenomenon", createOutputValue(timeseries.getPhenomenon()));
            timeseriesOutput.put("feature", createOutputValue(timeseries.getFeature()));
            timeseriesOutputs.put(timeseries.getTimeseriesId(), timeseriesOutput);
        }
        return timeseriesOutputs;
    }

    private ParameterOutput createOutputValue(TimeseriesParameter parameter) {
        ParameterOutput outputvalue = new ParameterOutput() {};
        outputvalue.setId(parameter.getGlobalId());
        outputvalue.setLabel(parameter.getLabel());
        return outputvalue;
    }

    private GeojsonPoint getCoordinates(Station station) {
        Point location = station.getLocation();
        double x = location.getX();
        double y = location.getY();
        return createWithCoordinates(new Double[] {x, y});
    }
}
