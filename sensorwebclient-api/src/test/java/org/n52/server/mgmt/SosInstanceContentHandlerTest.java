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
package org.n52.server.mgmt;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.mgmt.SosInstanceContentHandler;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosInstanceContentHandlerTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SosInstanceContentHandlerTest.class);

    private static final String EEA_SOE_URL = "http://ags.dev.52north.org:6080/arcgis/rest/services/ObservationDB/MapServer/exts/SOSExtension/";

    private static final String PEGELONLINE_URL = "http://pegelonline.wsv.de/webservices/gis/gdi-sos";

    private static final String IRCELINE_URL = "http://sos.irceline.be/sos";
    
    private static final String FLUGGS_URL = "http://fluggs.wupperverband.de/sos/sos";
    
    private static final String DEFAULT_CONNECTOR = "org.n52.server.oxf.util.parser.DefaultMetadataHandler";
    
    private static final String EEA_CONNECTOR = "org.n52.server.oxf.util.connector.eea.ArcGISSoeMetadataHandler";
    
    private static final String DEFAULT_ADAPTER = "org.n52.server.oxf.util.access.oxfExtensions.SOSAdapter_OXFExtension";
    
    private static final String SOS_INSTANCES_FILE = "/files/test-sos-instances.data.xml";
    
    @Test
    public void testParsing() {
        try {
            InputStream is = getClass().getResourceAsStream(SOS_INSTANCES_FILE);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(is, new MySosInstanceContentHandlerSeam());
        } catch (Exception e) {
            LOGGER.error("Could not parse preconfigured sos instances.", e);
            fail(String.format("Error parsing %s", SOS_INSTANCES_FILE));
        }
        
        Map<String, SOSMetadata> serviceMetadatas = ConfigurationContext.getServiceMetadatas();
        assertEquals("Unequal amount of SOS instances.", 4, ConfigurationContext.getSOSMetadatas().size());
        assertCorrectPegelOnline(serviceMetadatas.get(PEGELONLINE_URL));
        assertCorrectIrceLine(serviceMetadatas.get(IRCELINE_URL));
        assertCorrectEeaSos(serviceMetadatas.get(EEA_SOE_URL));
        assertCorrectFluggsSos(serviceMetadatas.get(FLUGGS_URL));
    }

    private void assertCorrectPegelOnline(SOSMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(PEGELONLINE_URL, metadata.getServiceUrl());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(DEFAULT_CONNECTOR, metadata.getSosMetadataHandler());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(300, metadata.getRequestChunk());
        
        assertTrue(metadata.isWaterML());
        assertFalse(metadata.isAutoZoom());
        assertFalse(metadata.isForceXYAxisOrder());
        assertTrue(metadata.isSupportsFirstLatest());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private void assertCorrectIrceLine(SOSMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(IRCELINE_URL, metadata.getServiceUrl());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(DEFAULT_CONNECTOR, metadata.getSosMetadataHandler());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(300, metadata.getRequestChunk());
        assertEquals(false, metadata.isAutoZoom());
        assertFalse(metadata.isSupportsFirstLatest());
        // TODO check bbox
        // TODO check defaultZoom
    }
    
    private void assertCorrectEeaSos(SOSMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(EEA_SOE_URL, metadata.getServiceUrl());
        assertEquals("2.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(EEA_CONNECTOR, metadata.getSosMetadataHandler());
        assertEquals(true, metadata.isForceXYAxisOrder());
        assertEquals(500, metadata.getRequestChunk());
        assertEquals(false, metadata.isAutoZoom());
        assertFalse(metadata.isSupportsFirstLatest());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private void assertCorrectFluggsSos(SOSMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(FLUGGS_URL, metadata.getServiceUrl());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(DEFAULT_CONNECTOR, metadata.getSosMetadataHandler());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(200, metadata.getRequestChunk());
        assertEquals(true, metadata.isAutoZoom());
        assertFalse(metadata.isSupportsFirstLatest());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private class MySosInstanceContentHandlerSeam extends SosInstanceContentHandler {
        MySosInstanceContentHandlerSeam() {}
    }
}
