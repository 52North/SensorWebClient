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
