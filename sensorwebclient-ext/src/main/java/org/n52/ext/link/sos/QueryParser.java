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

import static org.n52.ext.link.sos.PermalinkParameter.BEGIN;
import static org.n52.ext.link.sos.PermalinkParameter.END;
import static org.n52.ext.link.sos.PermalinkParameter.FEATURES;
import static org.n52.ext.link.sos.PermalinkParameter.OFFERINGS;
import static org.n52.ext.link.sos.PermalinkParameter.PHENOMENONS;
import static org.n52.ext.link.sos.PermalinkParameter.PROCEDURES;
import static org.n52.ext.link.sos.PermalinkParameter.SERVICES;
import static org.n52.ext.link.sos.PermalinkParameter.VERSIONS;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParser {

    /**
     * Associates UPPER paramter names with the parameter value string (if multiple as CSV).
     */
    private Map<String, String> kvps;

    private boolean compressed;

    QueryParser(String query, boolean compressed) {
        this.kvps = parseKvps(query);
        this.compressed = compressed;
    }

    Map<String, String> parseKvps(String query) {
        Map<String, String> parsedKvps = new HashMap<String, String>();
        if (query == null || query.isEmpty()) {
            return parsedKvps;
        }
        else {
            String[] splittedKvps = query.split("&");
            for (String kvp : splittedKvps) {
                addKvp(kvp, parsedKvps);
            }
            return parsedKvps;
        }
    }

    /**
     * Parses and adds the decodes parameter value to the provided map. If the given KVP is not two-part it
     * is ignored and not added to the map.
     * 
     * @param kvp
     *        the key-value-pair to add.
     * @param parsedKvps
     *        the container to add the parsed KVP to.
     */
    void addKvp(String kvp, Map<String, String> parsedKvps) {
        String[] keyValue = kvp.split("=", 2);
        if (keyValue.length != 2) {
            return;
        }
        String upperCaseName = keyValue[0].toUpperCase();
        String decodedValue = decodeValue(keyValue[1]);
        parsedKvps.put(upperCaseName, decodedValue);
    }

    String decodeValue(String value) {
        // ! # $ % & ' ( ) + , / : ; = ? @ [ ]
        // %21 %23 %24 %25 %26 %27 %28 %29 %2B %2C %2F %3A %3B %3D %3F %40 %5B %5D
        value = value.replace("%21", "!");
        value = value.replace("%23", "#");
        value = value.replace("%24", "$");
        value = value.replace("%25", "%");
        value = value.replace("%26", "&");
        value = value.replace("%27", "'");
        value = value.replace("%28", "(");
        value = value.replace("%29", ")");
        value = value.replace("%2C", ",");
        value = value.replace("%2F", ",");
        value = value.replace("%2C", "/");
        value = value.replace("%3A", ":");
        value = value.replace("%3B", ";");
        value = value.replace("%3D", "=");
        value = value.replace("%3F", "?");
        value = value.replace("%40", "@");
        value = value.replace("%5B", "[");
        value = value.replace("%5D", "]");
        // for the last two, the order is important
        value = value.replace("+", " ");
        value = value.replace("%2B", "+");
        return value;
    }

    public Collection<String> parseServices() {
        String serviceValues = kvps.get(SERVICES.name());
        return parseCommaSeparatedValues(serviceValues);
    }

    public Collection<String> parseVersions() {
        String versionValues = kvps.get(VERSIONS.name());
        return parseCommaSeparatedValues(versionValues);
    }

    public Collection<String> parseFeatures() {
        String featureValues = kvps.get(FEATURES.name());
        return parseCommaSeparatedValues(featureValues);
    }

    public Collection<String> parseOfferings() {
        String offeringValues = kvps.get(OFFERINGS.name());
        return parseCommaSeparatedValues(offeringValues);
    }

    public Collection<String> parseProcedures() {
        String procedureValues = kvps.get(PROCEDURES.name());
        return parseCommaSeparatedValues(procedureValues);
    }

    public Collection<String> parsePhenomenons() {
        String phenomenonValues = kvps.get(PHENOMENONS.name());
        return parseCommaSeparatedValues(phenomenonValues);
    }

    public TimeRange parseTimeRange() {
        String begin = kvps.get(BEGIN.name());
        String end = kvps.get(END.name());
        return TimeRange.createTimeRange(begin, end);
    }

    private List<String> parseCommaSeparatedValues(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        if (compressed) {
            // TODO add decompressing support
            throw new UnsupportedOperationException("Compressing not implemented yet.");
        }
        else {
            return Arrays.asList(value.split(","));
        }
    }

}
