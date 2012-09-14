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

package org.n52.server.oxf.util.generator;

import java.util.List;

import org.n52.oxf.ows.capabilities.ITime;
import org.n52.server.oxf.util.ConfigurationContext;

/**
 * An object of this class stores the information which is used to execute a
 * GetObservation request.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 */
public class RequestConfig {

    private String sosURL;

    private String offeringID;

    private List<String> stationsSet;

    private List<String> phenomenonsSet;

    private List<String> procedureSet;

    private ITime time = null;

    private String firsLastParam;

    private String responseFormat = "";

    public RequestConfig(String sosURL, String offeringID, List<String> stationsSet,
            List<String> phenomenonsSet, List<String> procedureSet, ITime time) {
        this.sosURL = sosURL;
        this.offeringID = offeringID;
        this.stationsSet = stationsSet;
        this.phenomenonsSet = phenomenonsSet;
        this.procedureSet = procedureSet;
        this.time = time;
        this.responseFormat = ConfigurationContext.getSOSMetadata(sosURL).getOmVersion();
    }

    public RequestConfig(String sosURL, String offeringID, List<String> stationsSet,
            List<String> phenomenonsSet, List<String> procedureSet, String timeParam) {
        this.sosURL = sosURL;
        this.offeringID = offeringID;
        this.stationsSet = stationsSet;
        this.phenomenonsSet = phenomenonsSet;
        this.procedureSet = procedureSet;
        this.firsLastParam = timeParam;
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
        return this.phenomenonsSet;
    }

    public List<String> getProcedureSet() {
        return this.procedureSet;
    }

    public String getResponseFormat() {
        return this.responseFormat;
    }

    public String getSosURL() {
        return this.sosURL;
    }

    public List<String> getStationsSet() {
        return this.stationsSet;
    }

    public ITime getTime() {
        return this.time;
    }

    public String getFirstLastParam() {
        return this.firsLastParam;
    }

}