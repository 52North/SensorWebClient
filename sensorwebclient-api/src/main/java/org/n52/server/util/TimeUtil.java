/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.util;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple time utility.
 */
public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * @return a formatter with pattern 'yyyy-MM-dd'T'HH:mm:ss.SSSZ'.
     */
    public static SimpleDateFormat createIso8601Formatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    /**
     * Creates a custom date formatter. If the given format string is invalid a default ISO8601 formatter is
     * returned
     * 
     * @param format
     *        the format string.
     * @return a formatter with given pattern.
     * @throws NullPointerException
     *         if format is <code>null</code>
     * @see #createIso8601Formatter()
     */
    public static SimpleDateFormat createSimpleDateFormat(String format) {
        try {
            return new SimpleDateFormat(format);
        }
        catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid date format: {}", format);
            return createIso8601Formatter();
        }
    }

}
