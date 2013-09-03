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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.n52.shared.serializable.pojos.sos.Station.Type;

/**
 * An SOS timeseries representation identified by <code>serviceUrl</code>, <code>procedure</code>,
 * <code>phenomenon</code>, <code>feature</code>, and <code>offering</code>.<br/>
 * <br/>
 * Timeseries can be categorized by a custom label (by default {@link #phenomenon}). It can be used to filter
 * a set of timeseries which belongs to a predefined category.
 */
public class SosTimeseries implements Serializable {

    private static final long serialVersionUID = 4336908002034438766L;

    private String serviceUrl;

    private String procedure;

    private String phenomenon;

    private String feature;

    private String offering;

    private String category;

    private Type type = Type.DEFAULT;
    
    public SosTimeseries() {
        // for serialization
    }

    /**
     * @return <code>true</code> if complete, <code>false</code> otherwise.
     */
    public boolean parametersComplete() {
        return serviceUrl != null && offering != null && phenomenon != null && procedure != null && feature != null;
    }

    /**
     * Computes a unique timeseries id on-the-fly dependend on the following parameters:
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offering}</li>
     * <li>{@link #feature}</li>
     * <li>{@link #procedure}</li>
     * <li>{@link #phenomenon}</li>
     * </ul>
     * If a parameter is not set it will be ignored.
     * 
     * @return a unique and gml:id-valid identifier dependend on the parameter values set.
     */
    public String getTimeseriesId() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (serviceUrl != null) {
                md.update(serviceUrl.getBytes());
            }
            if (offering != null) {
                md.update(offering.getBytes());
            }
            if (phenomenon != null) {
                md.update(phenomenon.getBytes());
            }
            if (procedure != null) {
                md.update(procedure.getBytes());
            }
            if (feature != null) {
                md.update(feature.getBytes());
            }
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return "ts_" + bigInt.toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 message digester not available!", e);
        }
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    /**
     * A label to categorize this timeseries. If not set, the {@link #phenomenon} of the timeseries is
     * returned. Can be used to filter a set of stations according a common category.
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
        result = prime * result + ( (feature == null) ? 0 : feature.hashCode());
        result = prime * result + ( (offering == null) ? 0 : offering.hashCode());
        result = prime * result + ( (phenomenon == null) ? 0 : phenomenon.hashCode());
        result = prime * result + ( (procedure == null) ? 0 : procedure.hashCode());
        result = prime * result + ( (serviceUrl == null) ? 0 : serviceUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if ( ! (obj instanceof SosTimeseries))
            return false;
        SosTimeseries other = (SosTimeseries) obj;
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
        if (serviceUrl == null) {
            if (other.serviceUrl != null)
                return false;
        }
        else if ( !serviceUrl.equals(other.serviceUrl))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SosTimeseries: [ ").append("\n");
        sb.append("\tService: ").append(serviceUrl).append("\n");
        sb.append("\tOffering: ").append(offering).append("\n");
        sb.append("\tFeature: ").append(feature).append("\n");
        sb.append("\tProcedure: ").append(procedure).append("\n");
        sb.append("\tPhenomenon: ").append(phenomenon).append("\n");
        sb.append("\tCategory: ").append(category).append("\n]");
        return sb.toString();
    }

    // @Override // fails during gwt compile
    public SosTimeseries clone() {
        SosTimeseries timeseries = new SosTimeseries();
        timeseries.setFeature(feature);
        timeseries.setPhenomenon(phenomenon);
        timeseries.setServiceUrl(serviceUrl);
        timeseries.setProcedure(procedure);
        timeseries.setOffering(offering);
        timeseries.setCategory(category);
        return timeseries;
    }

    /**
     * Match against a filter criteria. The filter criteria is built as an <code>AND</code> criteria to match
     * against all parameters. If a parameter is <code>null</code> is will be ignored (to match).
     * 
     * @param offeringFilter
     *        filter to match the offering. If <code>null</code> the filter matches by default.
     * @param phenomenonFilter
     *        filter to match the phenomenon. If <code>null</code> the filter matches by default.
     * @param procedureFilter
     *        filter to match the procedure. If <code>null</code> the filter matches by default.
     * @param featureFilter
     *        filter to match the feature. If <code>null</code> the filter matches by default.
     * @return <code>true</code> if constellation matches to all given filters.
     */
    public boolean matchParameters(String offering, String phenomenon, String procedure, String feature) {
        return matchesOffering(offering) && matchesPhenomenon(phenomenon) && matchesProcedure(procedure)
                && matchesFeature(feature);
    }

    /**
     * Checks if given filter and currently set {@link #feature} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #feature} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesFeature(String filter) {
        return (filter == null) ? true : filter.equals(feature);
    }

    /**
     * Checks if given filter and currently set {@link #procedure} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #procedure} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesProcedure(String filter) {
        return (filter == null) ? true : filter.equals(procedure);
    }

    /**
     * Checks if given filter and currently set {@link #phenomenon} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #phenomenon} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesPhenomenon(String filter) {
        return (filter == null) ? true : filter.equals(phenomenon);
    }

    /**
     * Checks if given filter and currently set {@link #offering} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #offering} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesOffering(String filter) {
        return (filter == null) ? true : filter.equals(offering);
    }
    
    public Type getType(){
    	return this.type;
    }
    
    public void setType( Type type ){
    	this.type = type;
    }

	public String getParentName() {
		HashMap<String, ArrayList<String>> phenomenonCategories = PhenomenonCategories.getLists();
		for(String parentName : phenomenonCategories.keySet()) {
			if( phenomenonCategories.get(parentName).contains(this.getCategory()) ){
				return parentName;
			}
		}
		return null;
	}
}
