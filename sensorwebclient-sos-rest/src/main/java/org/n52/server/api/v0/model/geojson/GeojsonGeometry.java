package org.n52.server.api.v0.model.geojson;

public abstract class GeojsonGeometry extends GeojsonObject {

    protected String[] coordinates;

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinates = coordinates;
    }
}
