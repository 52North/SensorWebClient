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
package org.n52.server.service.rest.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TimeseriesDataCollection {
    
    /**
     * Associates timeseries to a (custom client) id.
     */
    private Map<String, TimeseriesData> allTimeseries = new HashMap<String, TimeseriesData>();
    
    public TimeseriesDataCollection() {
        // for serialization
    }
    
    public void addAll(TimeseriesDataCollection timseriesCollection) {
        allTimeseries.putAll(timseriesCollection.getAllTimeseries());
    }
    
    public void addNewTimeseries(String reference, TimeseriesData timeseries) {
        this.allTimeseries.put(reference, timeseries);
    }
    
    public TimeseriesData getTimeseries(String timeseriesId) {
        return allTimeseries.get(timeseriesId);
    }
    
    public Map<String, TimeseriesData> getAllTimeseries() {
        return allTimeseries;
    }
    
//    public Set<String> getTimeseriesIds() {
//        return allTimeseries.keySet();
//    }

    public void setAllTimeseries(HashMap<String, TimeseriesData> timeseries) {
        this.allTimeseries = timeseries;
    }

    
}
