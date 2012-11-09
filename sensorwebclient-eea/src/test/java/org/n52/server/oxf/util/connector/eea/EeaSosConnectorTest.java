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
package org.n52.server.oxf.util.connector.eea;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.valueDomains.spatial.BoundingBox;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EeaSosConnectorTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EeaSosConnectorTest.class);
    
    private static final String SF_SPATIAL_FEATURE = "/files/sf_spatial_feature.xml";
    
    private static final AReferencingHelper referenceHelper = AReferencingHelper.createEpsgForcedXYAxisOrder();
    
    private SFSamplingFeatureDocument sfSamplingFeature;

    private ArcGISSoeMetadataHandler sosConnector;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream(SF_SPATIAL_FEATURE);
        sfSamplingFeature = SFSamplingFeatureDocument.Factory.parse(is);
        sosConnector = new ArcGISSoeMetadataHandler();
    }

    @Test
    public void testGetPointOfSamplingFeatureType() throws XmlException {
        SFSamplingFeatureType feature = sfSamplingFeature.getSFSamplingFeature();
        ParsedPoint parsedPoint = sosConnector.getPointOfSamplingFeatureType(feature, referenceHelper);
        // <gml:Point gml:id="BETR701-point-location">
        //  <gml:pos srsName="http://www.opengis.net/def/crs/EPSG/0/4326">3.729 51.058</gml:pos>
        // </gml:Point>
        assertEquals(parsedPoint.getLon(), "3.729");
        assertEquals(parsedPoint.getLat(), "51.058");
        assertEquals(parsedPoint.getSrs(), "EPSG:4326");
        LOGGER.debug(parsedPoint.toString());
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

}
