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

/**
 * Represents a valid parameter constellation to retrieve timeseries data from an SOS.
 * 
 * @see Station which contains multiple parameter constellations valid for a specific location.
 */
public class ParameterConstellation implements Serializable {

    private static final long serialVersionUID = 4336908002034438766L;

    private String procedure;

    private String phenomenon;

    private String feature;

    private String offering;

    private String category;

    public boolean isValid() {
        if (this.offering == null || this.phenomenon == null
                || this.procedure == null || feature == null) {
            return false;
        }
        return true;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public String getFeatureOfInterest() {
        return feature;
    }

    public void setFeatureOfInterest(String featureOfInterest) {
        this.feature = featureOfInterest;
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    /**
     * A label to categorize parameter constellation. If not set, the {@link #phenomenon} of the parameter
     * constellation is returned. Can be used to filter a set of stations according a common category.
     * 
     * @return a label to categorize stations on which filtering can take place.
     */
    public String getCategory() {
        return category == null ? phenomenon : category;
    }

    /**
     * @param category
     *        a filter to categorize stations.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ( (feature == null) ? 0 : feature
                        .hashCode());
        result = prime * result
                + ( (offering == null) ? 0 : offering.hashCode());
        result = prime * result
                + ( (phenomenon == null) ? 0 : phenomenon.hashCode());
        result = prime * result
                + ( (procedure == null) ? 0 : procedure.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if ( ! (obj instanceof ParameterConstellation))
            return false;
        ParameterConstellation other = (ParameterConstellation) obj;
        if (feature == null) {
            if (other.feature != null)
                return false;
        }
        else if ( !feature.equals(other.feature))
            return false;
        if (offering == null) {
            if (other.offering != null)
                return false;
        }
        else if ( !offering.equals(other.offering))
            return false;
        if (phenomenon == null) {
            if (other.phenomenon != null)
                return false;
        }
        else if ( !phenomenon.equals(other.phenomenon))
            return false;
        if (procedure == null) {
            if (other.procedure != null)
                return false;
        }
        else if ( !procedure.equals(other.procedure))
            return false;
        return true;
    }

    public boolean hasProcedure(String procedure) {
        return this.procedure != null && this.procedure.equals(procedure);
    }

    public boolean hasPhenomenon(String phenomenon) {
        return this.phenomenon != null && this.phenomenon.equals(phenomenon);
    }

    public boolean hasFoi(String foi) {
        return this.feature != null
                && this.feature.equals(foi);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ParameterConstellation: [ ").append("\n");
        sb.append("\tOffering: ").append(offering).append("\n");
        sb.append("\tFeature: ").append(feature).append("\n");
        sb.append("\tProcedure: ").append(procedure).append("\n");
        sb.append("\tPhenomenon: ").append(phenomenon).append("\n");
        sb.append("\tCategory: ").append(category).append("]\n");
        return sb.toString();
    }

    public boolean hasOffering(String offeringId) {
        return offering != null && offering.equals(offeringId);
    }

    public ParameterConstellation clone() {
        ParameterConstellation paramConst = new ParameterConstellation();
        paramConst.setFeatureOfInterest(feature);
        paramConst.setPhenomenon(phenomenon);
        paramConst.setProcedure(procedure);
        paramConst.setOffering(offering);
        paramConst.setCategory(category);
        return paramConst;
    }

    public boolean matchFilter(String offering, String phenomenon, String procedure, String feature) {
        return matchesOffering(offering) && matchesPhenomenon(phenomenon) && matchesProcedure(procedure)
                && matchesFeature(feature);
    }

    /**
     * Checks if given filter and currently set {@link #feature} match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #feature} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    private boolean matchesFeature(String filter) {
        return (filter == null) ? true : filter.equals(feature);
    }

    /**
     * Checks if given filter and currently set {@link #procedure} match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #procedure} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    private boolean matchesProcedure(String filter) {
        return (filter == null) ? true : filter.equals(procedure);
    }

    /**
     * Checks if given filter and currently set {@link #phenomenon} match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #phenomenon} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    private boolean matchesPhenomenon(String filter) {
        return (filter == null) ? true : filter.equals(phenomenon);
    }

    /**
     * Checks if given filter and currently set {@link #offering} match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #offering} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    private boolean matchesOffering(String filter) {
        return (filter == null) ? true : filter.equals(offering);
    }

}
