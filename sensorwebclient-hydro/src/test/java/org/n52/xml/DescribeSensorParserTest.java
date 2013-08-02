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
package org.n52.xml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadSoapBodyFromXmlFileViaClassloader;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

public class DescribeSensorParserTest {
    
    private static final String DESCRIBE_SENSOR_RESPONSE_FILE = "/files/sensor_description_with_invalid_gmlid.xml";
    
    private XmlObject xml;

    @Before
    public void setUp() throws Exception {
        String file = DESCRIBE_SENSOR_RESPONSE_FILE;
        xml = loadSoapBodyFromXmlFileViaClassloader(file, "DescribeSensorResponse", getClass());
    }

    @Test
    public void testFileLoaded() {
        assertNotNull(xml);
    }
    
    @Test
    public void shouldHandleInvalidNcName() {
        try {
            // check if invalid GmlId is replaced accordingly
            SOSMetadata metadata = new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0"));
            DescribeSensorParser parser = new DescribeSensorParser(xml.newInputStream(), metadata);
        }
        catch (IOException e) {
            fail("Error reading XML.");
        }
        catch (XMLHandlingException e) {
            fail("Error handling XML.");
        }
        catch (XmlException e) {
            fail("Error parsing XML.");
        }
    }

}
