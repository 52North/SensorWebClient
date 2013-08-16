package org.n52.io.style;

import org.n52.io.v1.data.StyleProperties;

public class LineStyle extends Style {
    
    private static final String WIDTH = "width";
    
    private static final int DEFAULT_LINE_WIDTH = 2;

    private static final int DEFAULT_DASH_GAP_WIDTH = 2;

    private static final int DEFAULT_DOT_WIDTH = 2;
    
    private static final String LINE_TYPE = "lineType";
    
    private static final String DEFAULT_LINE_TYPE = "solid";
    
    public int getDotWidth() {
        if (hasProperty(WIDTH)) {
            return getPropertyAsInt(WIDTH);
        }
        return DEFAULT_DOT_WIDTH;
    }
    
    public int getDashGapWidth() {
        if (hasProperty(WIDTH)) {
            return getPropertyAsInt(WIDTH);
        }
        return DEFAULT_DASH_GAP_WIDTH;
    }
    
    public int getLineWidth() {
        if (hasProperty(WIDTH)) {
            return getPropertyAsInt(WIDTH);
        }
        return DEFAULT_LINE_WIDTH;
    }
    
    public String getLineType() {
        if (hasProperty(LINE_TYPE)) {
            return getPropertyAsString(LINE_TYPE);
        }
        return DEFAULT_LINE_TYPE;
    }
    
    public static LineStyle createLineStyle(StyleProperties options) {
        if (options == null) {
            return createDefaultLineStyle();
        }
        LineStyle lineStyleOptions = new LineStyle();
        lineStyleOptions.setProperties(options.getProperties());
        return lineStyleOptions;
    }

    public static LineStyle createDefaultLineStyle() {
        return new LineStyle();
    }
    
}
