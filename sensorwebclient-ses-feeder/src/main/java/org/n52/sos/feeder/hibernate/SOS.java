/****************************************************************************
 * Copyright (C) 2010
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 * 
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 * 
 * This program is free software; you can redistribute and/or modify it under 
 * the terms of the GNU General Public License version 2 as published by the 
 * Free Software Foundation.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 * 
 * Author: Jan Schulte
 * Created: 19.05.2010
 *****************************************************************************/
package org.n52.sos.feeder.hibernate;

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
    private Set<Sensor> sensors = new HashSet<Sensor>();

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
    public Set<Sensor> getSensors() {
        return this.sensors;
    }

    /**
     * Sets the sensors.
     *
     * @param sensors the sensors to set
     */
    public void setSensors(Set<Sensor> sensors) {
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
