/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.sos.connector.eea;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.valueDomains.spatial.BoundingBox;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Point;

public class EeaSosConnectorTest {
    
    private static final double ALLOWED_DELTA = 0.0001;

    private static final String SF_SPATIAL_FEATURE = "/files/sf_spatial_feature.xml";

    private static final String VERSION_200 = "2.0.0";

    private static final String FAKE_URL = "http://fake.url";
    
    private static CRSUtils referenceHelper;
    
    private SFSamplingFeatureDocument sfSamplingFeature;

    private ArcGISSoeMetadataHandler sosConnector;

    @Before
    public void setUp() throws Exception {
        referenceHelper = CRSUtils.createEpsgForcedXYAxisOrder();
        InputStream is = this.getClass().getResourceAsStream(SF_SPATIAL_FEATURE);
        sfSamplingFeature = SFSamplingFeatureDocument.Factory.parse(is);
        sosConnector = new ArcGISSoeMetadataHandler(createSosMetadata());
    }

    @Test
    public void testGetPointOfSamplingFeatureType() throws XmlException, FactoryException {
        SFSamplingFeatureType feature = sfSamplingFeature.getSFSamplingFeature();
        Point parsedPoint = sosConnector.getPointOfSamplingFeatureType(feature, referenceHelper);
        // <gml:Point gml:id="BETR701-point-location">
        //  <gml:pos srsName="http://www.opengis.net/def/crs/EPSG/0/4326">3.729 51.058</gml:pos>
        // </gml:Point>
        assertThat(parsedPoint.getX(), closeTo(3.729, ALLOWED_DELTA));
        assertThat(parsedPoint.getY(), closeTo(51.058, ALLOWED_DELTA));
    }
    
    @Test
    public void testCreateBboxString() {
        double[] lowerLeft = new double[] { -176.0,-70.5 };
        double[] upperRight = new double[] { 179.0,88.7 };
        IBoundingBox bbox = new BoundingBox("EPSG:4325", lowerLeft, upperRight);
        String bboxString = sosConnector.createBboxString(bbox, referenceHelper);
        assertEquals("om:featureOfInterest/*/sams:shape,-176.0,-70.5,179.0,88.7,urn:ogc:def:crs:EPSG::4325", bboxString);
        
        bbox = new BoundingBox("4325", lowerLeft, upperRight);
        bboxString = sosConnector.createBboxString(bbox, referenceHelper);
        assertEquals("om:featureOfInterest/*/sams:shape,-176.0,-70.5,179.0,88.7,urn:ogc:def:crs:EPSG::4325", bboxString);
    }
    
    private static SOSMetadata createSosMetadata() {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        builder
            .addServiceVersion(VERSION_200)
            .addServiceURL(FAKE_URL);
        return new SOSMetadata(builder);
    }
}
