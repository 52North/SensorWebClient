package org.n52.client.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.n52.client.util.ClientUtils.splitJsonObjects;

import org.junit.Test;


public class ClientUtilsTest {
    
    @Test public void
    shouldReadEmptyJson()
    {
        assertThat(splitJsonObjects("{}").length, is(1));
    }
    
    @Test public void
    shouldReadSingleJsonObject()
    {
        assertThat(splitJsonObjects("{\"parameter\":\"value\"}").length, is(1));
    }
    
    @Test public void
    shouldReadMultipleJsonObject()
    {
        assertThat(splitJsonObjects("{\"parameter\":\"value\"},{\\\"parameter\\\":\\\"value\\\"}").length, is(2));
    }
    
    @Test public void
    shouldReturnEmptyArrayIfInvalid()
    {
        assertThat(splitJsonObjects("{\"parameter\":\"value\"").length, is(0));
    }

}
