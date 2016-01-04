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
