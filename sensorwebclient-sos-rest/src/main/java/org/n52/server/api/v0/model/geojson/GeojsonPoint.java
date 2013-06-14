package org.n52.server.api.v0.model.geojson;

public class GeojsonPoint extends GeojsonGeometry {

    private String[] coordinates;
    
    public static GeojsonPoint create(String[] coordinates) {
        GeojsonPoint sfGeometry = new GeojsonPoint();
        sfGeometry.setCoordinates(coordinates);
        return sfGeometry;
    }
    
    public String getType() {
        return GEOJSON_TYPE_POINT;
    }

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinates = coordinates;
    }
}

