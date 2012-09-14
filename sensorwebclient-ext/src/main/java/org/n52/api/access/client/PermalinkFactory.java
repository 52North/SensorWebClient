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

package org.n52.api.access.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.n52.api.access.AccessLinkFactory;

public class PermalinkFactory implements AccessLinkFactory {

    protected Iterable<String> services;
    protected Iterable<String> features;
    protected Iterable<String> offerings;
    protected Iterable<String> phenomenons;
    protected Iterable<String> procedures;
    protected QueryBuilder queryBuilder;
    protected TimeRange timeRange;

    PermalinkFactory(TimeSeriesPermalinkBuilder builder) {
        this.queryBuilder = new QueryBuilder();
        this.services = builder.getServices();
        this.offerings = builder.getOfferings();
        this.procedures = builder.getProcedures();
        this.phenomenons = builder.getPhenomenons();
        this.features = builder.getFeatures();
        this.timeRange = builder.getTimeRange();
    }

    @Override
    public URL createAccessURL(String baseURL) throws MalformedURLException {
        URL accessURL = new URL(baseURL);
        String query = accessURL.getQuery();

        queryBuilder.append(query == null ? "?" : query + "&");
        queryBuilder.appendParameters("sos", this.services);
        queryBuilder.append("&");
        queryBuilder.appendParameters("stations", this.features);
        queryBuilder.append("&");
        queryBuilder.appendParameters("offering", this.offerings);
        queryBuilder.append("&");
        queryBuilder.appendParameters("procedures", this.procedures);
        queryBuilder.append("&");
        queryBuilder.appendParameters("phenomenons", this.phenomenons);

        queryBuilder.appendTimeRangeParameters(this.timeRange);

        // return new URL(accessURL.getHost() + this.queryBuilder.toString());
        return new URL(accessURL + this.queryBuilder.toString());
    }
}
