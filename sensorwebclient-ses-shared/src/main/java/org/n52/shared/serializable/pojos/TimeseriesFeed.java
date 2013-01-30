
package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A {@link TimeseriesFeed} represents a continues data stream of timeseries observation data hold by a
 * particular SOS which shall be filtered by an SES instance.
 */
public class TimeseriesFeed implements Serializable {

    private static final long serialVersionUID = 8770405020547586667L;

    private String timeseriesId;

    private String sesId;

    private TimeseriesMetadata timeseriesMetadata;

    private Calendar lastFeeded;

    private long usedCounter;

    private long lastConsideredTimeInterval;

    private boolean active;

    /**
     * use {@link #usedCounter} as it implicitly indicates if feed is being used
     */
    @Deprecated
    private int inUse;

    TimeseriesFeed() {
        // for serialization
    }

    /**
     * Creates a timeseries feed from a parameter constellation.
     * 
     * @param timeseriesMetadata
     *        the timeseries metadata representing a parameter constellation to retrieve observation data from
     *        a specific SOS instance.
     */
    public TimeseriesFeed(TimeseriesMetadata timeseriesMetadata) {
        if ( !isValid(timeseriesMetadata)) {
            throw new IllegalStateException("Passed parameter constellation is not complete.");
        }
        this.timeseriesId = timeseriesMetadata.getTimeseriesId();
        this.timeseriesMetadata = timeseriesMetadata;
    }

    private boolean isValid(TimeseriesMetadata timeseriesMetadata) {
        return timeseriesMetadata.isComplete();

    }

    public TimeseriesMetadata getTimeseriesMetadata() {
        return timeseriesMetadata;
    }

    public void setTimeseriesMetadata(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
    }

    public String getTimeseriesId() {
        return this.timeseriesId;
    }

    public void setTimeseriesId(String timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    public String getSesId() {
        return this.sesId;
    }

    public void setSesId(String sesId) {
        this.sesId = sesId;
    }

    public long getUsedCounter() {
        return usedCounter;
    }

    public void setUsedCounter(long usedCounter) {
        this.usedCounter = usedCounter;
    }

    public boolean hasBeenFeededBefore() {
        return lastFeeded != null;
    }

    /**
     * @return when the last GetObservation update was done for this feed. 
     */
    public Calendar getLastFeeded() {
        return lastFeeded;
    }

    /**
     * @param lastFeeded when the last GetObservation update was done for this feed.
     */
    public void setLastFeeded(Calendar lastFeeded) {
        this.lastFeeded = lastFeeded;
    }

    /**
     * @param lastConsideredTimeInterval
     *        the time interval of the last successful GetObservation response in milliseconds.
     */
    public void setLastConsideredTimeInterval(long lastConsideredTimeInterval) {
        this.lastConsideredTimeInterval = lastConsideredTimeInterval;
    }

    /**
     * @return the time interval of the last successful GetObservation response in milliseconds.
     */
    public long getLastConsideredTimeInterval() {
        return lastConsideredTimeInterval;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Deprecated
    public int getInUse() {
        return inUse;
    }

    @Deprecated
    public void setInUse(int inUse) {
        this.inUse = inUse;
    }

}