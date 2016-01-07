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
        value = value.replace("%26", "&");
        value = value.replace("%27", "'");
        value = value.replace("%28", "(");
        value = value.replace("%29", ")");
        value = value.replace("%2C", ",");
        value = value.replace("%2F", "/");
        value = value.replace("%3A", ":");
        value = value.replace("%3B", ";");
        value = value.replace("%3D", "=");
        value = value.replace("%3F", "?");
        value = value.replace("%40", "@");
        value = value.replace("%5B", "[");
        value = value.replace("%5D", "]");
        // for the last three, the order is important
        value = value.replace("+", " ");
        value = value.replace("%2B", "+");
        value = value.replace("%25", "%");
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
