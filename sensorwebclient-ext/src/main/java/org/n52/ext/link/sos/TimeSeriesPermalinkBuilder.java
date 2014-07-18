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
package org.n52.ext.link.sos;

import java.util.ArrayList;
import java.util.List;

import org.n52.ext.link.AccessBuilder;
import org.n52.ext.link.AccessLinkFactory;

public class TimeSeriesPermalinkBuilder implements AccessBuilder<AccessLinkFactory> {

    private List<String> services = new ArrayList<String>();

    private List<String> versions = new ArrayList<String>();

    private List<String> offerings = new ArrayList<String>();

    private List<String> procedures = new ArrayList<String>();

    private List<String> phenomenons = new ArrayList<String>();

    private List<String> features = new ArrayList<String>();

    private TimeRange timeRange = null; // optional

    public TimeSeriesPermalinkBuilder addParameters(TimeSeriesParameters parameters) {
        this.services.add(parameters.getService());
        this.versions.add(parameters.getVersion());
        this.offerings.add(parameters.getOffering());
        this.procedures.add(parameters.getProcedure());
        this.phenomenons.add(parameters.getPhenomenon());
        this.features.add(parameters.getFeature());
        this.timeRange = parameters.getTimeRange();
        return this;
    }

    public Iterable<String> getServices() {
        return services;
    }

    public Iterable<String> getVersions() {
        return versions;
    }

    public Iterable<String> getOfferings() {
        return offerings;
    }

    public Iterable<String> getProcedures() {
        return procedures;
    }

    public Iterable<String> getPhenomenons() {
        return phenomenons;
    }

    public Iterable<String> getFeatures() {
        return features;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    @Override
    public AccessLinkFactory build() {
        if ( !isConsistent()) {
            throw new IllegalStateException("Parameter sizes do not match.");
        }
        return new PermalinkFactory(this);
    }

    private boolean isConsistent() {
        int size = this.procedures.size();
        boolean invalidFeaturesSize = size != this.features.size();
        boolean invalidOfferingsSize = size != this.offerings.size();
        boolean invalidPhenomenonsSize = size != this.phenomenons.size();
        boolean invalidServicesSize = size != this.services.size();
        if (invalidFeaturesSize || invalidOfferingsSize || invalidPhenomenonsSize || invalidServicesSize)
            return false;
        return true;
    }

}
