/**
 * ﻿Copyright (C) 2012
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
 */
package org.n52.shared.responses;

import java.io.Serializable;
import java.util.List;

import org.n52.shared.serializable.pojos.sos.Station;

public class StationPositionsResponse implements Serializable {

    private static final long serialVersionUID = 8042730853557317306L;

    private String sosURL;

    private List<Station> stations;

    private boolean finished;

    private int startIdx;

    private int endIdx;

    private String srs;

    @SuppressWarnings("unused")
    private StationPositionsResponse() {
        // serializable for GWT needs empty default constructor
    }

    /**
     * Instantiates a new station positions response.
     * 
     * @param sosURL
     *            the sos url
     * @param stations
     *            the stations
     * @param srs
     *            the srs
     * @param finished
     *            the finished
     * @param startIdx
     *            the start idx
     * @param endIdx
     *            the end idx
     */
    public StationPositionsResponse(String sosURL, List<Station> stations, String srs,
            boolean finished, int startIdx, int endIdx) {
        this.sosURL = sosURL;
        this.stations = stations;
        this.srs = srs;
        this.finished = finished;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }

    /**
     * Gets the srs.
     * 
     * @return the srs
     */
    public String getSrs() {
        return this.srs;
    }

    /**
     * Gets the start idx.
     * 
     * @return the start idx
     */
    public int getStartIdx() {
        return this.startIdx;
    }

    /**
     * Gets the end idx.
     * 
     * @return the end idx
     */
    public int getEndIdx() {
        return this.endIdx;
    }

    /**
     * Checks if is finished.
     * 
     * @return true, if is finished
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Gets the sos url.
     * 
     * @return the sos url
     */
    public String getSosURL() {
        return this.sosURL;
    }

    /**
     * Gets the stations.
     * 
     * @return the stations
     */
    public List<Station> getStations() {
        return this.stations;
    }

}
