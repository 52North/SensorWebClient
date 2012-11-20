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

import org.n52.ext.access.AccessLinkFactory;

public class PermalinkFactory implements AccessLinkFactory {

    protected Iterable<String> services;
    protected Iterable<String> versions;
    protected Iterable<String> features;
    protected Iterable<String> offerings;
    protected Iterable<String> phenomenons;
    protected Iterable<String> procedures;
    protected QueryBuilder permalinkBuilder;
    protected TimeRange timeRange;

    PermalinkFactory(TimeSeriesPermalinkBuilder builder) {
        this.permalinkBuilder = new QueryBuilder();
        this.services = builder.getServices();
        this.versions = builder.getVersions();
        this.offerings = builder.getOfferings();
        this.procedures = builder.getProcedures();
        this.phenomenons = builder.getPhenomenons();
        this.features = builder.getFeatures();
        this.timeRange = builder.getTimeRange();
    }

    @Override
    public String createAccessURL(String baseURL) {
        permalinkBuilder.initialize(baseURL);
        permalinkBuilder.appendParameters("sos", this.services);
        permalinkBuilder.append("&");
        permalinkBuilder.appendParameters("versions", this.versions);
        permalinkBuilder.append("&");
        permalinkBuilder.appendParameters("stations", this.features);
        permalinkBuilder.append("&");
        permalinkBuilder.appendParameters("offerings", this.offerings);
        permalinkBuilder.append("&");
        permalinkBuilder.appendParameters("procedures", this.procedures);
        permalinkBuilder.append("&");
        permalinkBuilder.appendParameters("phenomenons", this.phenomenons);

        permalinkBuilder.appendTimeRangeParameters(this.timeRange);

         // return new URL(accessURL.getHost() + this.queryBuilder.toString());
        return permalinkBuilder.toString();
    }
    
}