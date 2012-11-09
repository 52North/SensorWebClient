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
import java.util.List;

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


    private String feature;
    
    private String phenomenon;

    private String procedure;

    private String offering;

    private String stationCategory;
    
    public Station() {
        id = IdGenerator.generate();
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
    
    public boolean hasAllEntries() {
        // XXX remove when Station refactoring is complete
        if (this.srs == null || this.feature == null || this.offering == null || this.phenomenon == null
                || this.procedure == null) {
            return false;
        }
        return true;
    }

    public boolean isProcedureEqual(String procedure) {
        // XXX remove when Station refactoring is complete
        return this.procedure.equals(procedure);
    }

    public boolean isPhenomenonEqual(String phenomenon) {
        // XXX remove when Station refactoring is complete
        return this.phenomenon.equals(phenomenon);
    }

    public boolean isFeatureEqual(String feature) {
        // XXX remove when Station refactoring is complete
        return this.feature.equals(feature);
    }

    public boolean isOfferingEqual(String offering) {
        // XXX remove when Station refactoring is complete
        return this.offering.equals(offering);
    }

    public String getFeature() {
        // XXX remove when Station refactoring is complete
        return this.feature;
    }

    public void setFeature(String feature) {
        // XXX remove when Station refactoring is complete
        this.feature = feature;
    }

    public String getPhenomenon() {
        // XXX remove when Station refactoring is complete
        return this.phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        // XXX remove when Station refactoring is complete
        this.phenomenon = phenomenon;
    }

    public String getProcedure() {
        // XXX remove when Station refactoring is complete
        return this.procedure;
    }

    public void setProcedure(String procedure) {
        // XXX remove when Station refactoring is complete
        this.procedure = procedure;
    }

    public String getOffering() {
        // XXX remove when Station refactoring is complete
        return this.offering;
    }

    public void setOffering(String offering) {
        // XXX remove when Station refactoring is complete
        this.offering = offering;
    }

    /**
     * A label to categorize stations. If not set, station's {@link #phenomenon} is returned. Can be used to
     * filter a set of stations according a common category.
     * 
     * @return a label to categorize stations on which filtering can take place.
     */
    public String getStationCategory() {
        return stationCategory == null ? phenomenon : stationCategory;
    }

    /**
     * @param stationCategory
     *        a filter to categorize stations.
     */
    public void setStationCategory(String stationCategory) {
        this.stationCategory = stationCategory;
    }

    public Station clone() {
        // XXX cleanup when Station refactoring is complete
        Station station = new Station();
        station.setLocation(location, srs);
        station.setStationCategory(stationCategory);
        station.setPhenomenon(phenomenon);
        station.setProcedure(procedure);
        station.setOffering(offering);
        station.setFeature(feature);
        return station;
    }

    @Override
    public String toString() {
        // XXX cleanup when Station refactoring is complete
        StringBuffer sb = new StringBuffer();
        // TODO wait for fix: http://code.google.com/p/google-web-toolkit/issues/detail?id=3404
        // sb.append(getClass().getSimpleName()).append(" [ ");
        sb.append("Station: [ ").append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("Feature: ").append(feature).append("\n");
        sb.append("Offering: ").append(offering).append("\n");
        sb.append("Procedure: ").append(procedure).append("\n");
        sb.append("Phenomenon: ").append(phenomenon).append(" ]");
        return sb.toString();
    }

    private static class IdGenerator {
        private static int id = 0;
    
        public static String generate() {
            return String.valueOf(++id);
        }
    }

}
