
package org.n52.shared.serializable.pojos;

import java.io.Serializable;

/**
 * Represents a timeseries parameter constellation. A parameter constellation consists of
 * <ul>
 * <li>{@link #serviceUrl}</li>
 * <li>{@link #offering}</li>
 * <li>{@link #procedure}</li>
 * <li>{@link #phenomenon}</li>
 * <li>{@link #featureOfInterest}</li>
 * </ul>
 * <br>
 * As an SES just uses procedure and phenomenon parameters to identify data stream to filter, the
 * {@link SosSesFeeder} has to use a unique id for registering a timeseries at the SES. This unique id is
 * being mapped so that each communication flow can be resolved either to an SOS parameter constellation (
 * {@link TimeseriesMetadata}) or a global id representing the timeseries on SES side.
 */
public class TimeseriesMetadata implements Serializable {

    private static final long serialVersionUID = -2169674834906583384L;

    @SuppressWarnings("unused")
    private String timeseriesId; // used as db id

    private Integer id;

    private String serviceUrl;

    private String offering;

    private String procedure;

    private String phenomenon;

    private String featureOfInterest;

    public TimeseriesMetadata() {
        // for serialization
    }

    /**
     * Generates a unique id for this timeseries. The id is created by hashing all parameters forming an
     * instance to a unique parameter constellation for a specific timeseries within an SOS instance.<br>
     * <br>
     * The following parameters identify a particular timeseries in an SOS instance:
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offering}</li>
     * <li>{@link #procedure}</li>
     * <li>{@link #phenomenon}</li>
     * <li>{@link #featureOfInterest}</li>
     * </ul>
     * 
     * @return an created id by hashing all timeseries parameters.
     * @see #hashCode()
     */
    public String getGlobalSesId() {
        return Integer.toString(hashCode());
    }

    /**
     * Gets the unique timeseries is for this parameter constellation. This method does <b>only</b> return a
     * reliable result, if and only if all the following parameters are set:
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offering}</li>
     * <li>{@link #procedure}</li>
     * <li>{@link #phenomenon}</li>
     * <li>{@link #featureOfInterest}</li>
     * </ul>
     * 
     * @return the unique timeseries id, if parameter constellation is complete.
     * @throws IllegalStateException
     *         if one or more of the required parameters are not set yet.
     * @see #getGlobalSesId()
     */
    public String getTimeseriesId() {
        if ( !isComplete()) {
            throw new IllegalStateException("Timeseries metadata has to be complete to determine id.");
        }
        return getGlobalSesId();
    }

    // leave package private for serialization
    void setTimeseriesId(String timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        return featureOfInterest;
    }

    public void setFeatureOfInterest(String featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * {@link Object#hashCode()} implementation using
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offering}</li>
     * <li>{@link #procedure}</li>
     * <li>{@link #phenomenon}</li>
     * <li>{@link #featureOfInterest}</li>
     * </ul>
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (featureOfInterest == null) ? 0 : featureOfInterest.hashCode());
        result = prime * result + ( (offering == null) ? 0 : offering.hashCode());
        result = prime * result + ( (phenomenon == null) ? 0 : phenomenon.hashCode());
        result = prime * result + ( (procedure == null) ? 0 : procedure.hashCode());
        result = prime * result + ( (serviceUrl == null) ? 0 : serviceUrl.hashCode());
        return result;
    }

    /**
     * {@link Object#equals(Object)} implementation using
     * <ul>
     * <li>{@link #serviceUrl}</li>
     * <li>{@link #offering}</li>
     * <li>{@link #procedure}</li>
     * <li>{@link #phenomenon}</li>
     * <li>{@link #featureOfInterest}</li>
     * </ul>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if ( ! (obj instanceof TimeseriesMetadata))
            return false;
        TimeseriesMetadata other = (TimeseriesMetadata) obj;
        if (featureOfInterest == null) {
            if (other.featureOfInterest != null)
                return false;
        }
        else if ( !featureOfInterest.equals(other.featureOfInterest))
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

    public boolean isComplete() {
        boolean hasServiceUrl = serviceUrl != null;
        boolean hasOffering = offering != null;
        boolean hasProcedure = procedure != null;
        boolean hasPhenomenon = phenomenon != null;
        boolean hasFOI = featureOfInterest != null;
        return hasServiceUrl && hasOffering && hasProcedure && hasPhenomenon && hasFOI;
    }

}
