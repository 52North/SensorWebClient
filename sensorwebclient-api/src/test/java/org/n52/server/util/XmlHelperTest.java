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
