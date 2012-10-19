package org.n52.xml;

import static org.junit.Assert.*;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.server.oxf.util.parser.DescribeSensorParser;

public class XQueryTest {
    
    private static final String DESCRIBE_SENSOR_RESPONSE_FILE = "/files/describe_sensor_response.xml";
    private XmlObject xml;

    @Before
    public void setUp() throws Exception {
        String file = DESCRIBE_SENSOR_RESPONSE_FILE;
        xml = XmlFileLoader.loadSoapBodyFromXmlFileViaClassloader(file, "DescribeSensorResponse", getClass());
    }

    @Test
    public void testFileLoaded() {
        assertNotNull(xml);
    }
    
    @Test
    public void testSetDataStreamToParse() throws Exception {
        DescribeSensorParser parser = new DescribeSensorParser(xml.newInputStream(), "2.0.0");
        // just to see if invalid GmlId is replaced accordingly
    }

}
