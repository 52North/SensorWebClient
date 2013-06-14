
package org.n52.server.api.v0.model.geojson;

public abstract class GeojsonObject {

    protected static final String GEOJSON_TYPE_POINT = "Point";
    
    protected static final String GEOJSON_TYPE_FEATURE = "Feature";
    

    /**
     * @return the geojson type of the object (e.g. <code>Feature</code>, <code>Point</code>, etc.).
     */
    public abstract String getType();
}
