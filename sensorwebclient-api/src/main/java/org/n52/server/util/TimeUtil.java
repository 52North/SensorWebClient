
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
