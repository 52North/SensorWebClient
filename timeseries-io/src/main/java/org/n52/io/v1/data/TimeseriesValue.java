package org.n52.io.v1.data;


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