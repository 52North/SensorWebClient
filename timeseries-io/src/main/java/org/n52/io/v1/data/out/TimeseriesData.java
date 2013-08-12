
package org.n52.io.v1.data.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TimeseriesData {

    private List<TimeseriesValue> values = new ArrayList<TimeseriesValue>();

    /**
     * @param values
     *        the timestamp &lt;-&gt; value map.
     * @return a timeseries object.
     */
    public static TimeseriesData newTimeseriesData(Map<Long, String> values) {
        TimeseriesData timeseries = new TimeseriesData();
        for (Long timestamp : values.keySet()) {
            String value = values.get(timestamp);
            timeseries.addNewValue(timestamp, value);
        }
        return timeseries;
    }

    /**
     * @return a sorted list of timeseries values.
     */
    public TimeseriesValue[] getValues() {
        Collections.sort(values);
        return values.toArray(new TimeseriesValue[0]);
    }

    void setValues(TimeseriesValue[] values) {
        this.values = Arrays.asList(values);
    }

    private void addNewValue(Long timestamp, String value) {
        values.add(new TimeseriesValue(timestamp, value));
    }

    public class TimeseriesValue implements Comparable<TimeseriesValue>{

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
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("TimeseriesValue [ ");
            sb.append("timestamp: ").append(timestamp).append(", ");
            sb.append("value: ").append(value);
            return sb.append(" ]").toString();
        }

        @Override
        public int compareTo(TimeseriesValue o) {
            return getTimestamp().compareTo(o.getTimestamp());
        }
    }
}
