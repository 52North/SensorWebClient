package org.n52.server.ses.feeder.hibernate;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * The sensor class handles the sensor objects in the database.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class SensorToFeed implements Serializable {

    private Integer id;

    private String procedure;

    private String offering;

    private String phenomenon;
    
    private String featureOfInterest;
    
    private String serviceURL;
    
    private String sesId;

    private Calendar lastUpdate;

    private long usedCounter;

    private long updateInterval;

    public String getOffering() {
		return offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public String getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public String getFeatureOfInterest() {
		return featureOfInterest;
	}

	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public long getUsedCounter() {
		return usedCounter;
	}

	public void setUsedCounter(long usedCounter) {
		this.usedCounter = usedCounter;
	}

	/**
     * Gets the id.
     *
     * @return the database id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id the database id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the procedure.
     *
     * @return the procedure
     */
    public String getProcedure() {
        return this.procedure;
    }

    /**
     * Sets the procedure.
     *
     * @param procedure the procedure to set
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    /**
     * Gets the ses id.
     *
     * @return the sesId
     */
    public String getSesId() {
        return this.sesId;
    }

    /**
     * Sets the ses id.
     *
     * @param sesId the ses ID to set
     */
    public void setSesId(String sesId) {
        this.sesId = sesId;
    }

    /**
     * Gets the last update.
     *
     * @return the lastUpdate
     */
    public Calendar getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Sets the last update.
     *
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Sets the update interval.
     *
     * @param updateInterval the updateInterval to set
     */
    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Gets the update interval.
     *
     * @return updateInterval
     */
    public long getUpdateInterval() {
        return this.updateInterval;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Sensor: id={0}, procedure={1}, offerings={2}", new Object[] { this.id, 
                this.procedure, this.offering });
    }
}
