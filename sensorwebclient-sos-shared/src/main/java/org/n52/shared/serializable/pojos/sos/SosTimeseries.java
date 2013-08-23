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

import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashIdGenerator;
import org.n52.shared.requests.query.QueryParameters;

/**
 * An SOS timeseries representation identified by <code>serviceUrl</code>, <code>procedure</code>,
 * <code>phenomenon</code>, <code>feature</code>, and <code>offering</code>.<br/>
 * <br/>
 * Timeseries can be categorized by a custom label (by default {@link #phenomenonId}). It can be used to
 * filter a set of timeseries which belongs to a predefined category.
 */
public class SosTimeseries implements Serializable {

    private static final long serialVersionUID = 4336908002034438766L;

    private SosService sosService;

    private Procedure procedure;

    private Phenomenon phenomenon;

    private Feature feature;

    private Offering offering;

    private String category;

    public SosTimeseries() {
        // for serialization
    }

    /**
     * @return <code>true</code> if complete, <code>false</code> otherwise.
     */
    public boolean parametersComplete() {
        return sosService != null && offering != null && phenomenon != null && procedure != null && feature != null;
    }

    /**
     * Computes a unique timeseries id on-the-fly dependend on the following parameters:
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offeringId}</li>
     * <li>{@link #featureId}</li>
     * <li>{@link #procedureId}</li>
     * <li>{@link #phenomenonId}</li>
     * </ul>
     * If a parameter is not set it will be ignored.
     * 
     * @return a unique and gml:id-valid identifier dependend on the parameter values set.
     */
    public String getTimeseriesId() {
        String[] parameters = getParamtersAsArray();
        IdGenerator idGenerator = new MD5HashIdGenerator("ts_");
        return idGenerator.generate(parameters);
    }

    private String[] getParamtersAsArray() {
        return new String[] {getServiceUrl(), getOfferingId(), getPhenomenonId(), getProcedureId(), getFeatureId()};
    }

    public SosService getSosService() {
        return sosService;
    }

    public void setSosService(SosService sosService) {
        this.sosService = sosService;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Phenomenon getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(Phenomenon phenomenon) {
        this.phenomenon = phenomenon;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    public String getServiceUrl() {
        return sosService == null ? null : sosService.getServiceUrl();
    }

    /**
     * A label to categorize this timeseries. If not set, the {@link #phenomenonId} of the timeseries is
     * returned. Can be used to filter a set of stations according a common category.
     * 
     * @return a label to categorize stations on which filtering can take place.
     */
    public String getCategory() {
        return category == null ? getPhenomenonId() : category;
    }

    /**
     * @param category
     *        a filter to categorize stations.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public String getProcedureId() {
        return procedure == null ? null : procedure.getProcedureId();
    }

    public String getFeatureId() {
        return feature == null ? null : feature.getFeatureId();
    }

    public String getOfferingId() {
        return offering == null ? null : offering.getOfferingId();
    }

    public String getPhenomenonId() {
        return phenomenon == null ? null : phenomenon.getPhenomenonId();
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
    public boolean matchParameters(QueryParameters filter) {
        return matchesOffering(filter.getOffering()) 
                && matchesService(filter.getService())
                && matchesPhenomenon(filter.getPhenomenon())
                && matchesProcedure(filter.getProcedure())
                && matchesFeature(filter.getFeature());
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
     * Checks if given filter and currently set {@link #featureId} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #featureId} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesFeature(String filter) {
        return (filter == null) ? true : filter.equals(feature.getFeatureId());
    }

    /**
     * Checks if given filter and currently set {@link #procedureId} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #procedureId} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesProcedure(String filter) {
        return (filter == null) ? true : filter.equals(procedure.getProcedureId());
    }
    
    /**
     * Checks if given filter and currently set {@link #sosService} do match.
     * 
     * @param filter
     *        the service to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #sosService} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesService(String filter) {
        return (filter == null) ? true : filter.equals(sosService.getServiceUrl());
    }

    /**
     * Checks if given filter and currently set {@link #phenomenonId} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #phenomenonId} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesPhenomenon(String filter) {
        return (filter == null) ? true : filter.equals(phenomenon.getPhenomenonId());
    }

    /**
     * Checks if given filter and currently set {@link #offeringId} do match.
     * 
     * @param filter
     *        the feature to match. If paramter is <code>null</code> the filter does not apply.
     * @return <code>false</code> if filter does not match the {@link #offeringId} of this instance. Returns
     *         <code>true</code> if filter matches or is <code>null</code> .
     */
    public boolean matchesOffering(String filter) {
        return (filter == null) ? true : filter.equals(offering.getOfferingId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (feature == null) ? 0 : feature.hashCode());
        result = prime * result + ( (offering == null) ? 0 : offering.hashCode());
        result = prime * result + ( (phenomenon == null) ? 0 : phenomenon.hashCode());
        result = prime * result + ( (procedure == null) ? 0 : procedure.hashCode());
        result = prime * result + ( (sosService == null) ? 0 : sosService.hashCode());
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
        if (sosService == null) {
            if (other.sosService != null)
                return false;
        }
        else if ( !sosService.equals(other.sosService))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SosTimeseries: [ ").append("\n");
        sb.append("\tService: ").append(getServiceUrl()).append("\n");
        sb.append("\tOffering: ").append(getOfferingId()).append("\n");
        sb.append("\tFeature: ").append(getFeatureId()).append("\n");
        sb.append("\tProcedure: ").append(getProcedureId()).append("\n");
        sb.append("\tPhenomenon: ").append(getPhenomenonId()).append("\n");
        sb.append("\tCategory: ").append(category).append("\n]");
        return sb.toString();
    }

    // @Override // fails during gwt compile
    public SosTimeseries clone() {
        SosTimeseries timeseries = new SosTimeseries();
        timeseries.setFeature(feature);
        timeseries.setPhenomenon(phenomenon);
        timeseries.setSosService(sosService);
        timeseries.setProcedure(procedure);
        timeseries.setOffering(offering);
        timeseries.setCategory(category);
        return timeseries;
    }

}
