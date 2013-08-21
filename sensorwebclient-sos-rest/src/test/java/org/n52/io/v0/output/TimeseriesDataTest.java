/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
