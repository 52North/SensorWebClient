package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.Calendar;

public class TimeseriesFeed implements Serializable {

    private static final long serialVersionUID = 8770405020547586667L;

    private Integer id;

    private String sesId;

    private FeedingMetadata feedingMetadata;

    private Calendar lastUpdate;

    @Deprecated
    private long usedCounter;

    private long updateInterval;

    private boolean active;

    private int inUse;
    
    public TimeseriesFeed() {
        // for serialization
    }

	public FeedingMetadata getFeedingMetadata() {
        return feedingMetadata;
    }

    public void setFeedingMetadata(FeedingMetadata feedingMetadata) {
        this.feedingMetadata = feedingMetadata;
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

    /**
     * @deprecated sharing is deprecatedreturn
     */
    public long getUsedCounter() {
		return usedCounter;
	}

    /**
     * @deprecated sharing is deprecatedreturn
     */
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