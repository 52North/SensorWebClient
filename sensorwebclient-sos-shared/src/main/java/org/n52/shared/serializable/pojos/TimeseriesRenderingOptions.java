
package org.n52.shared.serializable.pojos;

import java.util.Random;

/**
 * Planned successor to replace {@link TimeseriesProperties} in the future. Currently used to define client
 * side rendering options.
 */
public class TimeseriesRenderingOptions {

    private String hexColor = getRandomHexColor();

    private int lineWidth = 2; // default

    /**
     * @return a default instance of rendering options and random hex color.
     */
    public static TimeseriesRenderingOptions createDefaultRenderingOptions() {
        return new TimeseriesRenderingOptions();
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

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

}
