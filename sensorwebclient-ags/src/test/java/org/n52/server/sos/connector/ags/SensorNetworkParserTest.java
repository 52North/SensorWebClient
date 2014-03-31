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
