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

package org.n52.server.service.rest;

import static java.lang.Integer.parseInt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

/**
 * Represents a parameter object to request data from one or multiple timeseries.
 */
public class ParameterSet {

    /**
     * The timespan of interest (as <a href="http://en.wikipedia.org/wiki/ISO_8601#Time_intervals">ISO8601
     * interval</a> excluding the Period only version).
     */
    private String timespan;

    /**
     * The size of the requested timeseries graph.
     */
    private String size;

    /**
     * A collection of explicit timeseries associated to their (custom client) ids.
     * 
     * @see {@link #addTimeseries(SosTimeseries)}
     * @see {@link #addTimeseries(String, SosTimeseries)}
     */
    private HashMap<String, SosTimeseries> referencedTimeseries;

    /**
     * Creates an instance with non-null default values.
     */
    public ParameterSet() {
        size = createDefaultSize();
        timespan = createDefaultTimespan();
        referencedTimeseries = new HashMap<String, SosTimeseries>();
    }

    private String createDefaultTimespan() {
        DateTime now = new DateTime();
        DateTime lastMonth = now.minusMonths(1);
        return new Interval(lastMonth, now).toString();
    }

    private String createDefaultSize() {
        return validateSize(null);
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        if (timespan == null) {
            this.timespan = createDefaultTimespan();
        }
        else {
            this.timespan = validateTimespan(timespan);
        }
    }

    private String validateTimespan(String timespan) {
        return Interval.parse(timespan).toString();
    }

    /**
     * @return the requested width or negative number if no size was set.
     */
    public int getWidth() {
        return parseInt(getSizeValues(size)[0]);
    }

    /**
     * @return the requested height or negative number if no size was set.
     */
    public int getHeight() {
        return parseInt(getSizeValues(size)[1]);
    }

    public void setSize(String size) {
        this.size = validateSize(size);
    }

    private String validateSize(String size) {
        if (size == null) {
            return "-1,-1";
        }
        String[] values = getSizeValues(size);
        int width = parseInt(values[0]);
        int height = parseInt(values[1]);
        return width + "," + height;
    }

    private String[] getSizeValues(String size) {
        String[] values = size.trim().split(",");
        if (values.length != 2) {
            throw new ArrayIndexOutOfBoundsException("Invalid size! Must be of form '<width>,<height>'");
        }
        return values;
    }

    public Set<String> getTimeseriesReferences() {
        return referencedTimeseries.keySet();
    }

    public Map<String, SosTimeseries> getReferencedTimeseries() {
        return referencedTimeseries;
    }

    public void setReferencedTimseries(HashMap<String, SosTimeseries> timeseries) {
        this.referencedTimeseries = timeseries;
    }

    /**
     * @param reference
     *        the (custom client) reference which references a timeseries.
     * @return the referenced timeseries
     */
    public SosTimeseries getTimeseriesByReference(String reference) {
        return referencedTimeseries.get(reference);
    }

    /**
     * Adds a timeseries to the parameter set.
     * 
     * @param timeseries
     *        the timeseries to add.
     */
    public void addTimeseries(SosTimeseries timeseries) {
        this.referencedTimeseries.put(timeseries.getTimeseriesId(), timeseries);
    }

    /**
     * Adds a timeseries with custom (client) association id to the parameters set.
     * 
     * @param clientReference
     *        the client's reference to the timeseries.
     * @param timeseries
     *        the timeseries to add.
     */
    public void addTimeseries(String clientReference, SosTimeseries timeseries) {
        this.referencedTimeseries.put(clientReference, timeseries);
    }

}
