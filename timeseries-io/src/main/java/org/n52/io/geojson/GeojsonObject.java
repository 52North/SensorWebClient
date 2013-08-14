
package org.n52.io.geojson;

import java.io.Serializable;

public abstract class GeojsonObject implements Serializable {
    
    private static final long serialVersionUID = -6879838545330014414L;
    
    private GeojsonCrs crs;
    
    public void setCrs(GeojsonCrs crs) {
        this.crs = crs;
    }

    public GeojsonCrs getCrs() {
        return crs;
    }

    /**
     * @return the geojson type of the object (e.g. <code>Feature</code>, <code>Point</code>, etc.).
     */
    public abstract String getType();
}
