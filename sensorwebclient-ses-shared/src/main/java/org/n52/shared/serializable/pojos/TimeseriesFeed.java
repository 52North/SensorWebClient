
package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A {@link TimeseriesFeed} represents a continues data stream of timeseries observation data hold by a
 * particular SOS which shall be filtered by an SES instance.
 */
public class TimeseriesFeed implements Serializable {

    private static final long serialVersionUID = 8770405020547586667L;

    private Integer id;

    private String sesId;

    private TimeseriesMetadata timeseriesMetadata;

    private Calendar lastUpdate;

    private long usedCounter;

    private long updateInterval;

    private boolean active;

    private int inUse;

    TimeseriesFeed() {
        // for serialization
    }

    /**
     * @param timeseriesMetadata
     *        the timeseries metadata representing a parameter constellation to retrieve observation data from
     *        a specific SOS instance.
     */
    public TimeseriesFeed(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
    }

    public TimeseriesMetadata getTimeseriesMetadata() {
        return timeseriesMetadata;
    }

    public void setTimeseriesMetadata(TimeseriesMetadata timeseriesMetadata) {
        this.timeseriesMetadata = timeseriesMetadata;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Calendar getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    public long getUpdateInterval() {
        return this.updateInterval;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getInUse() {
        return inUse;
    }

    public void setInUse(int inUse) {
        this.inUse = inUse;
    }

}