/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import static org.n52.ext.link.sos.QueryBuilder.COMPRESSION_PARAMETER;

import java.util.Collection;


/**
 * Adapts between a given permalink and provides access to the {@link PermalinkParameter}s relevant for an SERVICES
 * timeseries:
 * <ul>
 * <li>{@link PermalinkParameter.SERVICES}</li>
 * <li>{@link PermalinkParameter.VERSIONS}</li>
 * <li>{@link PermalinkParameter.OFFERINGS}</li>
 * <li>{@link PermalinkParameter.PHENOMENONS}</li>
 * <li>{@link PermalinkParameter.PROCEDURES}</li>
 * <li>{@link PermalinkParameter.FEATURES}</li>
 * <li>{@link PermalinkParameter.BEGIN}</li>
 * <li>{@link PermalinkParameter.END}</li>
 * </ul>
 */
public class PermalinkParser {

    private QueryParser parser;
    private boolean compressed;

    public PermalinkParser(String permalink) {
        this.compressed = isCompressed(permalink);
        if (permalink.contains("?")) {
            String queryString = permalink.substring(permalink.indexOf('?') + 1);
            this.parser = new QueryParser(queryString, compressed);
        }
        else {
            this.parser = new QueryParser(null, compressed);
        }
    }

    private boolean isCompressed(String queryString) {
        return queryString.contains(COMPRESSION_PARAMETER);
    }

    public Collection<String> getServices() {
        return parser.parseServices();
    }

    public Collection<String> getVersions() {
        return parser.parseVersions();
    }

    public Collection<String> getOfferings() {
        return parser.parseOfferings();
    }

    public Collection<String> getProcedures() {
        return parser.parseProcedures();
    }

    public Collection<String> getPhenomenons() {
        return parser.parsePhenomenons();
    }

    public Collection<String> getFeatures() {
        return parser.parseFeatures();
    }

    public TimeRange getTimeRange() {
        return parser.parseTimeRange();
    }

}
