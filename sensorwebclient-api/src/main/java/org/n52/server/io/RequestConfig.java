/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
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