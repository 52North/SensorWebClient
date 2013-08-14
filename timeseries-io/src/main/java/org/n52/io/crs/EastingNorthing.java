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

package org.n52.io.crs;

import static org.n52.io.geojson.GeojsonCrs.createCrs84Named;

import java.io.Serializable;

import org.n52.io.geojson.GeojsonCrs;
import org.n52.io.geojson.GeojsonPoint;

public class EastingNorthing implements Serializable {

    private static final long serialVersionUID = 4080241800833286545L;

    private double easting;
    private double northing;
    private double altitude;
    private GeojsonCrs crs;

    @SuppressWarnings("unused")
    private EastingNorthing() {
        // client requires class to be default instantiable
    }
    
    public EastingNorthing(GeojsonPoint point) {
        this(point.getCrs(), point.getCoordinates());
    }

    public EastingNorthing(Double easting, Double northing, String srs) {
        this(GeojsonCrs.createNamedCRS(srs), new Double[] {easting, northing});
    }
    
    public EastingNorthing(Double[] coordinates) {
        this(GeojsonCrs.createCrs84Named(), coordinates);
    }

    public EastingNorthing(GeojsonCrs crs, Double[] coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Coordinates must not null.");
        }
        if (coordinates.length != 2 && coordinates.length != 3) {
            throw new IllegalArgumentException("Coordinates must be either 2- or 3-dimensional.");
        }
        this.crs = crs == null ? createCrs84Named() : crs;
        easting = coordinates[0];
        northing = coordinates[1];
        if (coordinates.length == 3) {
            altitude = coordinates[2];
        }
    }
    
    public EastingNorthing(double easting, double northing, GeojsonCrs crs) {
        this.easting = easting;
        this.northing = northing;
        this.crs = crs;
    }

    public double getEasting() {
        return easting;
    }

    public double getNorthing() {
        return northing;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    
    public String getCrsDefinition() {
        return crs.getName();
    }
    
    public GeojsonCrs getCrs() {
        return crs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(altitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(easting);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(northing);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof EastingNorthing)) {
            return false;
        }
        EastingNorthing other = (EastingNorthing) obj;
        if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude)) {
            return false;
        }
        if (Double.doubleToLongBits(easting) != Double.doubleToLongBits(other.easting)) {
            return false;
        }
        if (Double.doubleToLongBits(northing) != Double.doubleToLongBits(other.northing)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [ ");
        sb.append("Easting: ").append(easting).append(", ");
        sb.append("Northing: ").append(northing).append(", ");
        sb.append("Altitude: ").append(altitude).append(" ]");
        return sb.toString();
    }
}
