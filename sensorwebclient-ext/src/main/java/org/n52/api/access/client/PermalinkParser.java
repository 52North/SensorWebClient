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

import static org.n52.api.access.client.PermalinkParameter.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermalinkParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(PermalinkParser.class);

    private String[] kvps;

    private boolean compressed; // TODO add compressed parsing

    PermalinkParser(String queryString, boolean compressed) {
        if (queryString != null) {
            this.kvps = queryString.split("&");
            this.compressed = compressed;
        }
    }

    public Iterable<String> parseServices() {
        return getValuesForKey(SOS.name());
    }

    public Iterable<String> parseStations() {
        return getValuesForKey(STATIONS.name());
    }

    public Iterable<String> parseOfferings() {
        return getValuesForKey(OFFERING.name());
    }

    public Iterable<String> parseProcedures() {
        return getValuesForKey(PROCEDURES.name());
    }

    public Iterable<String> pasePhenomenons() {
        return getValuesForKey(PHENOMENONS.name());
    }

    // public TimeRange parseTimeRange() {
    // String begin = getSingleValueForKey(BEGIN.name());
    // String end = getSingleValueForKey(END.name());
    // }

    private Collection<String> getValuesForKey(String key) {
        try {
            for (String kvp : kvps) {
                String[] keyValue = parseKeyValuePair(kvp);
                if (keyValue[0].equalsIgnoreCase(key)) {
                    String value = keyValue[1];
                    if ( !compressed) {
                        return Arrays.asList(value.split(","));
                    }
                    else {
                        // TODO uncompression!
                    }
                }
            }
        }
        catch (PermalinkException e) {
            LOGGER.warn("Parsing services parameter failed.", e);
        }
        return Collections.emptyList();
    }

    private String[] parseKeyValuePair(String kvp) throws PermalinkException {
        String[] keyValue = kvp.split("=");
        if (keyValue.length == 2)
            return keyValue;

        throw new PermalinkException("Key-Value pair is invalid: " + kvp);
    }

}
