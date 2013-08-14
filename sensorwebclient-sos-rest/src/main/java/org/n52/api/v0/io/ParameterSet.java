package org.n52.api.v0.io;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;

public abstract class ParameterSet {

    /**
     * The timespan of interest (as <a href="http://en.wikipedia.org/wiki/ISO_8601#Time_intervals">ISO8601
     * interval</a> excluding the Period only version).
     */
    private String timespan;
    
    protected ParameterSet() {
        timespan = createDefaultTimespan();
    }

    private String createDefaultTimespan() {
        DateTime now = new DateTime();
        DateTime lastWeek = now.minusWeeks(1);
        return new Interval(lastWeek, now).toString();
    }

    public String getTimespan() {
        return timespan;
    }
    
    public void setTimespan(String timespan) {
        if (timespan == null) {
            this.timespan = createDefaultTimespan();
        }
        else {
            this.timespan = validateTimespan(timespan);
        }
    }

    private String validateTimespan(String timespan) {
        return Interval.parse(timespan).toString();
    }

    public abstract String[] getTimeseries();
    
    public abstract TimeseriesRenderingOptions getTimeseriesRenderingOptions(String timeseriesId);
    
    public abstract int getWidth();
    
    public abstract int getHeight();
    
}
