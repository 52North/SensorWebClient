
package org.n52.server.service.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ParameterSetTest {

    @Test
    public void
            shouldCreateNonNullTimspan()
    {
        ParameterSet parameterSet = new ParameterSet();
        assertNotNull(parameterSet.getTimespan());
    }

    @Test
    public void
            shouldCreateNonNullTimeseries()
    {
        ParameterSet parameterSet = new ParameterSet();
        assertNotNull(parameterSet.getReferencedTimeseries());
    }

    @Test
    public void
            shouldCreateEmptyTimeseries()
    {
        ParameterSet parameterSet = new ParameterSet();
        assertTrue(parameterSet.getReferencedTimeseries().isEmpty());
    }

    @Test
    public void
            shouldSetValuableDefaultIfTimespanIsNull()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan(null);
        assertNotNull(parameterSet.getTimespan());
    }

    @Test
    public void
            shouldSetNegativeWidthAndHeightIfNullSize()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSize(null);
        assertTrue(parameterSet.getWidth() < 0);
        assertTrue(parameterSet.getHeight() < 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void
            shouldThrowExceptionWhenTimespanIsInvalid()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan("2013-23-23T20:23:23Z");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithStartAndEnd()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan("2007-03-01T13:00:00Z/2008-05-11T15:30:00Z");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithStartAndPeriod()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan("2007-03-01T13:00:00Z/P1Y2M10DT2H30M");
    }

    @Test
    public void
            shouldAcceptValidIntervalWithPeriodAndEnd()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan("P1Y2M10DT2H30M/2008-05-11T15:30:00Z");
    }

    @Test(expected = IllegalArgumentException.class)
    public void
            shouldNotAcceptPeriodOnlyVersion()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan("P1Y2M10DT2H30M");
    }

    @Test public void
    shouldReturnCorrectWidth()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSize("123,234");
        assertThat(parameterSet.getWidth(), is(123));
    }
    
    @Test public void
    shouldReturnCorrectHeight()
    {
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSize("123,234");
        assertThat(parameterSet.getHeight(), is(234));
    }

}
