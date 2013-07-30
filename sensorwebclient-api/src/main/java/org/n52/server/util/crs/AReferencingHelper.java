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

package org.n52.server.util.crs;

import java.util.Collection;
import java.util.List;

import org.geotools.factory.Hints;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Station;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public abstract class AReferencingHelper {

    protected CRSAuthorityFactory crsFactory;

    /**
     * Creates an {@link AReferencingHelper} which offers assistance when doing spatial opererations. Strict
     * means that all CRS defined with lat/lon axis ordering will be handled as defined. <br>
     * <br>
     * If {@link ConfigurationContext#IS_DEV_MODE} is set to <code>true</code> a
     * {@link ReferencingMockupHelper} is created.
     * 
     * @return creates a reference helper which (strictly) handles referencing operations.
     */
    public static AReferencingHelper createEpsgStrictAxisOrder() {
        /*
         * Setting FORCE_LONGITUDE_FIRST_AXIS_ORDER to FALSE seems to be unnecessary as this is geotools
         * default value for this. It becomes necessary, when property org.geotools.referencing.forceXY was
         * set as System property which silently different axis order than expected.
         * 
         * FORCE_LONGITUDE_FIRST_AXIS_ORDER parameter is preferred to org.geotools.referencing.forceXY so we
         * have to set it explicitly to find the correct CRS factory.
         */
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        return createEpsgReferenceHelper(hints);
    }

    /**
     * Creates an {@link AReferencingHelper} which offers assistance when doing spatial opererations. Forcing
     * XY means that CRS axis ordering is considered lon/lat ordering, even if defined lat/lon. <br>
     * <br>
     * If {@link ConfigurationContext#IS_DEV_MODE} is set to <code>true</code> a
     * {@link ReferencingMockupHelper} is created.
     * 
     * @return creates a reference helper which (strictly) handles referencing operations.
     */
    public static AReferencingHelper createEpsgForcedXYAxisOrder() {
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        return createEpsgReferenceHelper(hints);
    }

    /**
     * Creates an {@link AReferencingHelper} which offers assistance when doing spatial opererations.
     * 
     * @param hints
     *        Some Geotools {@link Hints} which set behavior and special considerations regarding to the
     *        spatial operations.
     */
    public static AReferencingHelper createEpsgReferenceHelper(Hints hints) {
        return createReferencingHelper(ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints));
    }

    protected AReferencingHelper(CRSAuthorityFactory crsFactory) {
        this.crsFactory = crsFactory;
    }

    /**
     * Creates an {@link AReferencingHelper} which offeres assistance when doing spatial operations. <br>
     * <br>
     * If {@link ConfigurationContext#IS_DEV_MODE} is set to <code>true</code> a
     * {@link ReferencingMockupHelper} is created.
     * 
     * @return a referencing facade implementation depending on {@link ConfigurationContext#IS_DEV_MODE}
     *         parameter
     */
    private static AReferencingHelper createReferencingHelper(CRSAuthorityFactory crsFactory) {
        if (ConfigurationContext.IS_DEV_MODE) {
            return new ReferencingMockupHelper(crsFactory);
        }
        return new ReferencingHelper(crsFactory);
    }

    public abstract List<Station> getContainingStations(BoundingBox bbox, Collection<Station> stations) throws FactoryException,
            TransformException;

    public abstract boolean isStationContainedByBBox(BoundingBox bbox, Station station) throws FactoryException,
            TransformException;

    /**
     * Transforms a given point from a given reference to WGS84 (EPSG:4328).
     * 
     * @param point
     *        the point to transform.
     * @param refFrame
     *        the reference the given point is in.
     * @return a transformed point.
     * @throws FactoryException
     *         if the creation of {@link CoordinateReferenceSystem} fails or no appropriate
     *         {@link MathTransform} could be created.
     * @throws TransformException
     *         if transformation fails for any other reason.
     */
    public Point transformToWgs84(Point point, String refFrame) throws FactoryException, TransformException {
        return transform(point, refFrame, Constants.EPSG_4326);
    }

    /**
     * Transforms a given point from a given reference to a destinated reference.
     * 
     * @param point
     *        the point to transform.
     * @param refFrame
     *        the reference the given point is in.
     * @param destFrame
     *        the reference frame the point shall be transformed to.
     * @return a transformed point.
     * @throws FactoryException
     *         if the creation of {@link CoordinateReferenceSystem} fails or no appropriate
     *         {@link MathTransform} could be created.
     * @throws TransformException
     *         if transformation fails for any other reason.
     */
    public Point transform(Point point, String refFrame, String destFrame) throws FactoryException, TransformException {
        CoordinateReferenceSystem srs = crsFactory.createCoordinateReferenceSystem(refFrame);
        CoordinateReferenceSystem dest = crsFactory.createCoordinateReferenceSystem(destFrame);
        // CoordinateReferenceSystem srs = CRS.decode(refFrame);
        // CoordinateReferenceSystem dest = CRS.decode(destFrame);
        return transform(point, srs, dest);
    }

    public Point transform(Point point, CoordinateReferenceSystem srs, CoordinateReferenceSystem dest) throws FactoryException,
            TransformException {
        return (Point) JTS.transform(point, CRS.findMathTransform(srs, dest));
    }

    public GeometryFactory createGeometryFactory(String srs) {
        return createGeometryFactory(getSrsIdFrom(srs));
    }
    
    public GeometryFactory createGeometryFactory(int srsId) {
        PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING);
        return new GeometryFactory(pm, srsId);
    }
    

    /**
     * Creates a coordinate with respect to axis ordering of the given srs parameter.
     * 
     * @param srs
     *        an authoritive spatial reference system code the coordinate is in.
     * @param easting
     *        the coordinate's easting value.
     * @param northing
     *        the coordinate's northing value.
     * @return a coordinate respecting axis ordering of the given spatial reference system
     * @throws FactoryException
     *         if no {@link CRS} factory could be found to create a coordinate reference system corresponding
     *         to the given srs parameter.
     */
    public Coordinate createCoordinate(String srs, Double easting, Double northing) throws FactoryException {
        return createCoordinate(srs, easting, northing, null);
    }


    /**
     * Creates a coordinate with respect to axis ordering of the given srs parameter.
     * 
     * @param srs
     *        an authoritive spatial reference system code the coordinate is in.
     * @param easting
     *        the coordinate's easting value.
     * @param northing
     *        the coordinate's northing value.
     * @param altitude
     *        the height or <code>null</code> if coordinate is 2D.
     * @return a coordinate respecting axis ordering of the given spatial reference system
     * @throws FactoryException
     *         if no {@link CRS} factory could be found to create a coordinate reference system corresponding
     *         to the given srs parameter.
     */
    public Coordinate createCoordinate(String srs, Double easting, Double northing, Double altitude) throws FactoryException {
        CoordinateReferenceSystem sourceCrs = crsFactory.createCoordinateReferenceSystem(srs);
        // CoordinateReferenceSystem sourceCrs = CRS.decode(srs);
        return createCoordinate(sourceCrs, easting, northing, altitude);
    }

    /**
     * Creates a coordinate with respect to axis ordering of the given srs parameter.
     * 
     * @param srs
     *        the spatial reference system code the coordinate is in.
     * @param easting
     *        the coordinate's easting value.
     * @param northing
     *        the coordinate's northing value.
     * @param altitude
     *        the height or <code>null</code> if coordinate is 2D.
     * @return a coordinate respecting axis ordering of the given spatial reference system
     * @throws FactoryException
     *         if no {@link CRS} factory could be found to create a coordinate reference system corresponding
     *         to the given srs parameter.
     * @throws NoSuchAuthorityCodeException
     *         if no {@link CRS} could be decoded from the given srs parameter.
     */
    public Coordinate createCoordinate(CoordinateReferenceSystem srs, Double easting, Double northing, Double altitude) {
        Coordinate coordinate = null;
        CoordinateSystemAxis axis = srs.getCoordinateSystem().getAxis(0);
        if (axis.getDirection().equals(AxisDirection.NORTH)) {
            // lat,lng ordering
            if (altitude == null) {
                coordinate = new Coordinate(northing, easting);
            }
            else {
                coordinate = new Coordinate(northing, easting, altitude);
            }
        }
        else {
            // lng,lat ordering
            if (altitude == null) {
                coordinate = new Coordinate(easting, northing);
            }
            else {
                coordinate = new Coordinate(easting, northing, altitude);
            }
        }
        return coordinate;
    }

    /**
     * @param srs
     *        the SRS definition string, either as URL ('<code>/</code>'-separated) or as URN ('<code>:</code>
     *        '-separated).
     * @return SRS string in the form of for example 'EPSG:4326' or 'EPSG:31466'.
     */
    public String extractSRSCode(String srs) {
        if (isSrsUrlDefinition(srs)) {
            return "EPSG:" + srs.substring(srs.lastIndexOf("/") + 1);
        }
        else {
            String[] srsParts = srs.split(":");
            return "EPSG:" + srsParts[srsParts.length - 1];
        }
    }

    private boolean isSrsUrlDefinition(String srs) {
        return srs.startsWith("http");
    }

    /**
     * Extracts the SRS number of the incoming SRS definition string. This can be either an HTTP URL (like
     * <code>http://www.opengis.net/def/crs/EPSG/0/4326</code>) or a URN (like
     * <code>urn:ogc:def:crs:EPSG::31466</code>).
     * 
     * @param srs
     *        the SRS definition string, either as URL ('<code>/</code>'-separated) or as URN ('<code>:</code>
     *        '-separated).
     * @return the SRS number, e.g. 4326
     */
    public int getSrsIdFrom(String srs) {
        return getSrsIdFromEPSG(extractSRSCode(srs));
    }

    public int getSrsIdFromEPSG(String srs) {
        String[] epsgParts = srs.split(":");
        if (epsgParts.length > 1) {
            return Integer.parseInt(epsgParts[epsgParts.length - 1]);
        }
        return Integer.parseInt(srs);
    }

}
