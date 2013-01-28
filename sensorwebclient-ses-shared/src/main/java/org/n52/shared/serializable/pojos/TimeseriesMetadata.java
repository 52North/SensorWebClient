package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public class TimeseriesMetadata implements Serializable {
    
    private static final long serialVersionUID = -2169674834906583384L;

    private Integer id;
    
    private String serviceUrl;
    
    private String offering;
    
    private String procedure;
    
    private String phenomenon;
    
    private String featureOfInterest;

    public TimeseriesMetadata() {
        // for serialization
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
    
}
