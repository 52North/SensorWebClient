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

package org.n52.ext.access.client;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a time range container which can be used to define a time interval. However, as this class
 * intends to be GWT compatible without forcing non-GWT applications to include GWT dependencies,
 * {@link TimeRange} does not include any {@link Date} or {@link Calendar} dependencies, nor does it include
 * GWT specific implementations to support time.<br>
 * <br>
 * {@link TimeRange} expects ISO8601 date strings, restricted to the following form:
 * 
 * <pre>
 * {@code YYYY-MM-DD'T'HH:MM:SS}
 * </pre>
 * 
 * for example:
 * 
 * <pre>
 * {@code 2012-11-07T15:01:59}
 * </pre>
 */
public class TimeRange {

    private String start;
    private String end;

    private TimeRange(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public static TimeRange createTimeRange(String start, String end) {
        return new TimeRange(start, end);
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    /**
     * @return <code>true</code> if both start and end interval bounds are set, <code>false</code> otherwise.
     */
    public boolean isSetStartAndEnd() {
        return start != null && end != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TimeRange: [");
        sb.append("start: ").append(start).append(", ");
        sb.append("end: ").append(end).append("]");
        return sb.toString();
    }

}
