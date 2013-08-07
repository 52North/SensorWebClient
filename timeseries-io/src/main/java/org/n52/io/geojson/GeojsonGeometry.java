package org.n52.io.geojson;

public abstract class GeojsonGeometry extends GeojsonObject {

    protected String[] coordinates;

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinates = coordinates;
    }
}
