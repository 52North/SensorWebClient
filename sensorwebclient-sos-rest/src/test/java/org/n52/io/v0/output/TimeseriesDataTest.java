/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.io.v0.output;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.n52.api.v0.out.TimeseriesData.newTimeseriesData;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.api.v0.out.TimeseriesData;
import org.n52.api.v0.out.TimeseriesData.TimeseriesValue;



public class TimeseriesDataTest {
    
    private final static Long FIRST = 100L;
    
    private final static Long SECOND = 200L;
    
    private final static Long THIRD = 300L;
    
    private final static Long FOURTH = 400L;

    private final static Long FIFTH = 500L;

    private Map<Long, String> inputData;
    
    private TimeseriesData timeseriesData;

    @Before public void
    setUp() 
    {
        inputData = new HashMap<Long, String>();
        inputData.put(SECOND, "secondValue");
        inputData.put(FIRST, "firstValue");
        inputData.put(THIRD, "thirdValue");
        inputData.put(FIFTH, "fourthValue");
        inputData.put(FOURTH, "fifthValue");
        timeseriesData = newTimeseriesData(inputData, "cm");
    }
    

    @Test public void
    shouldHaveSameSizeAsInputData()
    {
        TimeseriesValue[] sortedValues = timeseriesData.getValues();
        assertNotNull(sortedValues);
        assertThat(inputData.size(), is(sortedValues.length));
    }
    
    @Test public void
    shouldHaveCorrectOrdering()
    {
        for (TimeseriesValue value : timeseriesData.getValues()) {
            System.out.println(value);
        }
        assertThat(timeseriesData.getValues()[0].getValue(), equalTo(inputData.get(FIRST)));
        assertThat(timeseriesData.getValues()[1].getValue(), equalTo(inputData.get(SECOND)));
        assertThat(timeseriesData.getValues()[2].getValue(), equalTo(inputData.get(THIRD)));
        assertThat(timeseriesData.getValues()[3].getValue(), equalTo(inputData.get(FOURTH)));
        assertThat(timeseriesData.getValues()[4].getValue(), equalTo(inputData.get(FIFTH)));
    }
}
