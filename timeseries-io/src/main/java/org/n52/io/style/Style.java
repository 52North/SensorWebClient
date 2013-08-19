
package org.n52.io.style;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Aggregates common style options of multiple timeseries to render them on one chart.
 */
public abstract class Style {

    private static final String PARAMETER_COLOR = "color";

    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * @return a 6-digit hex color. If not set a random color will be returned.
     */
    public String getColor() {
        if (hasProperty(PARAMETER_COLOR)) {
            return getPropertyAsString(PARAMETER_COLOR);
        }
        return getRandomHexColor();
    }

    private String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private String getNextFormattedRandomNumber() {
        String randomHex = toHexString(new Random().nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
    }

    Object getProperty(String property) {
        return properties == null ? null : properties.get(property);
    }

    String getPropertyAsString(String property) {
        return (String) getProperty(property);
    }

    /**
     * @param property
     *        the property name.
     * @return the property value as double.
     * @throws NullPointerException
     *         if properties were not set (which means that default values should be expected).
     * @throws NumberFormatException
     *         if property value is not a double.
     * @see #hasProperty(String)
     */
    double getPropertyAsDouble(String property) {
        if (properties == null) {
            throw new NullPointerException("No property with name '" + property + "'.");
        }
        return parseDouble(properties.get(property));
    }

    /**
     * @param property
     *        the property name.
     * @return the property value as int.
     * @throws NullPointerException
     *         if properties were not set (which means that default values should be expected).
     * @throws NumberFormatException
     *         if property value is not an integer.
     * @see #hasProperty(String)
     */
    int getPropertyAsInt(String property) {
        if (properties == null) {
            throw new NullPointerException("No property with name '" + property + "'.");
        }
        return parseInt(properties.get(property));
    }

    /**
     * @param property
     *        the property name.
     * @return the property value as boolean.
     * @throws NullPointerException
     *         if properties were not set (which means that default values should be expected).
     * @see #hasProperty(String)
     */
    boolean getPropertyAsBoolean(String property) {
        if (properties == null) {
            throw new NullPointerException("No property with name '" + property + "'.");
        }
        return parseBoolean(properties.get(property));
    }

    Object[] getPropertyAsArray(String property) {
        return (Object[]) getProperty(property);
    }

    boolean hasProperty(String property) {
        return !shallExpectDefault() && properties.containsKey(property);
    }

    private boolean shallExpectDefault() {
        return properties == null;
    }

    Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the style properties. If not set or <code>null</code> the default properties are used. However,
     * default styles can be random values and should not be expected to be reproducable.
     * 
     * @param properties
     *        style options. If <code>null</code> or not set default values are chose.
     */
    void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}