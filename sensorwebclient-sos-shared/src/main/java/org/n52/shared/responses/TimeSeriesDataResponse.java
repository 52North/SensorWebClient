/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.responses;

import java.util.Date;
import java.util.HashMap;

public class TimeSeriesDataResponse extends RepresentationResponse {

    private static final long serialVersionUID = 6907927979169769766L;

    private HashMap<String, HashMap<Long, Double>> data = null;

    TimeSeriesDataResponse() {
        // serializable for GWT needs empty default constructor
    }

    public TimeSeriesDataResponse(HashMap<String, HashMap<Long, Double>> data) {
        this.data = data;
    }

    public HashMap<String, HashMap<Long, Double>> getPayloadData() {
        return this.data;
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nData for timeseries \n"); 
        for (String key : data.keySet()) {
            sb.append("\t").append(key).append("\n");
            int count = 5;
            sb.append("\tsize: ").append(data.get(key).size()).append("\n");
            sb.append("\t\t");
            for (Long date : data.get(key).keySet()) {
                sb.append(new Date(date)).append(" : ").append(data.get(key).get(date)).append(";  ");
                count--;
                if (count == 0) {
                    break;
                }
            }
            sb.append("...\n");
        }
        return sb.toString();
    }

}
