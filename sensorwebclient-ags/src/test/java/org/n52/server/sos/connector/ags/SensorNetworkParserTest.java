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
package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import net.opengis.sensorML.x101.ComponentType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;

public class SensorNetworkParserTest {
    
    private static final String SENSOR_NETWORK_SUBSET = "/files/describe-sensor-network_subset.xml";

    @Test
    public void 
    givenSensorNetwork_parsingNetwork_parsedCorrectNumberOfNetworkMembers() throws XmlException, IOException {
        XmlObject network = XmlFileLoader.loadXmlFileViaClassloader(SENSOR_NETWORK_SUBSET, getClass());
        Map<String, ComponentType> descriptions = new SensorNetworkParser().parseSensorDescriptions(network.newInputStream());
        assertThat(descriptions.size(), is(4));
    }

}
