package org.n52.ext.link.sos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class QueryBuilderTest {

    private QueryBuilder queryBuilder;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testEncodeValues() {
        queryBuilder = new QueryBuilder();
        //  !   #   $   %   &   '   (   )   +   ,   /   :   ;   =   ?   @   [   ]
        // %21 %23 %24 %25 %26 %27 %28 %29 %2B %2C %2F %3A %3B %3D %3F %40 %5B %5D
        assertEquals("value+with+spaces", queryBuilder.encodeValue("value with spaces"));
        assertEquals("value%2Cwith%2Ccommas", queryBuilder.encodeValue("value,with,commas"));
        assertEquals("value+with+spaces%2Bplus+sign", queryBuilder.encodeValue("value with spaces+plus sign"));
        assertEquals("%21+%23+%24", queryBuilder.encodeValue("! # $"));
        assertEquals("%25+%26+%27+%28", queryBuilder.encodeValue("% & ' ("));
        assertEquals("%26+%27+%25+%28", queryBuilder.encodeValue("& ' % ("));
    }

}
