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
