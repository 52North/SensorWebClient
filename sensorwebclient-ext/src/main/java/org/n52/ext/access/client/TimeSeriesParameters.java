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

public class TimeSeriesParameters {

    private String serviceURL;

    private String version;

    private String offering;

    private String procedure;

    private String phenomenon;

    private String feature;

    private TimeRange timeRange;

    public TimeSeriesParameters(String serviceURL, String version, String offering, String procedure, String phenomenon, String feature) {
        this.serviceURL = serviceURL;
        this.version = version;
        this.offering = offering;
        this.procedure = procedure;
        this.phenomenon = phenomenon;
        this.feature = feature;
    }

    public boolean isSetTimeRange() {
        return this.timeRange != null;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    public String getServiceURL() {
        return this.serviceURL;
    }

    public String getVersion() {
        return this.version;
    }

    public String getOffering() {
        return this.offering;
    }

    public String getProcedure() {
        return this.procedure;
    }

    public String getPhenomenon() {
        return this.phenomenon;
    }

    public String getStation() {
        return this.feature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeSeriesParameters [");
        sb.append(this.serviceURL);
        sb.append(", ");
        sb.append(this.version);
        sb.append(", ");
        sb.append(this.offering);
        sb.append(", ");
        sb.append(this.procedure);
        sb.append(", ");
        sb.append(this.phenomenon);
        sb.append(", ");
        sb.append(this.feature);
        sb.append("]");
        return sb.toString();
    }
}
