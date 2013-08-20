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
import java.util.List;

import org.n52.api.v1.io.StationConverter;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class StationOutputAdapter implements ParameterService<StationOutput> {

    @Override
    public StationOutput[] getExpandedParameters(QueryMap map) {
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            Station[] stationsAsArray = getStationsAsArray(metadata);
            allStations.addAll(converter.convertExpanded(stationsAsArray));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput[] getCondensedParameters(QueryMap map) {
        List<StationOutput> allStations = new ArrayList<StationOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            StationConverter converter = new StationConverter(metadata);
            Station[] stationsAsArray = getStationsAsArray(metadata);
            allStations.addAll(converter.convertCondensed(stationsAsArray));
        }
        return allStations.toArray(new StationOutput[0]);
    }

    @Override
    public StationOutput[] getParameters(String[] stationIds) {
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
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.getStation(stationId) != null) {
                StationConverter converter = new StationConverter(metadata);
                return converter.convertExpanded(metadata.getStation(stationId));
            }
        }
        return null;
    }

    private Station[] getStationsAsArray(SOSMetadata metadata) {
        return metadata.getStations().toArray(new Station[0]);
    }
}
