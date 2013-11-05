package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;

import java.io.IOException;
import java.util.Map;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;

import com.vividsolutions.jts.geom.Point;

public class FeatureParserTest {

    private static final String GET_FOI_RESPONSE = "/files/get-foi_complete.xml";
    
    private FeatureParser featureParser;

    @Before public void
    setUp() throws XmlException, IOException {
        featureParser = new FeatureParser(CRSUtils.createEpsgStrictAxisOrder());
    }
    
    @Test public void
    shouldParseToGetFoiResponse() throws XmlException, IOException {
        XmlObject featureResponse = loadXmlFileViaClassloader(GET_FOI_RESPONSE, getClass());
        assertThat(featureResponse.schemaType(), is(GetFeatureOfInterestResponseDocument.type));
    }
    
    @Test public void
    shouldParseFeatureMembersFromFileExample() throws XmlException, IOException {
        XmlObject featureResponse = loadXmlFileViaClassloader(GET_FOI_RESPONSE, getClass());
        Map<String, Point> features = featureParser.parseFeatures(featureResponse.newInputStream());
        assertNotNull(features);
        assertThat(features.size(), is(1000));
    }
}
