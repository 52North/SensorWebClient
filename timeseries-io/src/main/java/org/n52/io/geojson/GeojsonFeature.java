package org.n52.io.geojson;

import java.util.HashMap;
import java.util.Map;

public class GeojsonFeature extends GeojsonObject {

    private static final long serialVersionUID = 863297394860249486L;

    private static final String GEOJSON_TYPE_FEATURE = "Feature";
    
    protected Map<String, Object> properties = null;
    
    private GeojsonPoint geometry; // XXX should be GeojsonGeometry, but generics are different here 
    
    public String getType() {
        return GEOJSON_TYPE_FEATURE;
    }
    
    public GeojsonPoint getGeometry() {
        return geometry;
    }

    public void setGeometry(GeojsonPoint geometry) {
        this.geometry = geometry;
    }
    
    public void addProperty(String property, Object value) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        properties.put(property, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
}
