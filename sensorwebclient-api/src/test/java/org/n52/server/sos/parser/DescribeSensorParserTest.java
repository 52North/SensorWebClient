package org.n52.server.sos.parser;

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
