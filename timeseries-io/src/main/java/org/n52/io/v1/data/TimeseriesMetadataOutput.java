
package org.n52.io.v1.data;

import java.util.Map;



public class TimeseriesMetadataOutput {

    private String id;

    private String uom;
    
    private StationOutput station;
    
    private Map<String, TimeseriesValue> refValues;

    private TimeseriesValue firstValue;

    private TimeseriesValue lastValue;

    private TimeseriesOutput parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public StationOutput getStation() {
        return station;
    }

    public void setStation(StationOutput station) {
        this.station = station;
    }

    public Map<String, TimeseriesValue> getRefValues() {
        return refValues;
    }

    public void setRefValues(Map<String, TimeseriesValue> refValues) {
        this.refValues = refValues;
    }

    public TimeseriesValue getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(TimeseriesValue firstValue) {
        this.firstValue = firstValue;
    }

    public TimeseriesValue getLastValue() {
        return lastValue;
    }

    public void setLastValue(TimeseriesValue lastValue) {
        this.lastValue = lastValue;
    }

    public TimeseriesOutput getParameters() {
        return parameters;
    }

    public void setParameters(TimeseriesOutput timeseries) {
        this.parameters = timeseries;
    }
    
}
