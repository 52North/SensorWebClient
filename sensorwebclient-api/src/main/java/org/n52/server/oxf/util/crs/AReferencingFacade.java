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
package org.n52.server.oxf.util.crs;

import java.util.Collection;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Station;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public abstract class AReferencingFacade {

    protected AReferencingFacade() {
        // use static Factory method
    }
    
    /**
     * @return a referencing facade implementation depending on {@link ConfigurationContext#IS_DEV_MODE} parameter
     */
    public static AReferencingFacade createReferenceFacade() {
        if (ConfigurationContext.IS_DEV_MODE) {
            return new ReferencingMockupFacade();
        } else {
            return new ReferencingFacade();
        }
    }
    
    public abstract List<Station> getContainingStations(BoundingBox bbox, Collection<Station> stations) throws NoSuchAuthorityCodeException, FactoryException, TransformException;

    public abstract boolean isStationContainedByBBox(BoundingBox bbox, Station station) throws NoSuchAuthorityCodeException, FactoryException, TransformException;
    
    public Point transform(Point point, String refFrame, String destFrame) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        CoordinateReferenceSystem srs = CRS.decode(refFrame);
        CoordinateReferenceSystem dest = CRS.decode(destFrame);
        return transform(point, srs, dest);
    }
    
    public Point transform(Point point, CoordinateReferenceSystem srs, CoordinateReferenceSystem dest) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        return (Point) JTS.transform(point, CRS.findMathTransform(srs, dest));
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
     * @throws NoSuchAuthorityCodeException
     *         if no {@link CRS} could be decoded from the given srs parameter.
     */
    public Coordinate createCoordinate(String srs, Double easting, Double northing, Double altitude) throws NoSuchAuthorityCodeException, FactoryException {
        CoordinateReferenceSystem sourceCrs = CRS.decode(srs);
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
            } else {
                coordinate = new Coordinate(northing, easting, altitude);
            }
        } else  {
            // lng,lat ordering
            if (altitude == null) {
                coordinate = new Coordinate(easting, northing);
            } else {
                coordinate = new Coordinate(easting,  northing, altitude);
            }
        }
        return coordinate;
    }
    
    public String extractSRSCode(String srs) {
        if (isSrsUrlDefinition(srs)) {
            return "EPSG:" + srs.substring(srs.lastIndexOf("/") + 1);
        } else {
            String[] srsParts = srs.split(":");
            return "EPSG:" + srsParts[srsParts.length - 1];
        }
    }

    private boolean isSrsUrlDefinition(String srs) {
        return srs.startsWith("http");
    }
    
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
