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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.shared.exceptions.ServiceOccupiedException;
import org.n52.shared.requests.query.FeatureQuery;
import org.n52.shared.requests.query.OfferingQuery;
import org.n52.shared.requests.query.PhenomenonQuery;
import org.n52.shared.requests.query.ProcedureQuery;
import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.requests.query.StationQuery;
import org.n52.shared.requests.query.responses.FeatureQueryResponse;
import org.n52.shared.requests.query.responses.OfferingQueryResponse;
import org.n52.shared.requests.query.responses.PhenomenonQueryResponse;
import org.n52.shared.requests.query.responses.ProcedureQueryResponse;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.requests.query.responses.StationQueryResponse;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryServiceImpl implements QueryService {

    private static final Logger LOG = LoggerFactory.getLogger(QueryServiceImpl.class);

	@Override
	public QueryResponse doQuery(QueryRequest request)
			throws Exception {
		// TODO optimize sever side
		if (request instanceof FeatureQuery) {
			return getFeatureResponse((FeatureQuery) request);
		} else if (request instanceof PhenomenonQuery) {
			return getPhenomenons((PhenomenonQuery)request);
		} else if (request instanceof ProcedureQuery) {
			return getProcedure((ProcedureQuery)request);
		} else if (request instanceof OfferingQuery) {
			return getOffering((OfferingQuery) request);
		} else if (request instanceof StationQuery) {
			return getStations((StationQuery) request);
		}
		return null;
	}
	
    private QueryResponse getStations(StationQuery query) throws Exception {
        try {
        	// check if update process is still running
        	if (ConfigurationContext.UPDATE_TASK_RUNNING) {
				LOG.info("Update running, no service available currently.");
				String reason = "Update running, currently no service available, please try again later";
				throw new ServiceOccupiedException(reason);
			}
        	String serviceUrl = query.getServiceUrl();
        	SOSMetadata metadata = ConfigurationContext.getSOSMetadata(serviceUrl);
        	ArrayList<Station> finalStations = new ArrayList<Station>();
            ArrayList<Station> stations = (ArrayList<Station>) metadata.getStations();
            Collection<String> offeringFilter = query.getOfferingFilter();
            Collection<String> phenomenonFilter = query.getPhenomenonFilter();
            Collection<String> procedureFilter = query.getProcedureFilter();
            Collection<String> featureFilter = query.getFeatureOfInterestFilter();
        	int startIndex = getStartIndex(query.getPagingStartIndex());
        	int interval = getInterval(query.getPagingInterval(), stations.size());
        	BoundingBox spatialFilter = query.getSpatialFilter();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getStations(sosUrl: %s, offeringID: %s, procedureID: %s, phenomenonID: %s, featureID: %s, Start: %s, Interval: %s, Spatial: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl, offeringFilter, procedureFilter, phenomenonFilter, featureFilter, startIndex, interval, spatialFilter));
            }
            
            boolean shallForceXYAxisOrder = metadata.isForceXYAxisOrder();
            AReferencingHelper referencing = createReferenceHelper(shallForceXYAxisOrder);
            
            int endIndex = 0;
            for(int i = startIndex; i < stations.size() && finalStations.size() < interval; i++) {
            	Station station = stations.get(i);
                if (referencing.isStationContainedByBBox(spatialFilter, station)) {
                	station.removeUnmatchedConstellations(offeringFilter, phenomenonFilter, procedureFilter, featureFilter);
                	if(station.hasAtLeastOneParameterconstellation()) {
                		finalStations.add(station);
                        endIndex = i + 1;
                	}
                }
            }
            
            boolean finished = isFinished(endIndex, finalStations);
            StationQueryResponse response = new StationQueryResponse();
            response.setServiceUrl(serviceUrl);
			response.setPagingEnd(finished);
            response.setPagingEndIndex(endIndex);
            response.setStations(finalStations);
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private int getInterval(int pagingInterval, int size) {
		return pagingInterval != 0 ? pagingInterval : size;
	}

	private int getStartIndex(int pagingStartIndex) {
		return pagingStartIndex != 0 ? pagingStartIndex : 0;
	}
	
	private boolean isFinished(int endIndex, ArrayList<Station> stations) {
        boolean devMode = ConfigurationContext.IS_DEV_MODE;
        return devMode || endIndex >= stations.size();
    }

	private QueryResponse getOffering(OfferingQuery query) throws Exception {
        try {
        	String serviceUrl = query.getServiceUrl();
        	Collection<String> offeringFilter = query.getOfferingFilter();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getOffering(sosUrl: %s, offeringID: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl, offeringFilter));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
            OfferingQueryResponse response = new OfferingQueryResponse();
            response.setServiceUrl(serviceUrl);
            for (String offering : offeringFilter) {
				response.addOffering(meta.getOffering(offering));
			}
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

	
	private QueryResponse getProcedure(ProcedureQuery query) throws Exception {
        try {
        	String serviceUrl = query.getServiceUrl();
        	Collection<String> procedureFilter = query.getProcedureFilter();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getProcedure(sosUrl: %s, procedureID: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl, procedureFilter));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
            ProcedureQueryResponse response = new ProcedureQueryResponse();
            response.setServiceUrl(serviceUrl);
            for (String procedure : procedureFilter) {
				response.addProcedure(meta.getProcedure(procedure));
			}
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
	
	private QueryResponse getPhenomenons(PhenomenonQuery query) throws Exception {
		try {
			String serviceUrl = query.getServiceUrl();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getPhen4SOS(sosUrl: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
            PhenomenonQueryResponse response = new PhenomenonQueryResponse();
            response.setPhenomenons(meta.getPhenomenons());
            response.setServiceUrl(serviceUrl);
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
	}
	
	private QueryResponse getFeatureResponse(FeatureQuery query) throws Exception {
	    try {
	    	String serviceUrl = query.getServiceUrl();
	    	Collection<String> featureFilter = query.getFeatureOfInterestFilter();
	        if (LOG.isDebugEnabled()) {
	            String msgTemplate = "Request -> getFeature(sosUrl: %s, featureID: %s)";
	            LOG.debug(String.format(msgTemplate, serviceUrl, featureFilter));
	        }
	        SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
	        FeatureQueryResponse response = new FeatureQueryResponse();
	        for (String feature : featureFilter) {
				response.addFeature(meta.getFeature(feature));
			}
	        response.setServiceUrl(serviceUrl);
	        return response;
	    } catch (Exception e) {
	        LOG.error("Exception occured on server side.", e);
	        throw e; // last chance to log on server side
	    }
	}
	
    private AReferencingHelper createReferenceHelper(boolean forceXYAxisOrder) {
        if (forceXYAxisOrder) {
            return AReferencingHelper.createEpsgForcedXYAxisOrder();
        } else {
            return AReferencingHelper.createEpsgStrictAxisOrder();
        }
    }
}
