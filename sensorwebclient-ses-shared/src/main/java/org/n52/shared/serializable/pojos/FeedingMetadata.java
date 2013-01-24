package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public class FeedingMetadata implements Serializable {
    
    private static final long serialVersionUID = -2169674834906583384L;

    private String serviceUrl;
    
    private String offering;
    
    private String procedure;
    
    private String phenomenon;
    
    private String featureOfInterest;

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
    
}
