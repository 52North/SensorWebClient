/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

public class DescribeSensorParserTest {
    
    private static final String SENSOR_ML_101 = "/files/test-sensorml-101.xml";
    
    private DescribeSensorParser parser;

    @Before
    public void setUp() throws Exception {
        SOSMetadata metadata = new SOSMetadataBuilder().build();
        XmlObject file = XmlFileLoader.loadXmlFileViaClassloader(SENSOR_ML_101, getClass());
        parser = new DescribeSensorParser(file.newInputStream(), metadata);
    }
    
    @Test public void
    shouldParseReferenceValuesFromCapabilitiesSection()
    {
        assertThat(parser.parseReferenceValues().size(), is(5));
    }
}
