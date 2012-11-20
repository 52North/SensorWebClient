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
