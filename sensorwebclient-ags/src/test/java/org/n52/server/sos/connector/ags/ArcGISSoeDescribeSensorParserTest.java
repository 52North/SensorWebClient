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
package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument.SensorML;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;

public class ArcGISSoeDescribeSensorParserTest {

    private final static String DESCRIBE_SENSOR_INSTANCE = "/files/describe-sensor-instance.xml";
    
    private final static String PHENOMENON = "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/1";
    
    private ArcGISSoeDescribeSensorParser parser;
    
    @Before public void 
    setUp() throws IOException, XmlException {
        XmlObject sml = XmlFileLoader.loadXmlFileViaClassloader(DESCRIBE_SENSOR_INSTANCE, getClass());
        parser = new ArcGISSoeDescribeSensorParser(sml);
        assertThat("XML is not a SensorML 1.0.1!", parser.getSensorML().schemaType(), is(SensorML.type));
    }
    
    @Test public void 
    shouldParseFirstAvailableUomFromInconsistentMultipleOutputSection() {
        assertThat("UOM code is not correct!", parser.getUomFor(PHENOMENON), is("mg.m-3"));
    }
    
    @Test public void
    shouldParseShortName() {
        assertThat("shortName is incorrect!", parser.getShortName(), is("GB_StationProcess_3746"));
    }

}
