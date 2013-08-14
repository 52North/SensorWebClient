package org.n52.io.style;

import org.n52.io.v1.data.StyleProperties;


public class BarStyle extends Style {

private static final String BAR_WIDTH = "barWidth";
    
    private static final int DEFAULT_BAR_WIDTH = 10;
    
    public int getBarWidth() {
        if (hasProperty(BAR_WIDTH)) {
            return getPropertyAsInt(BAR_WIDTH);
        }
        return DEFAULT_BAR_WIDTH;
    }
    
    public static BarStyle createFrom(StyleProperties options) {
        BarStyle barStyleOptions = new BarStyle();
        barStyleOptions.setProperties(options.getProperties());
        return barStyleOptions;
    }
    
}
