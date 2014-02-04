/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.api.v0.io;

import static java.lang.Double.parseDouble;
import static java.lang.Math.toRadians;
import static org.n52.io.crs.CRSUtils.createEpsgForcedXYAxisOrder;
import static org.n52.io.crs.WGS84Util.EPSG_4326;
import static org.n52.io.crs.WGS84Util.getLatitudeDelta;
import static org.n52.io.crs.WGS84Util.getLongitudeDelta;
import static org.n52.io.crs.WGS84Util.normalizeLatitude;
import static org.n52.io.crs.WGS84Util.normalizeLongitude;
import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import org.n52.io.crs.BoundingBox;
import org.n52.io.crs.CRSUtils;
import org.n52.io.geojson.GeojsonPoint;
import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Point;

/**
 * Represents the surrounding area based on a center and a radius. All coordinate calculations are based on a
 * EPSG:4326, lon-lat ordered reference frame.
 */
public class Vicinity {
    
    private Point center;

    private double radius;

    Vicinity() {
        // for serialization
    }

    /**
     * @param center
     *        the lon-lat ordered center.
     * @param radius
     *        the distance around the center
     */
    public Vicinity(Double[] center, String radius) {
        try {
            this.radius = parseDouble(radius);
            this.center = createCenter(createWithCoordinates(center));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse radius.");
        }
        catch (FactoryException e) {
            throw new IllegalArgumentException("Could not parse center.");
        }
    }

    /**
     * @return a bounding rectangle.
     */
    public BoundingBox calculateBounds() {
        CRSUtils inputReference = createEpsgForcedXYAxisOrder();
        double latInRad = toRadians(center.getY());
        double llEasting = normalizeLongitude(center.getX() - getLongitudeDelta(latInRad, radius));
        double llNorthing = normalizeLatitude(center.getY() - getLatitudeDelta(radius));
        double urEasting = normalizeLongitude(center.getX() + getLongitudeDelta(latInRad, radius));
        double urNorthing = normalizeLatitude(center.getY() + getLatitudeDelta(radius));
        Point ll = inputReference.createPoint(llEasting, llNorthing, "CRS:84");
        Point ur = inputReference.createPoint(urEasting, urNorthing, "CRS:84");
        return new BoundingBox(ll, ur, "CRS:84");
    }

    /**
     * @param coordinates the center point to set.
     * @throws FactoryException
     * @throws IllegalArgumentException
     *         if coordinates are <code>null</code> or do not contain a two dimensional point.
     */
    public void setCenter(Double[] coordinates) throws FactoryException {
        center = createCenter(createWithCoordinates(coordinates));
    }
    

    /**
     * @param center
     *        the center point as GeoJSON point.
     * @return the lon-lat ordered WGS84 center point.
     * @throws FactoryException
     *         if creating coordinates fails.
     */
    private Point createCenter(GeojsonPoint center) throws FactoryException {
        CRSUtils inputReference = createEpsgForcedXYAxisOrder();
        Double easting = center.getCoordinates()[0];
        Double northing = center.getCoordinates()[1];
        return inputReference.createPoint(easting, northing, EPSG_4326);
    }


    /**
     * @param radius
     *        the vicinity's radius.
     * @throws NumberFormatException
     *         if radius could not be parsed to a double value.
     */
    public void setRadius(String radius) {
        this.radius = parseDouble(radius);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [ ");
        sb.append("Center: ").append(center).append(", ");
        sb.append("Radius: ").append(radius).append(" km");
        return sb.append(" ]").toString();
    }

}
