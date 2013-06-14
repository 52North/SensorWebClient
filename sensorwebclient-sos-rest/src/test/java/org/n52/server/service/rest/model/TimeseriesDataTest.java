package org.n52.server.service.rest.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.n52.server.service.rest.model.TimeseriesData.newTimeseriesData;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.server.service.rest.model.TimeseriesData.TimeseriesValue;



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
