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
package org.n52.server.oxf.util.connector.eea;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
