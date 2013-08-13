
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

}
