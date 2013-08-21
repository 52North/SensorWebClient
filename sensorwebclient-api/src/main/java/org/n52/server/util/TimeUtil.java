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
