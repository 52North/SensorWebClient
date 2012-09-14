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
package org.n52.server.oxf.util.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.SosInstanceContentHandler;
import org.n52.shared.serializable.pojos.ServiceMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosInstanceContentHandlerTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SosInstanceContentHandlerTest.class);

    private static final String EEA_SOE_URL = "http://ags.dev.52north.org:6080/arcgis/rest/services/ObservationDB/MapServer/exts/SOSExtension/";

    private static final String PEGELONLINE_URL = "http://pegelonline.wsv.de/webservices/gis/gdi-sos";

    private static final String IRCELINE_URL = "http://sos.irceline.be/sos";
    
    private static final String FLUGGS_URL = "http://fluggs.wupperverband.de/sos/sos";
    
    private static final String DEFAULT_CONNECTOR = "org.n52.server.oxf.util.parser.DefaultSosConnector";
    
    private static final String EEA_CONNECTOR = "org.n52.server.oxf.util.connector.eea.EEASOSConnector";
    
    private static final String DEFAULT_ADAPTER = "org.n52.server.oxf.util.access.oxfExtensions.SOSAdapter_OXFExtension";
    
    private static final String EEA_ADAPTER = "org.n52.server.oxf.util.connector.eea.SOSAdapterByGET";

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
        
        Map<String, ServiceMetadata> serviceMetadatas = ConfigurationContext.getServiceMetadatas();
        assertEquals("Unequal amount of SOS instances.", 4, ConfigurationContext.getSOSMetadatas().size());
        assertCorrectPegelOnline(serviceMetadatas.get(PEGELONLINE_URL));
        assertCorrectIrceLine(serviceMetadatas.get(IRCELINE_URL));
        assertCorrectEeaSos(serviceMetadatas.get(EEA_SOE_URL));
        assertCorrectFluggsSos(serviceMetadatas.get(FLUGGS_URL));
    }

    private void assertCorrectPegelOnline(ServiceMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(PEGELONLINE_URL, metadata.getId());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(true, metadata.isWaterML());
        assertEquals(DEFAULT_CONNECTOR, metadata.getConnector());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(150, metadata.getRequestChunk());
        assertEquals(false, metadata.isAutoZoom());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private void assertCorrectIrceLine(ServiceMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(IRCELINE_URL, metadata.getId());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(DEFAULT_CONNECTOR, metadata.getConnector());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(150, metadata.getRequestChunk());
        assertEquals(true, metadata.isAutoZoom());
        // TODO check bbox
        // TODO check defaultZoom
    }
    
    private void assertCorrectEeaSos(ServiceMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(EEA_SOE_URL, metadata.getId());
        assertEquals("2.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(EEA_CONNECTOR, metadata.getConnector());
        assertEquals(EEA_ADAPTER, metadata.getAdapter());
        assertEquals(200, metadata.getRequestChunk());
        assertEquals(false, metadata.isAutoZoom());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private void assertCorrectFluggsSos(ServiceMetadata serviceMetadata) {
        SOSMetadata metadata = (SOSMetadata) serviceMetadata;
        assertEquals(FLUGGS_URL, metadata.getId());
        assertEquals("1.0.0", metadata.getVersion());
        assertEquals(false, metadata.isWaterML());
        assertEquals(DEFAULT_CONNECTOR, metadata.getConnector());
        assertEquals(DEFAULT_ADAPTER, metadata.getAdapter());
        assertEquals(100, metadata.getRequestChunk());
        assertEquals(true, metadata.isAutoZoom());
        // TODO check bbox
        // TODO check defaultZoom
    }

    private class MySosInstanceContentHandlerSeam extends SosInstanceContentHandler {
        MySosInstanceContentHandlerSeam() {}
    }
}
