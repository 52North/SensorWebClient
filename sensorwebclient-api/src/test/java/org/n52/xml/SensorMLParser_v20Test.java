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
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.parser.SensorMLParser_v20;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

import net.opengis.sensorml.x20.PhysicalSystemDocument;
import net.opengis.sensorml.x20.PositionUnionPropertyType;
import net.opengis.swe.x20.QuantityDocument;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

public class SensorMLParser_v20Test {
	
	private static final double WGS84_LATITUDE_RESULT_FROM_EPSG31466 = 51.0701;
	private static final double WGS84_LONGITUDE_RESULT_FROM_EPSG31466 = 6.9942;

	private static final double LATITUDE_EPSG4326 = -26.06340;
	private static final double LONGITUDE_EPSG4326 = 27.69591;

	private static final double LATITUDE_EPSG31466 = 5659928.636;
	private static final double LONGITUDE_EPSG31466 = 2569725.188;

	private static final double ALTITUDE = 1506.0;

	private static final double ALLOWED_DELTA = 0.01d;

	private Point crs84TransformationResult;
	private Point crs84Point;

	@Before
	public void setUp() throws Exception {
		CRSUtils strictEpsg = CRSUtils.createEpsgStrictAxisOrder();
		crs84Point = strictEpsg.createPoint(LONGITUDE_EPSG4326, LATITUDE_EPSG4326, 1506.0, "CRS:84");
		crs84TransformationResult = strictEpsg.createPoint(WGS84_LONGITUDE_RESULT_FROM_EPSG31466,
				WGS84_LATITUDE_RESULT_FROM_EPSG31466, "CRS:84");
	}

	@Test
	public void shouldParseStrictEpsg4326FromPositionTypeWithNamedLatitudeLongitudeAxes()
			throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
		MySensorMLParser_v20 parser = new MySensorMLParser_v20(getForceXYOrderingMetadata());
		Point actual = parser.testPointCreation(createStrictEpsg4326());
		assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
		assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));

		actual = parser.testPointCreation(createStrictEpsg4326ShortNames());
		assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
		assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
	}

	@Test
	public void shouldParseXYOdered4326FromPositionTypeWithNamedLatitudeLongitudeAxes()
			throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
		MySensorMLParser_v20 parser = new MySensorMLParser_v20(getForceXYOrderingMetadata());
		Point actual = parser.testPointCreation(createStrictXYOrdered4326());
		assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
		assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));

		actual = parser.testPointCreation(createStrictXYOrdered4326ShortNames());
		assertThat("X value (latitude) is incorrect.", actual.getX(), is(crs84Point.getX()));
		assertThat("Y value (longitude) is incorrect.", actual.getY(), is(crs84Point.getY()));
	}

	@Test
	public void shouldParseXYOdered31466FromPositionTypeWithNamedLatitudeLongitudeAxes()
			throws FactoryException, TransformException, XmlException, IOException, XMLHandlingException {
		MySensorMLParser_v20 parser = new MySensorMLParser_v20(getSimpleMetadata());
		Point actual = parser.testPointCreation(createStrictEpsg31466());
		assertThat("X value (latitude) is incorrect.", actual.getX(),
				closeTo(crs84TransformationResult.getX(), ALLOWED_DELTA));
		assertThat("Y value (longitude) is incorrect.", actual.getY(),
				closeTo(crs84TransformationResult.getY(), ALLOWED_DELTA));

		actual = parser.testPointCreation(createStrictEpsg31466ShortNames());
		assertThat("X value (latitude) is incorrect.", actual.getX(),
				closeTo(crs84TransformationResult.getX(), ALLOWED_DELTA));
		assertThat("Y value (longitude) is incorrect.", actual.getY(),
				closeTo(crs84TransformationResult.getY(), ALLOWED_DELTA));
	}

	private SOSMetadata getSimpleMetadata() {
		return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0"));
	}

	private PositionUnionPropertyType createStrictEpsg4326() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
		setNamedCoordinate(LATITUDE_EPSG4326, "latitude", coordinates); // #1
		setNamedCoordinate(LONGITUDE_EPSG4326, "longitude", coordinates); // #2
		setNamedCoordinate(ALTITUDE, "altitude", coordinates); // #3
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictXYOrdered4326() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
		setNamedCoordinate(LONGITUDE_EPSG4326, "longitude", coordinates); // #1
		setNamedCoordinate(LATITUDE_EPSG4326, "latitude", coordinates); // #2
		setNamedCoordinate(ALTITUDE, "altitude", coordinates); // #3
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictEpsg4326ShortNames() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
		setNamedCoordinate(LATITUDE_EPSG4326, "lat", coordinates); // #1
		setNamedCoordinate(LONGITUDE_EPSG4326, "lon", coordinates); // #2
		setNamedCoordinate(ALTITUDE, "alt", coordinates); // #3
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictXYOrdered4326ShortNames() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::4326");
		setNamedCoordinate(LONGITUDE_EPSG4326, "lon", coordinates); // #1
		setNamedCoordinate(LATITUDE_EPSG4326, "lat", coordinates); // #2
		setNamedCoordinate(ALTITUDE, "alt", coordinates); // #3
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictEpsg31466() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::31466");
		setNamedCoordinate(LATITUDE_EPSG31466, "latitude", coordinates); // #1
		setNamedCoordinate(LONGITUDE_EPSG31466, "longitude", coordinates); // #2
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictEpsg31466ShortNames() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::31466");
		setNamedCoordinate(LATITUDE_EPSG31466, "lat", coordinates); // #1
		setNamedCoordinate(LONGITUDE_EPSG31466, "lgt", coordinates); // #2
		return positionPropType;
	}

	private PositionUnionPropertyType createStrictEpsg31466AbbreviatedAxes() {
		PositionUnionPropertyType positionPropType = PositionUnionPropertyType.Factory.newInstance();
		VectorType coordinates = positionPropType.addNewVector();
		coordinates.setReferenceFrame("urn:ogc:def:crs:EPSG::31466");
		setNamedCoordinate(LATITUDE_EPSG4326, "x", coordinates); // #1
		setNamedCoordinate(LONGITUDE_EPSG4326, "y", coordinates); // #2
		setNamedCoordinate(ALTITUDE, "z", coordinates); // #3
		return positionPropType;
	}

	private SOSMetadata getForceXYOrderingMetadata() {
		return new SOSMetadata(new SOSMetadataBuilder().addServiceVersion("2.0.0").setForceXYAxisOrder(true));
	}

	private void setNamedCoordinate(double value, String name, VectorType vector) {
		QuantityDocument quantityDoc = QuantityDocument.Factory.newInstance();
		QuantityType quantity = quantityDoc.addNewQuantity();
		quantity.setValue(value);

		Coordinate coordiante = vector.addNewCoordinate();
		coordiante.setQuantity(quantity);
		coordiante.setName(name);
	}

	private class MySensorMLParser_v20 extends SensorMLParser_v20 {

		public MySensorMLParser_v20(SOSMetadata metadata) {
			this(null, metadata);
		}

		public MySensorMLParser_v20(PhysicalSystemDocument smlDoc, SOSMetadata metadata) {
			super(smlDoc, metadata);
		}

		public Point testPointCreation(PositionUnionPropertyType position) throws FactoryException, TransformException {
			return this.createPoint(position);
		}
	}

}
