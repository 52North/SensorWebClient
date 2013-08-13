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

    private ArrayList<SosTimeseries> observingTimeseries;

    private EastingNorthing location;

    private String label;
    
    // TODO make station identifiable

    Station() {
        // for serialization
    }

    public Station(String label) {
        this.label = label;
        observingTimeseries = new ArrayList<SosTimeseries>();
    }

    public String getLabel() {
        return label;
    }

    public void setLocation(EastingNorthing location) {
        this.location = location;
    }

    public EastingNorthing getLocation() {
        return location;
    }

    public void addTimeseries(SosTimeseries timeseries) {
        observingTimeseries.add(timeseries);
    }

    public ArrayList<SosTimeseries> getObservedTimeseries() {
        return observingTimeseries;
    }

    public boolean contains(SosTimeseries timeseries) {
        return observingTimeseries.contains(timeseries);
    }
    
    public boolean contains(String timeseriesId) {
        return getTimeseriesById(timeseriesId) != null;
    }
    
    public SosTimeseries getTimeseriesById(String timeseriesId) {
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getTimeseriesId().equals(timeseriesId)) {
                return timeseries;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Station: [ ").append("\n");
        sb.append("\tId: ").append(label).append("\n");
        sb.append("\tLocation: ").append(location).append("\n");
        sb.append("\t#Timeseries: ").append(observingTimeseries.size()).append(" ]\n");
        return sb.toString();
    }

    public boolean hasStationCategory(String filterCategory) {
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getCategory().equals(filterCategory)) {
                return true;
            }
        }
        return false;
    }

    public SosTimeseries getTimeseriesByCategory(String category) {
        for (SosTimeseries paramConst : observingTimeseries) {
            if (paramConst.getCategory().equals(category)) {
                return paramConst;
            }
        }
        return null;
    }

    public boolean hasAtLeastOneParameterConstellation() {
        return observingTimeseries.size() > 0 ? true : false;
    }

    // @Override // fails during gwt compile
    public Station clone() {
        Station station = new Station(label);
        station.setLocation(location);
        station.setObservingTimeseries(new ArrayList<SosTimeseries>(observingTimeseries));
        return station;
    }
    
    private void setObservingTimeseries(ArrayList<SosTimeseries> observingTimeseries) {
        this.observingTimeseries = observingTimeseries;
    }

}
