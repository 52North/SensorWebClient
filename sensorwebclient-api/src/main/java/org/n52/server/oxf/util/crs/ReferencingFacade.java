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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.referencing.CRS;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Station;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class ReferencingFacade extends AReferencingFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencingFacade.class);
    
    protected ReferencingFacade() {
        // use static AReferencingFacade.createAReferencingFacade factory method
    }

    @Override
    public List<Station> getContainingStations(BoundingBox bbox, Collection<Station> stations) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        LOGGER.trace(String.format("BBox coordinates: %s", bbox));
        List<Station> stationsToKeep = new ArrayList<Station>();
        for (Station station : stations) {
            if (isStationContainedByBBox(bbox, station)) {
                stationsToKeep.add(station);
            }
        }
        return stationsToKeep;
    }
    
    public boolean isStationContainedByBBox(BoundingBox bbox, Station station) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        String sourceSrs = station.getSrs();
        String targetSrs = bbox.getSrs();
        if (sourceSrs != null) {
            CoordinateReferenceSystem sourceCrs = CRS.decode(sourceSrs);
            CoordinateReferenceSystem targetCrs = CRS.decode(targetSrs);
            PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING);
            GeometryFactory geometryFactory = new GeometryFactory(pm, getSrsIdFromEPSG(sourceSrs));
            Coordinate coordinate = createCoordinate(sourceCrs, station.getLon(), station.getLat(), null);
            Point point = geometryFactory.createPoint(coordinate);
            point = transform(point, sourceCrs, targetCrs);
            if (isAxesSwitched(sourceCrs, targetCrs)) {
                return bbox.contains(point.getX(), point.getY());
            } else {
                return bbox.contains(point.getY(), point.getX());
            }
        }
        return false;
    }

    /**
     * @param first
     *        the first CRS.
     * @param second
     *        the second CRS.
     * @return <code>true</code> if the first axes of both given CRS do not point in the same direction,
     *         <code>false</code> otherwise.
     */
    private boolean isAxesSwitched(CoordinateReferenceSystem first, CoordinateReferenceSystem second) {
        AxisDirection sourceFirstAxis = first.getCoordinateSystem().getAxis(0).getDirection();
        AxisDirection targetFirstAxis = second.getCoordinateSystem().getAxis(0).getDirection();
        return sourceFirstAxis.equals(AxisDirection.NORTH) && !targetFirstAxis.equals(AxisDirection.NORTH)
                || !sourceFirstAxis.equals(AxisDirection.NORTH) && targetFirstAxis.equals(AxisDirection.NORTH);

    }
}
