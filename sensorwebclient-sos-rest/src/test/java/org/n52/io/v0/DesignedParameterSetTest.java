
package org.n52.io.v0;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.n52.api.v0.io.DesignedParameterSet;

public class DesignedParameterSetTest {

    @Test
    public void
            shouldCreateNonNullTimspan()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        assertNotNull(parameterSet.getTimespan());
    }

    @Test
    public void
            shouldCreateNonNullTimeseries()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        assertNotNull(parameterSet.getTimeseries());
    }

    @Test
    public void
            shouldCreateEmptyTimeseries()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        assertThat(parameterSet.getTimeseries().length, is(0));
    }

    @Test
    public void
            shouldSetValuableDefaultIfTimespanIsNull()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan(null);
        assertNotNull(parameterSet.getTimespan());
    }

    @Test
    public void
            shouldSetNegativeWidthAndHeightIfNullSize()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        assertTrue(parameterSet.getWidth() < 0);
        assertTrue(parameterSet.getHeight() < 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void
            shouldThrowExceptionWhenTimespanIsInvalid()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan("2013-23-23T20:23:23Z");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithStartAndEnd()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan("2007-03-01T13:00:00Z/2008-05-11T15:30:00Z");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithStartAndPeriod()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan("2007-03-01T13:00:00Z/P1Y2M10DT2H30M");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithPeriodAndEnd()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan("P1Y2M10DT2H30M/2008-05-11T15:30:00Z");
    }

    @Test(expected = IllegalArgumentException.class)
    public void
            shouldNotAcceptPeriodOnlyVersion()
    {
        DesignedParameterSet parameterSet = new DesignedParameterSet();
        parameterSet.setTimespan("P1Y2M10DT2H30M");
    }

}
