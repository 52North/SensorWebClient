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
package org.n52.api.v1.io;

import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.io.geojson.GeojsonPoint;
import org.n52.io.v1.data.OutputValue;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParameter;

import com.vividsolutions.jts.geom.Point;

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
    
    
    private List<Map<String, OutputValue>> createCondensedTimeseriesList(Station station) {
        List<Map<String, OutputValue>> timeseriesOutputs = new ArrayList<Map<String, OutputValue>>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            Map<String, OutputValue> timeseriesOutput = new HashMap<String, OutputValue>();
            timeseriesOutput.put("service", createOutputValue(timeseries.getSosService()));
            timeseriesOutput.put("offering", createOutputValue(timeseries.getOffering()));
            timeseriesOutput.put("procedure", createOutputValue(timeseries.getProcedure()));
            timeseriesOutput.put("phenomenon", createOutputValue(timeseries.getPhenomenon()));
            timeseriesOutput.put("feature", createOutputValue(timeseries.getFeature()));
            timeseriesOutputs.add(timeseriesOutput);
        }
        return timeseriesOutputs;
    }

    private OutputValue createOutputValue(TimeseriesParameter parameter) {
        OutputValue outputvalue = new OutputValue();
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
