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
package org.n52.xml;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadSoapBodyFromXmlFileViaClassloader;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.constraints.AssertTrue;

import net.opengis.swe.x101.PositionDocument;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.QuantityDocument;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hamcrest.number.IsCloseTo;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

public class DescribeSensorParserTest {
    
    private static final String DESCRIBE_SENSOR_RESPONSE_FILE = "/files/sensor_description_with_invalid_gmlid.xml";

    private static final String SML_POSITION_VECTOR_4326 = "/files/sensorPositionVector_4326.xml";

    private static final double LATITUDE_EPSG31466 = 5659928.636;
    private static final double LONGITUDE_EPSG31466 = 2569725.188;
    private static final double WGS84_LATITUDE_RESULT_FROM_EPSG31466 = 51.0701;
    private static final double WGS84_LONGITUDE_RESULT_FROM_EPSG31466 = 6.9942;

    private static final double LATITUDE_EPSG4326 = -26.06340;
    private static final double LONGITUDE_EPSG4326 = 27.69591;
    private static final double ALTITUDE = 1506.0;
    
    private static final double ALLOWED_DELTA = 0.01d;

    private Point crs84TransformationResult;
    private Point crs84Point;

    @Before
    public void setUp() throws Exception {
        CRSUtils strictEpsg = CRSUtils.createEpsgStrictAxisOrder();
        crs84Point = strictEpsg.createPoint(LONGITUDE_EPSG4326, LATITUDE_EPSG4326, 1506.0, "CRS:84");
        crs84TransformationResult = strictEpsg.createPoint(WGS84_LONGITUDE_RESULT_FROM_EPSG31466, WGS84_LATITUDE_RESULT_FROM_EPSG31466, "CRS:84");
    }

    @Test
    public void shouldHandleInvalidNcName() throws XmlException, IOException {
        String file = DESCRIBE_SENSOR_RESPONSE_FILE;
        XmlObject sml = loadSoapBodyFromXmlFileViaClassloader(file, "DescribeSensorResponse", getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getSimpleMetadata());
        
        // TODO check if invalid GmlId is replaced accordingly
    }
    
    private SOSMetadata getSimpleMetadata() {
        return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0"));
    }

    @Test 
    public void shouldParseStrict4326PositionToCrs84Position() throws XmlException, IOException, FactoryException, TransformException {
        XmlObject sml = loadXmlFileViaClassloader(SML_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getSimpleMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test 
    public void shouldParseLonLatOrdered4326PositionToCrs84Position() throws XmlException, IOException, FactoryException, TransformException {
        XmlObject sml = loadXmlFileViaClassloader(SML_POSITION_VECTOR_4326, getClass());
        DescribeSensorParser parser = createParserFromFile(sml, getForceXYOrderingMetadata());
        
        /*
         * We expect a lon/lat ordered coordinate as the inner CRS is CRS:84
         */
        Point actual = parser.buildUpSensorMetadataPosition();
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test
    public void shouldParseStrictEpsg4326FromPositionTypeWithNamedLatitudeLongitudeAxes() throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
        MyDescribeSensorParser parser = new MyDescribeSensorParser(getForceXYOrderingMetadata());
        Point actual = parser.testPointCreation(createStrictEpsg4326());
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
        
        actual = parser.testPointCreation(createStrictEpsg4326ShortNames());
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test
    public void shouldParseXYOdered4326FromPositionTypeWithNamedLatitudeLongitudeAxes() throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
        MyDescribeSensorParser parser = new MyDescribeSensorParser(getForceXYOrderingMetadata());
        Point actual = parser.testPointCreation(createStrictXYOrdered4326());
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
        
        actual = parser.testPointCreation(createStrictXYOrdered4326ShortNames());
        assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
    }
    
    @Test
    public void shouldParseXYOdered31466FromPositionTypeWithNamedLatitudeLongitudeAxes() throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
        MyDescribeSensorParser parser = new MyDescribeSensorParser(getSimpleMetadata());
        Point actual = parser.testPointCreation(createStrictEpsg31466());
        assertThat("X value (latitude) is incorrect.", actual.getX(), closeTo(crs84TransformationResult.getX(), ALLOWED_DELTA));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), closeTo(crs84TransformationResult.getY(), ALLOWED_DELTA));
        
