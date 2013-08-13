package org.n52.io.style;

import org.n52.io.v1.data.StyleProperties;

public class LineStyle extends Style {
    
    private static final String LINE_WIDTH = "lineWidth";
    
    private static final int DEFAULT_LINE_WIDTH = 2;
    
    private static final String LINE_TYPE = "lineType";
    
    private static final String DEFAULT_LINE_TYPE = "solid";
    
    public int getLineWidth() {
        if (hasProperty(LINE_WIDTH)) {
            return getPropertyAsInt(LINE_WIDTH);
        }
        return DEFAULT_LINE_WIDTH;
    }
    
    public String getLineType() {
        if (hasProperty(LINE_TYPE)) {
            return getPropertyAsString(LINE_TYPE);
        }
        return DEFAULT_LINE_TYPE;
    }
    
    public static LineStyle createFrom(StyleProperties options) {
        LineStyle lineStyleOptions = new LineStyle();
        lineStyleOptions.setProperties(options.getProperties());
        return lineStyleOptions;
    }
}
