package org.n52.io.geojson;


import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import org.junit.Test;



public class GeojsonPointTest {

    @Test(expected = NullPointerException.class) public void 
    shouldThrowIAEIfNullCoordinatesShallBeSet()
    {
        GeojsonPoint.createWithCoordinates(null);
    }
    
    @Test(expected = IllegalArgumentException.class) public void
    shouldThrowIAEIfCoordinatesOfDifferentDimensionShallBeSet() 
    {
        createWithCoordinates(new Double[0]);
    }
    
}
