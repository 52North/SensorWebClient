
package org.n52.server.service.rest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TimeseriesData {

    private List<TimeseriesValue> values = new ArrayList<TimeseriesValue>();

    private String uom;
    
    /**
     * @param values
     *        the timestamp &lt;-&gt; value map.
     * @param uom
     *        the unit of measurement.
     * @return a timeseries object.
     */
    public static TimeseriesData newTimeseriesData(Map<Long, String> values, String uom) {
        TimeseriesData timeseries = new TimeseriesData();
        for (Long timestamp : values.keySet()) {
            String value = values.get(timestamp);
            timeseries.addNewValue(timestamp, value);
        }
        timeseries.setUom(uom);
        return timeseries;
    }

    public TimeseriesValue[] getValues() {
        return values.toArray(new TimeseriesValue[0]);
    }

    void setValues(TimeseriesValue[] values) {
        this.values = Arrays.asList(values);
    }

    private void addNewValue(Long timestamp, String value) {
        values.add(new TimeseriesValue(timestamp, value));
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public class TimeseriesValue {

        private Long timestamp;

        private String value;

        public TimeseriesValue() {
            // for serialization
        }

        public TimeseriesValue(Long timestamp, String value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
