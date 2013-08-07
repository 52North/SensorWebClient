package org.n52.io.geojson;


import org.junit.Test;



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
