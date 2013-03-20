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
package org.n52.server.service;

import java.util.HashMap;

import org.n52.client.service.ServiceMetadataService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.responses.GetFeatureResponse;
import org.n52.shared.responses.GetOfferingResponse;
import org.n52.shared.responses.GetPhenomenonResponse;
import org.n52.shared.responses.GetProcedureResponse;
import org.n52.shared.responses.GetStationResponse;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceMetadataServiceImpl implements ServiceMetadataService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMetadataServiceImpl.class);

    private HashMap<String, String> getAllPhenomenaForSos(SOSMetadata meta) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (Phenomenon phen : meta.getPhenomenons()) {
            result.put(phen.getId(), phen.getLabel());
        }
        return result;
    }
    
    @Override
    public GetPhenomenonResponse getPhen4SOS(String sosURL) throws Exception {
        try {
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getPhen4SOS(sosUrl: %s)";
                LOG.debug(String.format(msgTemplate, sosURL));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(sosURL);
            HashMap<String, String> result = getAllPhenomenaForSos(meta);
            return new GetPhenomenonResponse(sosURL, result);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetProcedureResponse getProcedure(String serviceURL, String procedureID) throws Exception {
        try {
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getProcedure(sosUrl: %s, procedureID: %s)";
                LOG.debug(String.format(msgTemplate, serviceURL, procedureID));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceURL);
            Procedure procedure = meta.getProcedure(procedureID);
            return new GetProcedureResponse(serviceURL, procedure);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetOfferingResponse getOffering(String serviceURL, String offeringID) throws Exception {
        try {
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getOffering(sosUrl: %s, offeringID: %s)";
                LOG.debug(String.format(msgTemplate, serviceURL, offeringID));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceURL);
            Offering offering = meta.getOffering(offeringID);
            return new GetOfferingResponse(serviceURL, offering);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetFeatureResponse getFeature(String serviceURL, String featureID) throws Exception {
        try {
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getFeature(sosUrl: %s, featureID: %s)";
                LOG.debug(String.format(msgTemplate, serviceURL, featureID));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceURL);
            FeatureOfInterest feature = meta.getFeature(featureID);
            return new GetFeatureResponse(serviceURL, feature);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetStationResponse getStation(String serviceURL, String offeringID, String procedureID, String phenomenonID, String featureID) throws Exception {
        try {
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getStation(sosUrl: %s, offeringID: %s, procedureID: %s, phenomenonID: %s, featureID %s)";
                LOG.debug(String.format(msgTemplate, serviceURL, offeringID, procedureID, phenomenonID, featureID));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceURL);
            Station station = meta.getStationByParameterConstellation(offeringID, featureID, procedureID, phenomenonID);
            return new GetStationResponse(serviceURL, station);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
}