        actual = parser.testPointCreation(createStrictEpsg31466ShortNames());
        assertThat("X value (latitude) is incorrect.", actual.getX(), closeTo(crs84TransformationResult.getX(), ALLOWED_DELTA));
        assertThat("Y value (longitude) is incorrect.", actual.getY(), closeTo(crs84TransformationResult.getY(), ALLOWED_DELTA));
    }

    
    private SOSMetadata getForceXYOrderingMetadata() {
        return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0").setForceXYAxisOrder(true));
    }

    private DescribeSensorParser createParserFromFile(XmlObject sensorML, SOSMetadata metadata) {
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
    
    private PositionType createStrictEpsg4326() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LATITUDE_EPSG4326, "latitude", coordinates); // #1
        setNamedCoordinate(LONGITUDE_EPSG4326, "longitude", coordinates); // #2
        setNamedCoordinate(ALTITUDE, "altitude", coordinates); // #3
        return position;
    }
    
    private PositionType createStrictXYOrdered4326() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LONGITUDE_EPSG4326, "longitude", coordinates); // #1
        setNamedCoordinate(LATITUDE_EPSG4326, "latitude", coordinates); // #2
        setNamedCoordinate(ALTITUDE, "altitude", coordinates); // #3
        return position;
    }
    
    private PositionType createStrictEpsg4326ShortNames() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LATITUDE_EPSG4326, "lat", coordinates); // #1
        setNamedCoordinate(LONGITUDE_EPSG4326, "lon", coordinates); // #2
        setNamedCoordinate(ALTITUDE, "alt", coordinates); // #3
        return position;
    }
    
    private PositionType createStrictXYOrdered4326ShortNames() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LONGITUDE_EPSG4326, "lon", coordinates); // #1
        setNamedCoordinate(LATITUDE_EPSG4326, "lat", coordinates); // #2
        setNamedCoordinate(ALTITUDE, "alt", coordinates); // #3
        return position;
    }
    
    private PositionType createStrictEpsg31466() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG:31466");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LATITUDE_EPSG31466, "latitude", coordinates); // #1
        setNamedCoordinate(LONGITUDE_EPSG31466, "longitude", coordinates); // #2
        return position;
    }
    
    private PositionType createStrictEpsg31466ShortNames() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG:31466");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LATITUDE_EPSG31466, "lat", coordinates); // #1
        setNamedCoordinate(LONGITUDE_EPSG31466, "lgt", coordinates); // #2
        return position;
    }
    
    private PositionType createStrictEpsg31466AbbreviatedAxes() {
        PositionDocument positionDoc = PositionDocument.Factory.newInstance();
        PositionType position = positionDoc.addNewPosition();
        position.setReferenceFrame("urn:ogc:def:crs:EPSG:31466");
        VectorPropertyType locationVector = position.addNewLocation();
        VectorType coordinates = locationVector.addNewVector();
        setNamedCoordinate(LATITUDE_EPSG4326, "x", coordinates); // #1
        setNamedCoordinate(LONGITUDE_EPSG4326, "y", coordinates); // #2
        setNamedCoordinate(ALTITUDE, "z", coordinates); // #3
        return position;
    }
    

    private void setNamedCoordinate(double value, String name, VectorType vector) {
        QuantityDocument quantityDoc = QuantityDocument.Factory.newInstance();
        Quantity quantity = quantityDoc.addNewQuantity();
        quantity.setValue(value);

        Coordinate coordiante = vector.addNewCoordinate();
        coordiante.setQuantity(quantity);
        coordiante.setName(name);
    }
    
    private class MyDescribeSensorParser extends DescribeSensorParser {
        
        public MyDescribeSensorParser(SOSMetadata metadata) throws XmlException, IOException, XMLHandlingException, FactoryException {
            this(null, metadata);
        }

        public MyDescribeSensorParser(InputStream inputStream, SOSMetadata metadata) throws XmlException,
                IOException,
                XMLHandlingException,
                FactoryException {
            super(inputStream, metadata);
            // TODO Auto-generated constructor stub
        }
        
        public Point testPointCreation(PositionType position) throws FactoryException, TransformException {
            return this.createPoint(position);
        }

        @Override
        protected void setDataStreamToParse(InputStream incomingResultAsStream) throws XmlException,
                IOException,
                XMLHandlingException {
            // DO NOTHING ATM
        }
        
    }
    
}
