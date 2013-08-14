package org.n52.io.geojson;

import java.util.Arrays;

public abstract class GeojsonGeometry extends GeojsonObject {

    private static final long serialVersionUID = -2611259809054586079L;
    
    /**
     * @throws IllegalArgumentException
     *         if coordinates are <code>null</code> or do not contain two dimensional point.
     */
    protected Double[] checkCoordinates(Double[] coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Coordinates must not be null.");
        }
        if (coordinates.length != 2) {
            String asString = Arrays.toString(coordinates);
            throw new IllegalArgumentException("Invalid Point coordinates: " + asString);
        }
        return coordinates;
    }
    
}
