
package org.n52.io.style;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Aggregates common style options of multiple timeseries to render them on one chart.
 */
public abstract class Style {
    
    private static final String PARAMETER_COLOR = "color";

    private Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * @return a 6-digit hex color. If not set a random color will be returned.
     */
    public String getColor() {
        if (hasProperty(PARAMETER_COLOR)) {
            getPropertyAsString(PARAMETER_COLOR);
        }
        return getRandomHexColor();
    }
    
    private static String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private static String getNextFormattedRandomNumber() {
        String randomHex = Integer.toHexString(new Random().nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
    }
    
    boolean hasProperty(String property) {
        return properties.containsKey(property);
    }

    Object getProperty(String property) {
        return properties.get(property);
    }

    String getPropertyAsString(String property) {
        return (String) properties.get(property);
    }

    Double getPropertyAsDouble(String property) {
        return (Double) properties.get(property);
    }

    int getPropertyAsInt(String property) {
        return ((Integer) properties.get(property)).intValue();
    }

    boolean getPropertyAsBoolean(String property) {
        return ((Boolean) properties.get(property)).booleanValue();
    }

    Object[] getPropertyAsArray(String property) {
        return (Object[]) properties.get(property);
    }

    Map<String, Object> getProperties() {
        return properties;
    }

    void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
