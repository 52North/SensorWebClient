<<<<<<< HEAD
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
    
=======

package org.n52.io.v1.data;

import org.n52.shared.serializable.pojos.sos.SosTimeseries;

/**
 * Presents a view on {@link SosTimeseries} to control output which gets automatically serialized/marshalled
 */
public class TimeseriesOutput {

    private SosTimeseries timeseries;

    public TimeseriesOutput(SosTimeseries timseriesToWrap) {
        timeseries = timseriesToWrap;
    }

    public String getServiceUrl() {
        return timeseries.getServiceUrl();
    }

    public String getOffering() {
        return timeseries.getOfferingId();
    }

    public String getFeature() {
        return timeseries.getFeatureId();
    }

    public String getProcedure() {
        return timeseries.getProcedureId();
    }

    public String getPhenomenon() {
        return timeseries.getPhenomenonId();
    }

>>>>>>> branch 'master' of ssh://git@github.com/ridoo/SensorWebClient.git
}
