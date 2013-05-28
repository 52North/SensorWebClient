package org.n52.server.service.rest;

import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;


public class UndesignedParameterSet extends ParameterSet {

    /**
     * The timeseriesIds of interest.
     */
    private String[] timeseriesIds;

    @Override
    public String[] getTimeseries() {
        return timeseriesIds;
    }

    public void setTimeseries(String[] timeseries) {
        this.timeseriesIds = timeseries;
    }

    @Override
    public TimeseriesRenderingOptions getTimeseriesRenderingOptions(String timeseriesId) {
        return null;
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public int getHeight() {
        return -1;
    }
    
}
