package org.n52.io.v1.data;

public class TimeseriesOutput {
    
    private OfferingOutput offering;
    
    private FeatureOutput feature;
    
    private ProcedureOutput procedure;
    
    private PhenomenonOutput phenomenon;
    
    private CategoryOutput category;

    public OfferingOutput getOffering() {
        return offering;
    }

    public void setOffering(OfferingOutput offering) {
        this.offering = offering;
    }

    public FeatureOutput getFeature() {
        return feature;
    }

    public void setFeature(FeatureOutput feature) {
        this.feature = feature;
    }

    public ProcedureOutput getProcedure() {
        return procedure;
    }

    public void setProcedure(ProcedureOutput procedure) {
        this.procedure = procedure;
    }

    public PhenomenonOutput getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(PhenomenonOutput phenomenon) {
        this.phenomenon = phenomenon;
    }

    public CategoryOutput getCategory() {
        return category;
    }

    public void setCategory(CategoryOutput category) {
        this.category = category;
    }
    
}
