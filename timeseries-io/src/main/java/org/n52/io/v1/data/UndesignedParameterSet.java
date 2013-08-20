package org.n52.io.v1.data;




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
    
    public static UndesignedParameterSet createForSingleTimeseries(String timeseriesId, String timespan) {
        UndesignedParameterSet parameters = new UndesignedParameterSet();
        parameters.setTimeseries(new String[] { timeseriesId });
        parameters.setTimespan(timespan);
        return parameters;
    }

    public static UndesignedParameterSet createFromDesignedParameters(DesignedParameterSet designedSet) {
        UndesignedParameterSet parameters = new UndesignedParameterSet();
        parameters.setTimeseries(designedSet.getTimeseries());
        parameters.setLanguage(designedSet.getLanguage());
        parameters.setTimespan(designedSet.getTimespan());
        return parameters;
    }
}
