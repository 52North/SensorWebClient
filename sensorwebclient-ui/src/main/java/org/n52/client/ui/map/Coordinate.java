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
package org.n52.client.ui.map;

import static org.n52.shared.Constants.EPSG_4326;

import org.gwtopenmaps.openlayers.client.LonLat;

import com.vividsolutions.jts.geom.Point;

public class Coordinate extends LonLat {

    public Coordinate(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Creates a {@link LonLat} coordinate ready to be rendered on a map.
     * 
     * @param mapProjection
     *        the projection of the map in which the coordinate will be rendered.
     */
    private Coordinate(double lon, double lat, String mapProjection) {
        this(lon, lat);
        if ( !EPSG_4326.equals(mapProjection)) {
            transform(EPSG_4326, mapProjection);
        }
    }

    /**
     * @param point
     *        the point to be mapped. Has to be in lon/lat order.
     * @param mapProjection
     *        the projection of the map in which the coordinate will be rendered.
     * @return a Coordinate instance transformed to given map projection.
     */
    public static Coordinate createProjectedCoordinate(Point point, String mapProjection) {
        return new Coordinate(point.getX(), point.getY(), mapProjection);
    }
}
