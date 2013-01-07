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
package org.n52.shared.responses;

import java.util.Date;
import java.util.HashMap;

public class TimeSeriesDataResponse extends RepresentationResponse {

    private static final long serialVersionUID = 6907927979169769766L;

    /** The data. ts_id to date to data */
    private HashMap<String, HashMap<Long, String>> data = null;

    private TimeSeriesDataResponse() {
        // serializable for GWT needs empty default constructor
    }

    public TimeSeriesDataResponse(HashMap<String, HashMap<Long, String>> data) {
        this.data = data;
    }

    public HashMap<String, HashMap<Long, String>> getPayloadData() {
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
