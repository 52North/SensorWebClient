package org.n52.io.v1.data.in;



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

}
