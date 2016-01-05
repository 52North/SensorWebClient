/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
        if (featureLocations == null || featureLocations.isEmpty()) {
            fail("No features have been parsed!");
        } else {
            assertThat(featureLocations.size(), is(3));
        }
    }

    @Test public void
    shouldHaveParsedFeatureNames() {

    }
}
