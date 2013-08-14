
package org.n52.io.v0;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.n52.api.v0.io.Vicinity;
import org.n52.io.crs.BoundingBox;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VicinityTest {
    
    private static final double ERROR_DELTA = 0.1;

    private String circleAroundNorthPole = "{\"center\":[\"-89.99\",\"89.999\"],\"radius\":\"500\"}";
    
    private String circleAroundSouthPole = "{\"center\":[\"23\",\"-89.999\"],\"radius\":\"500\"}";
    
    private String circleCenterAtGreenwhichAndEquator = "{\"center\":[\"0\",\"0\"],\"radius\":\"500\"}";

    @Test
    public void
    shouldHaveInversedLatitudesWhenCenterIsOnEquator()
    {
        Vicinity vicinity = createRadiusAtNorthPole(circleCenterAtGreenwhichAndEquator);
        BoundingBox bounds = vicinity.calculateBounds();
        double llLatitudeOfSmallCircle = bounds.getLowerLeftCorner().getNorthing();
        double urLatitudeOfSmallCircle = bounds.getUpperRightCorner().getNorthing();
        assertThat(llLatitudeOfSmallCircle, closeTo(-urLatitudeOfSmallCircle, ERROR_DELTA));
    }

    @Test
    public void
    shouldHaveInversedLongitudesWhenCenterIsOnGreenwhich()
    {
        Vicinity vicinity = createRadiusAtNorthPole(circleCenterAtGreenwhichAndEquator);
        BoundingBox bounds = vicinity.calculateBounds();
        double llLongitudeOfGreatCircle = bounds.getLowerLeftCorner().getEasting();
        double urLongitudeOnGreatCircle = bounds.getUpperRightCorner().getEasting();
        assertThat(llLongitudeOfGreatCircle, closeTo(-urLongitudeOnGreatCircle, ERROR_DELTA));
    }
    
    @Test
    public void
    shouldHaveCommonLatitudeCircleWhenCenterIsNorthPole()
    {
        Vicinity vicinity = createRadiusAtNorthPole(circleAroundNorthPole);
        BoundingBox bounds = vicinity.calculateBounds();
        double llLatitudeOfSmallCircle = bounds.getLowerLeftCorner().getNorthing();
        double urLatitudeOfSmallCircle = bounds.getUpperRightCorner().getNorthing();
        assertThat(llLatitudeOfSmallCircle, closeTo(urLatitudeOfSmallCircle, ERROR_DELTA));
    }

    @Test
    public void
    shouldHaveCommonLatitudeCircleWhenCenterIsSouthPole()
    {
            Vicinity vicinity = createRadiusAtNorthPole(circleAroundSouthPole);
            BoundingBox bounds = vicinity.calculateBounds();
            double llLatitudeOfSmallCircle = bounds.getLowerLeftCorner().getNorthing();
            double urLatitudeOfSmallCircle = bounds.getUpperRightCorner().getNorthing();
            assertThat(llLatitudeOfSmallCircle, closeTo(urLatitudeOfSmallCircle, ERROR_DELTA));
    }

    private Vicinity createRadiusAtNorthPole(String circleJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(circleJson, Vicinity.class);
        }
        catch (JsonParseException e) {
            fail("Could not parse GeoJson");
        }
        catch (IOException e) {
            fail("Could not read GeoJson");
        }
        return null;
    }
    
}
