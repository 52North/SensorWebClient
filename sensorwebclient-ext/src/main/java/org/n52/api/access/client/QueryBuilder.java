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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

    public void removeLastComma(StringBuilder builder) {
        builder.deleteCharAt(builder.length() - 1);
    }

    void appendTimeRangeParameters(TimeRange time) {
        Calendar calendarStart = new GregorianCalendar();
        Calendar calendarEnd = new GregorianCalendar();
        if (time != null) {
            calendarStart.setTime(new Date(time.getStart()));
            calendarEnd.setTime(new Date(time.getEnd()));
        }
        else {
            Date now = new Date();
            calendarStart.setTime(now);
            calendarStart.roll(Calendar.MONTH, false);
            calendarEnd.setTime(now);
        }

        queryBuilder.append("&");
        queryBuilder.append("begin");
        queryBuilder.append("=");
        queryBuilder.append(getFormattedTimeString(calendarStart));
        queryBuilder.append("&");
        queryBuilder.append("end");
        queryBuilder.append("=");
        queryBuilder.append(getFormattedTimeString(calendarEnd));
    }

    /**
     * formats to yyyy-MM-dd'T'HH:mm:ss
     */
    private String getFormattedTimeString(Calendar pointInTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(pointInTime.get(Calendar.YEAR));
        sb.append("-");
        sb.append(pointInTime.get(Calendar.MONTH));
        sb.append("-");
        sb.append(pointInTime.get(Calendar.DAY_OF_MONTH));
        sb.append("T");
        sb.append(pointInTime.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(pointInTime.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(pointInTime.get(Calendar.SECOND));
        return sb.toString();
    }

    void appendParameters(String key, Iterable<String> values) {
        this.queryBuilder.append(key);
        this.queryBuilder.append("=");

        for (String value : values) {
            this.queryBuilder.append(value);
            this.queryBuilder.append(",");
        }
        removeLastComma(this.queryBuilder);
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
                this.queryBuilder.append(value);
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

    public int capacity() {
        return queryBuilder.capacity();
    }

    public int hashCode() {
        return queryBuilder.hashCode();
    }

    public void ensureCapacity(int minimumCapacity) {
        queryBuilder.ensureCapacity(minimumCapacity);
    }

    public void trimToSize() {
        queryBuilder.trimToSize();
    }

    public void setLength(int newLength) {
        queryBuilder.setLength(newLength);
    }

    public boolean equals(Object obj) {
        return queryBuilder.equals(obj);
    }

    public StringBuilder append(Object obj) {
        return queryBuilder.append(obj);
    }

    public StringBuilder append(String str) {
        return queryBuilder.append(str);
    }

    public StringBuilder append(StringBuffer sb) {
        return queryBuilder.append(sb);
    }

    public char charAt(int index) {
        return queryBuilder.charAt(index);
    }

    public StringBuilder append(CharSequence s) {
        return queryBuilder.append(s);
    }

    public int codePointAt(int index) {
        return queryBuilder.codePointAt(index);
    }

    public StringBuilder append(CharSequence s, int start, int end) {
        return queryBuilder.append(s, start, end);
    }

    public StringBuilder append(char[] str) {
        return queryBuilder.append(str);
    }

    public StringBuilder append(char[] str, int offset, int len) {
        return queryBuilder.append(str, offset, len);
    }

    public StringBuilder append(boolean b) {
        return queryBuilder.append(b);
    }

    public StringBuilder append(char c) {
        return queryBuilder.append(c);
    }

    public StringBuilder append(int i) {
        return queryBuilder.append(i);
    }

    public int codePointBefore(int index) {
        return queryBuilder.codePointBefore(index);
    }

    public StringBuilder append(long lng) {
        return queryBuilder.append(lng);
    }

    public StringBuilder append(float f) {
        return queryBuilder.append(f);
    }

    public StringBuilder append(double d) {
        return queryBuilder.append(d);
    }

    public StringBuilder appendCodePoint(int codePoint) {
        return queryBuilder.appendCodePoint(codePoint);
    }

    public StringBuilder delete(int start, int end) {
        return queryBuilder.delete(start, end);
    }

    public StringBuilder deleteCharAt(int index) {
        return queryBuilder.deleteCharAt(index);
    }

    public StringBuilder replace(int start, int end, String str) {
        return queryBuilder.replace(start, end, str);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        return queryBuilder.codePointCount(beginIndex, endIndex);
    }

    public StringBuilder insert(int index, char[] str, int offset, int len) {
        return queryBuilder.insert(index, str, offset, len);
    }

    public StringBuilder insert(int offset, Object obj) {
        return queryBuilder.insert(offset, obj);
    }

    public StringBuilder insert(int offset, String str) {
        return queryBuilder.insert(offset, str);
    }

    public StringBuilder insert(int offset, char[] str) {
        return queryBuilder.insert(offset, str);
    }

    public StringBuilder insert(int dstOffset, CharSequence s) {
        return queryBuilder.insert(dstOffset, s);
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return queryBuilder.offsetByCodePoints(index, codePointOffset);
    }

    public StringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        return queryBuilder.insert(dstOffset, s, start, end);
    }

    public StringBuilder insert(int offset, boolean b) {
        return queryBuilder.insert(offset, b);
    }

    public StringBuilder insert(int offset, char c) {
        return queryBuilder.insert(offset, c);
    }

    public StringBuilder insert(int offset, int i) {
        return queryBuilder.insert(offset, i);
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        queryBuilder.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    public StringBuilder insert(int offset, long l) {
        return queryBuilder.insert(offset, l);
    }

    public StringBuilder insert(int offset, float f) {
        return queryBuilder.insert(offset, f);
    }

    public StringBuilder insert(int offset, double d) {
        return queryBuilder.insert(offset, d);
    }

    public int indexOf(String str) {
        return queryBuilder.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return queryBuilder.indexOf(str, fromIndex);
    }

    public int lastIndexOf(String str) {
        return queryBuilder.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return queryBuilder.lastIndexOf(str, fromIndex);
    }

    public StringBuilder reverse() {
        return queryBuilder.reverse();
    }

    public String toString() {
        return queryBuilder.toString();
    }

    public void setCharAt(int index, char ch) {
        queryBuilder.setCharAt(index, ch);
    }

    public String substring(int start) {
        return queryBuilder.substring(start);
    }

    public CharSequence subSequence(int start, int end) {
        return queryBuilder.subSequence(start, end);
    }

    public String substring(int start, int end) {
        return queryBuilder.substring(start, end);
    }

    public void appendCompressedParameter() {
        this.queryBuilder.append("&");
        this.queryBuilder.append(COMPRESSION_PARAMETER);
        this.queryBuilder.append("=");
        this.queryBuilder.append(VALUE_ID_DELIMITER);
    }

}