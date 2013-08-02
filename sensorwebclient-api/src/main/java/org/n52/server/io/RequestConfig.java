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

package org.n52.server.io;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;

import java.util.List;

import org.n52.oxf.ows.capabilities.ITime;
import org.n52.server.mgmt.ConfigurationContext;

/**
 * An object of this class stores the information which is used to execute a
 * GetObservation request.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 */
public class RequestConfig {

    private String sosURL;

    private String offeringID;

    private List<String> stations;

    private List<String> phenomenons;

    private List<String> procedures;

    private ITime time = null;

    private String firstLastParam;

    private String responseFormat = "";

    public RequestConfig(String sosURL, String offeringID, List<String> stationsSet,
            List<String> phenomenonsSet, List<String> procedureSet, ITime time) {
        this.sosURL = sosURL;
        this.offeringID = offeringID;
        this.stations = stationsSet;
        this.phenomenons = phenomenonsSet;
        this.procedures = procedureSet;
        this.time = time;
        this.responseFormat = getSOSMetadata(sosURL).getOmVersion();
    }

    public RequestConfig(String sosURL, String offeringID, List<String> stationsSet,
            List<String> phenomenonsSet, List<String> procedureSet, String timeParam) {
        this.sosURL = sosURL;
        this.offeringID = offeringID;
        this.stations = stationsSet;
        this.phenomenons = phenomenonsSet;
        this.procedures = procedureSet;
        this.firstLastParam = timeParam;
        this.responseFormat = ConfigurationContext.getSOSMetadata(sosURL).getOmVersion();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RequestConfig) {
            RequestConfig reqConf = (RequestConfig) obj;
            if (getSosURL().equals(reqConf.getSosURL())
                    && getOfferingID().equals(reqConf.getOfferingID())
                    && getStationsSet().equals(reqConf.getStationsSet())
                    && getPhenomenonsSet().equals(reqConf.getPhenomenonsSet())
                    && getProcedureSet().equals(reqConf.getProcedureSet())
                    && getTime().equals(reqConf.getTime())) {
                return true;
            }
        }
        return false;
    }

    public String getOfferingID() {
        return this.offeringID;
    }

    public List<String> getPhenomenonsSet() {
        return this.phenomenons;
    }

    public List<String> getProcedureSet() {
        return this.procedures;
    }

    public String getResponseFormat() {
        return this.responseFormat;
    }

    public String getSosURL() {
        return this.sosURL;
    }

    public List<String> getStationsSet() {
        return this.stations;
    }

    public ITime getTime() {
        return this.time;
    }

    public String getFirstLastParam() {
        return this.firstLastParam;
    }

}