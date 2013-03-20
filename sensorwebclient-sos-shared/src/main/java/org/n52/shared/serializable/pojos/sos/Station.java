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

package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.n52.shared.serializable.pojos.EastingNorthing;

/**
 * A {@link Station} represents a location where timeseries data can be retrieved at. The most important
 * 
 * (TODO put more infos here)<br>
 * <br>
 * A Station belongs to a category (by default to its {@link #phenomenon} field. It can be used to filter a common set 
 * of stations according to a predefined category.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = 5016550440955260625L;

    private String id;

    private String srs; // TODO srs and location into one object!
    private EastingNorthing location;
    
    private ArrayList<ParameterConstellation> parameterConstellations; 
    
    public Station() {
    	// zero-argument contructor for GWT
    }

    public Station(String stationId) {
    	this.id = stationId;
        parameterConstellations = new ArrayList<ParameterConstellation>();
    }

    public String getId() {
        return id;
    }

    public void setLocation(EastingNorthing location, String srs) {
        // TODO should be made private as we never should change the equals attributes when having objects in a HashSet
        this.location = location;
        this.srs = srs;
    }
    
    public EastingNorthing getLocation() {
        return location;
    }

    public double getLat() {
        return location.getNorthing();
    }

    public double getLon() {
        return location.getEasting();
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public String getSrs() {
        return srs;
    }

    public void addParameterConstellation(ParameterConstellation parameterConstellation) {
        parameterConstellations.add(parameterConstellation);
    }
    
    public ArrayList<ParameterConstellation> getParameterConstellations() {
        return parameterConstellations;
    }
    
    public boolean contains(ParameterConstellation parameterConstellation) {
        return parameterConstellations.contains(parameterConstellation);
    }
    
    public void setParameterConstellations(ArrayList<ParameterConstellation> parameterConstellations) {
        this.parameterConstellations = parameterConstellations;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        // TODO wait for fix: http://code.google.com/p/google-web-toolkit/issues/detail?id=3404
        // sb.append(getClass().getSimpleName()).append(" [ ");
        sb.append("Station: [ ").append("\n");
        sb.append("\tLocation: ").append(location).append("\n");
        sb.append("\tParameterConstellation-Count: ").append(parameterConstellations.size()).append(" ]\n");
        return sb.toString();
    }

	public boolean hasParameterConstellation(String offeringId, String featureId,
			String procedureId, String phenomenonId) {
		if(id.equals(featureId)) {
			for (ParameterConstellation paramConst : parameterConstellations) {
				if (paramConst.hasOffering(offeringId) && paramConst.hasFoi(featureId) && paramConst.hasProcedure(procedureId) && paramConst.hasPhenomenon(phenomenonId)) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<String> getStationCategories() {
		Set<String> categories = new HashSet<String>();
		for (ParameterConstellation paramConst : parameterConstellations) {
			categories.add(paramConst.getCategory());
		}
		return categories;
	}

	public boolean hasStationCategory(String filterCategory) {
		for (ParameterConstellation paramConst : parameterConstellations) {
			if (paramConst.getCategory().equals(filterCategory)){
				return true;
			}
		}
		return false;
	}

	public ParameterConstellation getParameterConstellationByCategory(
			String category) {
		for (ParameterConstellation paramConst : parameterConstellations) {
			if (paramConst.getCategory().equals(category)){
				return paramConst;
			}
		}
		return null;
	}
}
