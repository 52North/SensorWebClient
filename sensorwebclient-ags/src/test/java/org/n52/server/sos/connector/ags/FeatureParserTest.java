package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;

import java.io.IOException;
import java.util.Map;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;
import org.n52.shared.serializable.pojos.sos.Feature;

import com.vividsolutions.jts.geom.Point;

public class FeatureParserTest {

    private static final String FAKE_URL = "http://points.nowhere";
    
    private static final String GET_FOI_RESPONSE = "/files/get-features_subset.xml";
    
    private FeatureParser featureParser;

    @Before public void
    setUp() throws XmlException, IOException {
        featureParser = new FeatureParser(FAKE_URL, CRSUtils.createEpsgStrictAxisOrder());
        XmlObject featureResponse = loadXmlFileViaClassloader(GET_FOI_RESPONSE, getClass());
        assertThat(featureResponse.schemaType(), is(GetFeatureOfInterestResponseDocument.type));
    }
    
    @Test public void
    shouldParseLocations() throws XmlException, IOException {
        XmlObject featureResponse = loadXmlFileViaClassloader(GET_FOI_RESPONSE, getClass());
        Map<Feature, Point> featureLocations = featureParser.parseFeatures(featureResponse.newInputStream());
        if (featureLocations == null || featureLocations.size() == 0) {
            fail("No features have been parsed!");
        }
        assertThat(featureLocations.size(), is(3));
    }
    
    @Test public void
    shouldHaveParsedFeatureNames() {
        
    }
}
