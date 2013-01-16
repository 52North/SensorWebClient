package org.n52.server.ses.feeder.hibernate;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Set;

/**
 * The sensor class handles the sensor objects in the database.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class Sensor implements Serializable {

    /** The id. */
    private Integer id;

    /** The procedure. */
    private String procedure;

    /** The offering. */
    private Set<Offering> offerings;

    /** The ses id. */
    private String sesId;

    /** The last update. */
    private Calendar lastUpdate;

    /** The used. */
    private boolean used;

    /** The update interval. */
    private long updateInterval;

    /** The sos. */
    private SOS sos;

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
     * Checks if is used.
     *
     * @return the used
     */
    public boolean isUsed() {
        return this.used;
    }

    /**
     * Sets the used.
     *
     * @param used the used to set
     */
    public void setUsed(boolean used) {
        this.used = used;
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

    /**
     * @param offerings the offerings to set
     */
    public void setOfferings(Set<Offering> offerings) {
        this.offerings = offerings;
    }

    /**
     * @return the offerings
     */
    public Set<Offering> getOfferings() {
        return this.offerings;
    }

    /**
     * Sets the sos.
     *
     * @param sos the sos to set
     */
    public void setSos(SOS sos) {
        this.sos = sos;
    }

    /**
     * Gets the sos.
     *
     * @return the sos
     */
    public SOS getSos() {
        return this.sos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MessageFormat.format("Sensor: id={0}, procedure={1}, offerings={2}", new Object[] { this.id, 
                this.procedure, this.offerings });
    }
}
