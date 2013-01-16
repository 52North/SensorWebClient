package org.n52.server.ses.feeder.hibernate;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * The sos class handles the sos objects in the databases.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class SOS implements Serializable {

    /** The id. */
    private Integer id;

    /** The url. */
    private String url;

    /** The sensors. */
    private Set<SensorToFeed> sensors = new HashSet<SensorToFeed>();

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets the url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the sensors.
     *
     * @return the sensors
     */
    public Set<SensorToFeed> getSensors() {
        return this.sensors;
    }

    /**
     * Sets the sensors.
     *
     * @param sensors the sensors to set
     */
    public void setSensors(Set<SensorToFeed> sensors) {
        this.sensors = sensors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MessageFormat.format("SOS: {0} {1} {2}", new Object[] { this.id, this.url, this.sensors });
    }
}
