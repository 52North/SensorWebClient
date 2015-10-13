/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadSoapBodyFromXmlFileViaClassloader;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

public class DescribeSensorParserTest {
    
    private static final String DESCRIBE_SENSOR_RESPONSE_FILE = "/files/sensor_description_with_invalid_gmlid.xml";

    private static final String SML_101_POSITION_VECTOR_4326 = "/files/sensorPositionVector_4326.xml";
    
    private static final String SML_20_POSITION_VECTOR_4326 = "/files/sml20_sensorPositionVector_4326.xml";
    
    private static final double LATITUDE_EPSG4326 = -26.06340;
    private static final double LONGITUDE_EPSG4326 = 27.69591;


    private Point crs84Point;

    @Before
    public void setUp() throws Exception {
        CRSUtils strictEpsg = CRSUtils.createEpsgStrictAxisOrder();
        crs84Point = strictEpsg.createPoint(LONGITUDE_EPSG4326, LATITUDE_EPSG4326, 1506.0, "CRS:84");
    }

    @Test
    public void shouldHandleInvalidNcName() throws XmlException, IOException, OXFException {
        String file = DESCRIBE_SENSOR_RESPONSE_FILE;
        XmlObject sml = loadSoapBodyFromXmlFileViaClassloader(file, "DescribeSensorResponse", getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getSimpleMetadata());
        
        // TODO check if invalid GmlId is replaced accordingly
    }
    
    private SOSMetadata getSimpleMetadata() {
        return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0"));
    }

    @Test 
    public void shouldParseStrict4326PositionToCrs84Position() throws XmlException, IOException, FactoryException, TransformException, OXFException {
        XmlObject sml = loadXmlFileViaClassloader(SML_101_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getSimpleMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test 
    public void shouldParseLonLatOrdered4326PositionToCrs84Position() throws XmlException, IOException, FactoryException, TransformException, OXFException {
        XmlObject sml = loadXmlFileViaClassloader(SML_101_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getForceXYOrderingMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test 
    public void shouldParseStrict4326PositionToCrs84PositionSensorMLv20() throws XmlException, IOException, FactoryException, TransformException, OXFException {
        XmlObject sml = loadXmlFileViaClassloader(SML_20_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getSimpleMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test 
    public void shouldParseLonLatOrdered4326PositionToCrs84PositionSensorMLv20() throws XmlException, IOException, FactoryException, TransformException, OXFException {
        XmlObject sml = loadXmlFileViaClassloader(SML_20_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getForceXYOrderingMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    private SOSMetadata getForceXYOrderingMetadata() {
        return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0").setForceXYAxisOrder(true));
    }

    private DescribeSensorParser createParserFromFile(XmlObject sensorML, SOSMetadata metadata) throws OXFException {
        try {
            assertNotNull(sensorML);
            return new DescribeSensorParser(sensorML.newInputStream(), metadata);
        }
        catch (IOException e) {
            fail("Error reading XML.");
        }
        catch (XMLHandlingException e) {
            fail("Error handling XML.");
        }
        catch (XmlException e) {
            fail("Error parsing XML.");
        }
        catch (FactoryException e) {
            fail("Could not create default CRS.");
        }
        return null;
    }
    
}
