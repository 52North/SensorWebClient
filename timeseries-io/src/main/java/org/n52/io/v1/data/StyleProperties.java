
package org.n52.io.v1.data;

import java.util.HashMap;
import java.util.Map;

/**
 * The chart style options for a timeseries.
 */
public class StyleProperties {
    
    /**
     * The chart type, e.g. <code>line</code>, <code>bar</code>, ...
     */
    private String chartType = "line";
    
    private Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * @return the chart type, e.g. <code>line</code>, or <code>bar</code>.
     */
    public String getType() {
        return chartType;
    }

    public void setType(String type) {
        this.chartType = type;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
