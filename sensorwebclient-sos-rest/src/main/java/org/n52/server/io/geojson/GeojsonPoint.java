
package org.n52.server.io.geojson;

import java.util.Arrays;

import org.apache.bcel.generic.IALOAD;

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

    /**
     * @throws IllegalArgumentException
     *         if coordinates are <code>null</code> or do not contain two dimensional point.
     */
    public void setCoordinates(String[] coordinates) {
        if (coordinates == null || coordinates.length != 2) {
            String asString = Arrays.toString(coordinates);
            throw new IllegalArgumentException("Invalid Point coordinates: " + asString);
        }
        this.coordinates = coordinates;
    }
}
