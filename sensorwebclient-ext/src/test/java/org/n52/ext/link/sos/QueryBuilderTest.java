/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.ext.link.sos;

import static org.junit.Assert.assertEquals;

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
