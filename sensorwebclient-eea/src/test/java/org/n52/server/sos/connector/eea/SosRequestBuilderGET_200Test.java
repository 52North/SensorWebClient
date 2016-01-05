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
package org.n52.server.sos.connector.eea;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.n52.server.sos.connector.eea.SOSRequestBuilderGET_200;

public class SosRequestBuilderGET_200Test {
    
    private SOSRequestBuilderGET_200 builder;


    @Before
    public void setUp() {
        builder = new SOSRequestBuilderGET_200();
    }

    @Test
    public void testEncodePlusInParameter() {
//        return parameter.replace("+", "%2B");
        String encoded = builder.encodePlusInParameter("a string with a \"+\"");
        assertEquals("a string with a \"%2B\"", encoded);
    }
    
    @Test
    public void testFixTimeZone() {
        String incorrectTime = "2012-08-06T13:49:30.0+0200";
        String correctTime = builder.fixTimeZone(incorrectTime);
        assertEquals("2012-08-06T13:49:30.0+02:00", correctTime);
    }
    
    @Test
    public void testEncodeParameter() {
        String encoded = builder.encode("2012-08-06T13:49:30.0+02:00");
        assertEquals("2012-08-06T13%3A49%3A30.0%2B02%3A00", encoded);
        
        /*
         * http://en.wikipedia.org/wiki/Percent-encoding (omitting '!' and '*')
         * #   $   &   '   (   )   +   ,   /   :   ;   =   ?   @   [   ]
         * %23 %24 %26 %27 %28 %29 %2B %2C %2F %3A %3B %3D %3F %40 %5B %5D
         */
        
        String encodedCharacters = builder.encode("#$&'()+,/:;=?@[]");
        assertEquals("%23%24%26%27%28%29%2B%2C%2F%3A%3B%3D%3F%40%5B%5D", encodedCharacters);
    }
    
    @Test
    public void testCreateIso8601Duration() {
        String start = "2012-08-06T13:49:30.0+02:00";
        String end = "2012-08-01T13:49:30.0+02:00";
        String duration = builder.createIso8601Duration(start, end);
        assertEquals("2012-08-06T13:49:30.0+02:00/2012-08-01T13:49:30.0+02:00", duration);
    }

}
