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
package org.n52.server.parser.utils;

public class ParsedPoint {
	
	private String lat;
	
	private String lon;

	private String srs;
	
	public static ParsedPoint createFromSpaceSeparatedLonLat(String lonlat, String srs) {
	    String[] coords = lonlat.split(" ");
	    if (coords.length != 2) {
            throw new IllegalArgumentException("Expected 2 coordinates after splitting.");
        }
	    return new ParsedPoint(coords[0], coords[1], srs);
	}
	
	public static ParsedPoint createFromSpaceSeparatedLatLon(String latLon, String srs) {
        String[] coords = latLon.split(" ");
        if (coords.length != 2) {
            throw new IllegalArgumentException("Expected 2 coordinates after splitting.");
        }
        return new ParsedPoint(coords[1], coords[0], srs);
    }
	
	public static ParsedPoint createFromCommaSeparatedLonLat(String lonlat, String srs) {
        String[] coords = lonlat.split(",");
        if (coords.length != 2) {
            throw new IllegalArgumentException("Expected 2 coordinates after splitting.");
        }
        if (coords[0] == null || coords[1] == null) {
            throw new IllegalArgumentException("Missing coordinate values in " + lonlat);
        }
        return new ParsedPoint(coords[0].trim(), coords[1].trim(), srs);
    }
	
	public static ParsedPoint createFromCommaSeparatedLatLon(String latLon, String srs) {
        String[] coords = latLon.split(",");
        if (coords.length != 2) {
            throw new IllegalArgumentException("Expected 2 coordinates after splitting.");
        }
        if (coords[0] == null || coords[1] == null) {
            throw new IllegalArgumentException("Missing coordinate values in " + latLon);
        }
        return new ParsedPoint(coords[1].trim(), coords[0].trim(), srs);
    }
	
	public ParsedPoint() {
	    
	}
	
	public ParsedPoint(String lon, String lat, String srs) {
		this.lon = lon;
		this.lat = lat;
		this.srs = srs;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}
	
	

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Point [ (");
        sb.append("lat: ").append(lat).append(", ");
        sb.append("lon: ").append(lon).append("), ");
        sb.append("srs: ").append(srs).append(" ]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (lat == null) ? 0 : lat.hashCode());
        result = prime * result + ( (lon == null) ? 0 : lon.hashCode());
        result = prime * result + ( (srs == null) ? 0 : srs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParsedPoint other = (ParsedPoint) obj;
        if (lat == null) {
            if (other.lat != null)
                return false;
        }
        else if ( !lat.equals(other.lat))
            return false;
        if (lon == null) {
            if (other.lon != null)
                return false;
        }
        else if ( !lon.equals(other.lon))
            return false;
        if (srs == null) {
            if (other.srs != null)
                return false;
        }
        else if ( !srs.equals(other.srs))
            return false;
        return true;
    }

}