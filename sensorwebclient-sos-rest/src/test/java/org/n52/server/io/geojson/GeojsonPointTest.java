package org.n52.server.io.geojson;

import org.junit.Test;
import org.n52.server.io.geojson.GeojsonPoint;



public class GeojsonPointTest {

    @Test(expected = IllegalArgumentException.class) public void 
    shouldThrowIAEIfNullCoordinatesShallBeSet()
    {
        GeojsonPoint.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class) public void
    shouldThrowIAEIfCoordinatesOfDifferentDimensionShallBeSet() 
    {
        GeojsonPoint.create(new String[0]);
    }
    
}
