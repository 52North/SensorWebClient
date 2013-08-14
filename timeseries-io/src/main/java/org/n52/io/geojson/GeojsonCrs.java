package org.n52.io.geojson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GeojsonCrs extends GeojsonObject {
    
    private static final long serialVersionUID = 5964748458745655509L;

    private static final String TYPE_NAME = "name";
    
    private static final String TYPE_LINK = "link";
    
    private Map<String, String> properties;

    private String type = TYPE_NAME;
    
    GeojsonCrs() {
        this.properties = new HashMap<String, String>();
    }
    
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    
    void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
    
    @JsonIgnore
    public String getName() {
        return properties.get("name");
    }
    
    @JsonIgnore
    public String getHRef() {
        return properties.get("href");
    }
    
    @JsonIgnore
    public String getLinkType() {
        return properties.get("type");
    }
    
    public static GeojsonCrs createCrs84Named() {
        return createNamedCRS("urn:ogc:def:crs:OGC:1.3:CRS84");
    }
    
    public static GeojsonCrs createNamedCRS(String name) {
        if (name == null) {
            return createCrs84Named();
        }
        GeojsonCrs namedCrs = new GeojsonCrs();
        namedCrs.addProperty("name", name);
        namedCrs.setType(TYPE_NAME);
        return namedCrs;
    }
    
    public static GeojsonCrs createLinkedCRS(String url, String type) {
        GeojsonCrs linkedCrs = new GeojsonCrs();
        linkedCrs.addProperty("type", type);
        linkedCrs.addProperty("href", url);
        linkedCrs.setType(TYPE_LINK);
        return linkedCrs;
    }
    
    
    
}
