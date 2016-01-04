/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.series.api.proxy.v0;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.n52.series.api.proxy.v0.io.DesignedParameterSet;

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
