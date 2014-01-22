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
package org.n52.server.util;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;

public class XmlHelperTest {
    
    private static final String SENSOR_ML_101 = "/files/test-sensorml-101.xml";
    
    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();
    
    {
        namespaceDeclarations.put("sml", "http://www.opengis.net/sensorML/1.0.1");
        namespaceDeclarations.put("swe", "http://www.opengis.net/swe/1.0.1");
    }
    
    private XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    private SensorMLDocument smlDoc;

    @Before
    public void setUp() throws Exception {
        smlDoc = (SensorMLDocument) XmlFileLoader.loadXmlFileViaClassloader(SENSOR_ML_101, getClass());
        assertNotNull(smlDoc);
    }

    @Test public void 
    shouldParseSystemViaXPath() {
        SystemType systemType = xmlHelper.parseFirst(smlDoc, "$this//sml:member//sml:System", SystemType.class);
        assertNotNull(systemType);
    }

}
