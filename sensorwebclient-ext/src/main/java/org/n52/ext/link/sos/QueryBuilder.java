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
import java.util.HashMap;
import java.util.List;

/**
 * Delegates to {@link StringBuilder} and extends it with utility methods to build a query string.
 */
class QueryBuilder {

    static final String VALUE_ID_DELIMITER = "-$-"; // http://www.faqs.org/rfcs/rfc1738.html
    static final String VALUE_ID_DELIMITER_REGEX = "-\\$-";
    static final String COMPRESSION_PARAMETER = "compr";

    private StringBuilder queryBuilder = new StringBuilder();
    
    String encodeValue(String value) {
        // ! # $ % & ' ( ) + , / : ; = ? @ [ ]
        // %21 %23 %24 %25 %26 %27 %28 %29 %2B %2C %2F %3A %3B %3D %3F %40 %5B %5D
        
        // for the first three, the order is important
        value = value.replace("%", "%25");
        value = value.replace("+", "%2B");
        value = value.replace(" ", "+");
        
        value = value.replace("!", "%21");
        value = value.replace("#", "%23");
        value = value.replace("$", "%24");
        value = value.replace("&", "%26");
        value = value.replace("'", "%27");
        value = value.replace("(", "%28");
        value = value.replace(")", "%29");
        value = value.replace(",", "%2C");
        value = value.replace("/", "%2F");
        value = value.replace(":", "%3A");
        value = value.replace(";", "%3B");
        value = value.replace("=", "%3D");
        value = value.replace("?", "%3F");
        value = value.replace("@", "%40");
        value = value.replace("[", "%5B");
        value = value.replace("]", "%5D");
        return value;
    }

    public void removeLastComma(StringBuilder builder) {
        builder.deleteCharAt(builder.length() - 1);
    }

    void appendTimeRangeParameters(TimeRange timeRange) {
        if (timeRange != null) {
            queryBuilder.append("&");
            queryBuilder.append("begin");
            queryBuilder.append("=");
            queryBuilder.append(encodeValue(timeRange.getStart()));
            queryBuilder.append("&");
            queryBuilder.append("end");
            queryBuilder.append("=");
            queryBuilder.append(encodeValue(timeRange.getEnd()));
        }
    }

    void appendCompressedParameters(String key, Iterable<String> values) {
        // full value --> short id:
        HashMap<String, Integer> valueIdMap = new HashMap<String, Integer>();
        // full value of duplicate --> already added to query:
        HashMap<String, Boolean> duplicateUsageMap = new HashMap<String, Boolean>();

        int i = 0;
        for (String value : values) {
            if (valueIdMap.containsKey(value)) {
                // duplicate value!
                if ( !duplicateUsageMap.containsKey(value))
                    duplicateUsageMap.put(value, Boolean.FALSE);
            }
            else {
                valueIdMap.put(value, Integer.valueOf(i));
            }
            i++;
        }

        this.queryBuilder.append(key);
        for (String value : values) {
            if ( !duplicateUsageMap.containsKey(value)) {
                this.queryBuilder.append(encodeValue(value));
            }
            else {
                // handle duplicate
                if (duplicateUsageMap.get(value).equals(Boolean.TRUE)) {
                    // already added, use id
                    this.queryBuilder.append(valueIdMap.get(value));
                }
                else {
                    this.queryBuilder.append(value);
                    this.queryBuilder.append(VALUE_ID_DELIMITER);
                    this.queryBuilder.append(valueIdMap.get(value));
                    duplicateUsageMap.put(value, Boolean.TRUE);
                }
            }
            this.queryBuilder.append(",");
        }
        removeLastComma(this.queryBuilder);
    }

    void appendUncompressedParameters(String key, List<String> values, String delimiter) {
        String delimiterRegex = delimiter;
        if (delimiterRegex.contains("$"))
            delimiterRegex = delimiterRegex.replace("$", "\\$");

        // create short id --> full value mapping
        HashMap<String, String> valueIdMap = new HashMap<String, String>();

        for (String value : values) {
            if (value.contains(delimiter)) {
                String[] valueId = value.split(delimiterRegex);
                String valueOnly = valueId[0];
                String idOnly = valueId[1];
                valueIdMap.put(idOnly, valueOnly);
            }
        }

        // uncompress the values
        ArrayList<String> uncompressedValues = new ArrayList<String>();
        for (String value : values) {
            // check if is first occurence of compressed value
            if (value.contains(delimiter)) {
                String[] valueId = value.split(delimiterRegex);
                uncompressedValues.add(valueId[0]);
            }
            // check if it is compressed value
            else if (valueIdMap.containsKey(value)) {
                uncompressedValues.add(valueIdMap.get(value));
            }
            // not compressed
            else
                uncompressedValues.add(value);
        }

        appendParameters(key, uncompressedValues);
    }

    public int length() {
        return queryBuilder.length();
    }

    void appendParameters(String key, Iterable<String> values) {
        queryBuilder.append(key);
        queryBuilder.append("=");
    
        for (String value : values) {
            queryBuilder.append(encodeValue(value));
            queryBuilder.append(",");
        }
        removeLastComma(queryBuilder);
    }

    public void appendCompressedParameter() {
        queryBuilder.append("&");
        queryBuilder.append(COMPRESSION_PARAMETER);
        queryBuilder.append("=");
        queryBuilder.append(VALUE_ID_DELIMITER);
    }

    /**
     * @param baseUrl
     *        the baseUrl.
     */
    public StringBuilder initialize(String baseUrl) {
        if (baseUrl == null) {
            return queryBuilder.insert(0, "?");
        }
        else if ( !baseUrl.contains("?")) {
            return queryBuilder.insert(0, baseUrl + "?");
        }
        else {
            return queryBuilder.insert(0, baseUrl + "&");
        }
    }

    @Override
    public String toString() {
        return queryBuilder.toString();
    }
    
    

}