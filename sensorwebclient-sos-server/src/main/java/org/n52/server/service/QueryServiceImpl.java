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
import java.util.List;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.shared.exceptions.ServiceOccupiedException;
import org.n52.shared.requests.query.FeatureQuery;
import org.n52.shared.requests.query.OfferingQuery;
import org.n52.shared.requests.query.PageResult;
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
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryServiceImpl implements QueryService {

    private static final Logger LOG = LoggerFactory.getLogger(QueryServiceImpl.class);

	@Override
	public QueryResponse<?> doQuery(QueryRequest request)
			throws Exception {
		// TODO refactor
		if (request instanceof FeatureQuery) {
			return getFeatureResponse((FeatureQuery) request);
		} else if (request instanceof PhenomenonQuery) {
			return getPhenomenons((PhenomenonQuery) request);
		} else if (request instanceof ProcedureQuery) {
			return getProcedure((ProcedureQuery) request);
		} else if (request instanceof OfferingQuery) {
			return getOffering((OfferingQuery) request);
		} else if (request instanceof StationQuery) { 
			return getStations((StationQuery) request);
		}
		return null;
	}
	
    private QueryResponse<?> getStations(StationQuery query) throws Exception {
        try {
        	// check if update process is still running
        	if (ConfigurationContext.UPDATE_TASK_RUNNING) {
				LOG.info("Update running, no service available currently.");
				String reason = "Update running, currently no service available, please try again later";
				throw new ServiceOccupiedException(reason);
			}
        	String serviceUrl = query.getServiceUrl();
        	SOSMetadata metadata = ConfigurationContext.getSOSMetadata(serviceUrl);
            ArrayList<Station> stations = (ArrayList<Station>) metadata.getStations();
            Collection<String> offeringFilter = query.getOfferingFilter();
            Collection<String> phenomenonFilter = query.getPhenomenonFilter();
            Collection<String> procedureFilter = query.getProcedureFilter();
            Collection<String> featureFilter = query.getFeatureOfInterestFilter();
        	int startIndex = getStartIndex(query.getOffset());
        	int interval = getInterval(query.getSize(), stations.size());
        	BoundingBox spatialFilter = query.getSpatialFilter();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getStations(sosUrl: %s, offeringID: %s, procedureID: %s, phenomenonID: %s, featureID: %s, Start: %s, Interval: %s, Spatial: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl, offeringFilter, procedureFilter, phenomenonFilter, featureFilter, startIndex, interval, spatialFilter));
            }
            
            boolean shallForceXYAxisOrder = metadata.isForceXYAxisOrder();
            AReferencingHelper referencing = createReferenceHelper(shallForceXYAxisOrder);
            
            int endIndex = 0;
            Station[] finalStations = new Station[interval];
            for(int i = startIndex; i < stations.size() && endIndex < interval; i++) {
            	Station station = stations.get(i).clone();
                if (spatialFilter == null || referencing.isStationContainedByBBox(spatialFilter, station)) {
                	station.removeUnmatchedConstellations(offeringFilter, phenomenonFilter, procedureFilter, featureFilter);
                	if(station.hasAtLeastOneParameterconstellation()) {
                		finalStations[endIndex++] = station;
                	}
                }
            }
            
            StationQueryResponse response = new StationQueryResponse();
            response.setServiceUrl(serviceUrl);
            response.setResultSubset(new PageResult<Station>(startIndex, stations.size(), finalStations));
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
	
	private boolean isFinished(int endIndex, Station[] stations) {
        boolean devMode = ConfigurationContext.IS_DEV_MODE;
        return devMode || endIndex >= stations.length;
    }

	private QueryResponse<?> getOffering(OfferingQuery query) throws Exception {
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
            if (offeringFilter == null || offeringFilter.size() == 0) {
            	response.setOffering(meta.getOfferings().toArray(new Offering[0]));
            } else {
                List<Offering> offerings = new ArrayList<Offering>();
            	for (String offering : offeringFilter) {
    				offerings.add(meta.getOffering(offering));
    			}
            	response.setOffering(offerings.toArray(new Offering[0]));
            }
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

	
	private QueryResponse<?> getProcedure(ProcedureQuery query) throws Exception {
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
            if (procedureFilter == null || procedureFilter.size() == 0) {
            	response.setProcedure(meta.getProcedures().toArray(new Procedure[0]));
            } else {
                List<Procedure> procedures = new ArrayList<Procedure>();
            	for (String procedure : procedureFilter) {
    				procedures.add(meta.getProcedure(procedure));
    			}
            	response.setProcedure(procedures.toArray(new Procedure[0]));
            }
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
	
	private QueryResponse<?> getPhenomenons(PhenomenonQuery query) throws Exception {
		try {
			String serviceUrl = query.getServiceUrl();
			Collection<String> phenomenonFilter = query.getPhenomenonFilter();
            if (LOG.isDebugEnabled()) {
                String msgTemplate = "Request -> getPhen4SOS(sosUrl: %s)";
                LOG.debug(String.format(msgTemplate, serviceUrl));
            }
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
            PhenomenonQueryResponse response = new PhenomenonQueryResponse();
            if (phenomenonFilter == null || phenomenonFilter.size() == 0) {
            	response.setPhenomenons(meta.getPhenomenons().toArray(new Phenomenon[0]));
            } else {
                List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
            	for (String phenomenon : phenomenonFilter) {
            	    phenomenons.add(meta.getPhenomenon(phenomenon));
    			}
            	response.setPhenomenons(phenomenons.toArray(new Phenomenon[0]));
            }
            response.setServiceUrl(serviceUrl);
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
	}
	
	private QueryResponse<?> getFeatureResponse(FeatureQuery query) throws Exception {
	    try {
	    	String serviceUrl = query.getServiceUrl();
	    	Collection<String> featureFilter = query.getFeatureOfInterestFilter();
	        if (LOG.isDebugEnabled()) {
	            String msgTemplate = "Request -> getFeature(sosUrl: %s, featureID: %s)";
	            LOG.debug(String.format(msgTemplate, serviceUrl, featureFilter));
	        }
	        SOSMetadata meta = ConfigurationContext.getSOSMetadata(serviceUrl);
	        FeatureQueryResponse response = new FeatureQueryResponse();
	        if (featureFilter == null || featureFilter.size() == 0) {
	        	response.setFeatures(meta.getFeatures().toArray(new FeatureOfInterest[0]));
	        } else {
	            List<FeatureOfInterest> fois = new ArrayList<FeatureOfInterest>();
	        	for (String feature : featureFilter) {
					fois.add(meta.getFeature(feature));
				}
	        	response.setFeatures(fois.toArray(new FeatureOfInterest[0]));
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
