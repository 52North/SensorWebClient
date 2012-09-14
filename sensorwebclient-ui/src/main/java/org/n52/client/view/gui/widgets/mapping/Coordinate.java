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

package org.n52.client.view.gui.widgets.mapping;

import org.gwtopenmaps.openlayers.client.LonLat;

/**
 * Represents a lon/lat Coordinate.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class Coordinate extends LonLat {

    /**
     * Instantiates a new lon/lat coordinate.
     * 
     * @param lng
     *        the lng
     * @param lat
     *        the lat
     * @param mapProjection
     *        the mapProjection
     * @param srs
     *        the coord projection
     */
    public Coordinate(double lng, double lat, String mapProjection, String srs) {
        super(lng, lat);
        if (srs == null) {
			srs = mapProjection; // FIXME aussumption ok?
		}
        if (!mapProjection.equals(srs)) {
            transform(srs, mapProjection);
        }
    }

    // /** XXX is this constructor needed?
    // * Instantiates a new coordinates.
    // *
    // * @param lon
    // * the lon
    // * @param lat
    // * the lat
    // */
    // public Coordinate(double lon, double lat) {
    // super(lon, lat);
    // }

}
