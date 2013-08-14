
package org.n52.io.geojson;


public class GeojsonPoint extends GeojsonGeometry {

    private static final long serialVersionUID = 4348077077881433456L;
    
    private static final String GEOJSON_TYPE_POINT = "Point";

    protected Double[] coordinates;
    
    public static GeojsonPoint createWithCoordinates(Double[] coordinates) {
        GeojsonPoint sfGeometry = new GeojsonPoint();
        sfGeometry.setCoordinates(coordinates);
        return sfGeometry;
    }
    
    public void setCoordinates(Double[] coordinates) {
        this.coordinates = checkCoordinates(coordinates);
    }


    public String getType() {
        return GEOJSON_TYPE_POINT;
    }
    
    public Double[] getCoordinates() {
        return coordinates;
    }

}
