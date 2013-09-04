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

import org.n52.shared.serializable.pojos.EastingNorthing;

/**
 * A {@link Station} represents a location where timeseries data is observed.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = 5016550440955260625L;

    private String id;

    private EastingNorthing location;

    private ArrayList<SosTimeseries> observingTimeseries;

    private Type type = Type.DEFAULT;
    
    public enum Type {
    	DEFAULT, SURFACE, GROUND
    }
    
    private static final String STATION_NAME_SUFFIX_GROUND = "_sohle";
    private static final String STATION_NAME_SUFFIX_SURFACE = "_oberflaeche";

    Station() {
        // for serialization
    }

    public Station(String stationId) {
        this.id = stationId;
        observingTimeseries = new ArrayList<SosTimeseries>();
    }

    public String getId() {
        return id;
    }
    
    public String getIdWithoutType(){
    	return getId()
    			.replaceAll(STATION_NAME_SUFFIX_SURFACE + "$", "")
    			.replaceAll(STATION_NAME_SUFFIX_GROUND + "$", "");
    }

    public void setLocation(EastingNorthing location) {
        this.location = location;
    }

    public EastingNorthing getLocation() {
        return location;
    }

    public void addTimeseries(SosTimeseries timeseries) {
        observingTimeseries.add(timeseries);
    }

    public ArrayList<SosTimeseries> getObservedTimeseries() {
        return observingTimeseries;
    }

    public boolean contains(SosTimeseries timeseries) {
        return observingTimeseries.contains(timeseries);
    }
    
    public boolean contains(String timeseriesId) {
        return getTimeseriesById(timeseriesId) != null;
    }
    
    public SosTimeseries getTimeseriesById(String timeseriesId) {
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getTimeseriesId().equals(timeseriesId)) {
                return timeseries;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Station: [ ").append("\n");
        sb.append("\tId: ").append(id).append("\n");
        sb.append("\tLocation: ").append(location).append("\n");
        sb.append("\t#Timeseries: ").append(observingTimeseries.size()).append(" ]\n");
        return sb.toString();
    }

    public boolean hasStationCategory(String filterCategory) {
    	String[] filterSplitted = filterCategory.split("\\|",2);
    	String category = filterSplitted[0];
		try{
	    	Type type = Type.valueOf(filterSplitted[1]);
			if( getType() != type){
				return false;
			}
		}catch(Exception e){
			// no type given. checking without. no problem.
		}
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getCategory().equals(category)) {
            	return true;
            }
        }
        return false;
    }

    public SosTimeseries getTimeseriesByCategory(String category) {
        for (SosTimeseries paramConst : observingTimeseries) {
            if (paramConst.getCategory().equals(category)) {
                return paramConst;
            }
        }
        return null;
    }

    public boolean hasAtLeastOneParameterConstellation() {
        return observingTimeseries.size() > 0 ? true : false;
    }

    // @Override // fails during gwt compile
    public Station clone() {
        Station station = new Station(id);
        station.setLocation(location);
        station.setObservingTimeseries(new ArrayList<SosTimeseries>(observingTimeseries));
        return station;
    }
    
    private void setObservingTimeseries(ArrayList<SosTimeseries> observingTimeseries) {
        this.observingTimeseries = observingTimeseries;
    }

	public Type getType() {
		return this.type;
	}
	
	public void setType( Type type ){
		this.type = type;
		markTimeseries();
	}
	
	/**
	 * Returns the type based on the id of itself.
	 * Checks for suffixes in the id.
	 * @return
	 */
	public Type getTypeById() {
		if (this.getId().endsWith(STATION_NAME_SUFFIX_GROUND)) {
			return Type.GROUND;
		} else if (this.getId().endsWith(STATION_NAME_SUFFIX_SURFACE)) {
			return Type.SURFACE;
		} else {
			return Type.DEFAULT;
		}
	}
	
	/**
	 * Marks all SosTimeseries with the current type.
	 */
	private void markTimeseries(){
		for(SosTimeseries st : this.observingTimeseries ){
			st.setType(this.getType());
		}
	}

	public static String decodeSpecialCharacters( String str){
        String retStr = str;
        retStr = retStr.replaceAll("_", " ");
        retStr = retStr.replaceAll("kuerzest", "kürzest");
        retStr = retStr.replaceAll("laengst", "längst");
        retStr = retStr.replaceAll("Leitfaehigkeit", "Leitfähigkeit");
        retStr = retStr.replaceAll("Saettigung", "Sättigung");
        retStr = retStr.replaceAll("Stroemung", "Strömung");
        retStr = retStr.replaceAll("hoechst", "höchst");
        retStr = retStr.replaceAll("Trueb", "Trüb");
//        retStr = retStr.replaceAll("", "");

        return retStr;
    }
}
