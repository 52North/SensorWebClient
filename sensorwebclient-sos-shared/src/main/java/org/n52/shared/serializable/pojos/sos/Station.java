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

package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;
import java.util.ArrayList;

import org.n52.shared.serializable.pojos.EastingNorthing;

/**
 * A {@link Station} represents a location where timeseries data is observed.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = 5016550440955260625L;

    private String id;

    private EastingNorthing location;

    private ArrayList<SosTimeseries> timeserieses;

    Station() {
        // for serialization
    }

    public Station(String stationId) {
        this.id = stationId;
        timeserieses = new ArrayList<SosTimeseries>();
    }

    public String getId() {
        return id;
    }

    public void setLocation(EastingNorthing location) {
        this.location = location;
    }

    public EastingNorthing getLocation() {
        return location;
    }

    public void addTimeseries(SosTimeseries timeseries) {
        timeserieses.add(timeseries);
    }

    public ArrayList<SosTimeseries> getTimeserieses() {
        return timeserieses;
    }

    public boolean contains(SosTimeseries timeseries) {
        return timeserieses.contains(timeseries);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Station: [ ").append("\n");
        sb.append("\tId: ").append(id).append("\n");
        sb.append("\tLocation: ").append(location).append("\n");
        sb.append("\t#Timeseries: ").append(timeserieses.size()).append(" ]\n");
        return sb.toString();
    }

    public boolean hasStationCategory(String filterCategory) {
        for (SosTimeseries timeseries : timeserieses) {
            if (timeseries.getCategory().equals(filterCategory)) {
                return true;
            }
        }
        return false;
    }

    public SosTimeseries getObservingTimeseriesByCategory(String category) {
        for (SosTimeseries paramConst : timeserieses) {
            if (paramConst.getCategory().equals(category)) {
                return paramConst;
            }
        }
        return null;
    }

    public boolean hasAtLeastOneParameterConstellation() {
        return timeserieses.size() > 0 ? true : false;
    }

    // @Override // fails during gwt compile
    public Station clone() {
        Station station = new Station(id);
        station.setLocation(location);
        station.setTimeserieses(new ArrayList<SosTimeseries>(timeserieses));
        return station;
    }
    
    private void setTimeserieses(ArrayList<SosTimeseries> timeserieses) {
        this.timeserieses = timeserieses;
    }

}
