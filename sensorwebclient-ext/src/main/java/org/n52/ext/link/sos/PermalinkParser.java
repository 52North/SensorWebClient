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
