/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.ext.link.sos;

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
