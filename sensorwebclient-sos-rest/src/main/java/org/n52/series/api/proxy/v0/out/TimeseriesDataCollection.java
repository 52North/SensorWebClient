/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.HashMap;
import java.util.Map;

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
    
    public void setAllTimeseries(HashMap<String, TimeseriesData> timeseries) {
        this.allTimeseries = timeseries;
    }

    
}
