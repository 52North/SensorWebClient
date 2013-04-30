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

import static org.n52.server.oxf.util.ConfigurationContext.UPDATE_TASK_RUNNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.shared.exceptions.ServiceOccupiedException;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.FeatureQuery;
import org.n52.shared.requests.query.queries.OfferingQuery;
import org.n52.shared.requests.query.queries.PhenomenonQuery;
import org.n52.shared.requests.query.queries.ProcedureQuery;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.queries.StationQuery;
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
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryServiceImpl implements QueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceImpl.class);

	@Override
	public QueryResponse<?> doQuery(QueryRequest request) throws Exception {
		// TODO refactor
		if (request instanceof FeatureQuery) {
			return getFeatures((FeatureQuery) request);
		} else if (request instanceof PhenomenonQuery) {
			return getPhenomenons((PhenomenonQuery) request);
		} else if (request instanceof ProcedureQuery) {
			return getProcedures((ProcedureQuery) request);
		} else if (request instanceof OfferingQuery) {
			return getOfferings((OfferingQuery) request);
		} else if (request instanceof StationQuery) { 
			return getStations((StationQuery) request);
		}
		return null;
	}
	
    private QueryResponse<?> getStations(StationQuery query) throws Exception {
        try {
        	if (UPDATE_TASK_RUNNING) {
				LOGGER.info("Update running, no service available currently.");
				String reason = "Update running, currently no service available, please try again later";
				throw new ServiceOccupiedException(reason);
			}
        	String serviceUrl = query.getServiceUrl();
        	SOSMetadata metadata = ConfigurationContext.getSOSMetadata(serviceUrl);
            ArrayList<Station> stations = (ArrayList<Station>) metadata.getStations();
            
            QueryParameters parameters = query.getQueryParameters();
            LOGGER.debug("Request -> getStations(sosUrl: {}, filter: {})", serviceUrl, parameters);
            
            BoundingBox spatialFilter = parameters.getSpatialFilter();
            boolean shallForceXYAxisOrder = metadata.isForceXYAxisOrder();
            AReferencingHelper referencing = createReferenceHelper(shallForceXYAxisOrder);
            
            int currentPageIndex = 0;
            int offset = query.getOffset();
            int pageSize = query.getPageSize();
            
            if (offset == 0 && pageSize == 0) {
                // when query is done from server side without paging
                List<Station> filteredStations = new ArrayList<Station>();
                for (Station station : stations) {
                    if (spatialFilter == null || referencing.isStationContainedByBBox(spatialFilter, station)) {
                    	if (parameters.getStation() == null || station.getId().equals(parameters.getStation())) {
                    		station = cloneAndMatchAgainstQuery(station, parameters);
                            if(station.hasAtLeastOneParameterConstellation()) {
                                filteredStations.add(station);
                            }
                    	}
                    }
                }
                Station[] finalStations = filteredStations.toArray(new Station[0]);
                return new StationQueryResponse(serviceUrl, finalStations);
            } else {
                Station[] finalStations = new Station[pageSize];
                for(int i = offset; i < stations.size() && currentPageIndex < pageSize; i++) {
                    Station station = stations.get(i);
                    if (spatialFilter == null || referencing.isStationContainedByBBox(spatialFilter, station)) {
                        station = cloneAndMatchAgainstQuery(station, parameters);
                        if(station.hasAtLeastOneParameterConstellation()) {
                            finalStations[currentPageIndex++] = station;
                        }
                    }
                }
                Station[] pagedStations = Arrays.copyOfRange(finalStations, offset, pageSize);
                return new StationQueryResponse(serviceUrl, pagedStations);
            }
            
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

	private Station cloneAndMatchAgainstQuery(Station station, QueryParameters parameters) {
	    Station clonedStation = station.clone();
	    String offering = parameters.getOffering();
        String procedure = parameters.getProcedure();
        String phenomenon = parameters.getPhenomenon();
        String feature = parameters.getFeature();
        clonedStation.removeUnmatchedConstellations(offering, phenomenon, procedure, feature);
        return clonedStation;
    }

    private QueryResponse<?> getOfferings(OfferingQuery query) throws Exception {
        try {
        	String serviceUrl = query.getServiceUrl();
        	QueryParameters parameters = query.getQueryParameters();
            LOGGER.debug("Request -> getOfferings(sosUrl: {}, filter: {})", serviceUrl, parameters);
            return queryOfferings(serviceUrl, parameters);
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private OfferingQueryResponse queryOfferings(String serviceUrl, QueryParameters parameters) {
        TimeseriesParametersLookup lookup = getParametersLookupFor(serviceUrl);
        OfferingQueryResponse response = new OfferingQueryResponse(serviceUrl);
        if ( !parameters.hasParameterFilter()) {
        	response.setResults(lookup.getOfferingsAsArray());
        } else {
        	String offeringId = parameters.getOffering();
            if (lookup.containsOffering(offeringId)) {
                Offering offering = lookup.getOffering(offeringId);
                response.setResults(new Offering[] { offering });
            }
        }
        
        if(parameters.hasSpatialFilter()) {
            
            // TODO apply spatial filter
            
        } 
        
        return response;
    }

	
	private QueryResponse<?> getProcedures(ProcedureQuery query) throws Exception {
        try {
        	String serviceUrl = query.getServiceUrl();
            QueryParameters parameters = query.getQueryParameters();
            LOGGER.debug("Request -> getProcedures(sosUrl: {}, filter: {})", serviceUrl, parameters);
            return queryProcedures(serviceUrl, parameters);
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private QueryResponse<Procedure> queryProcedures(String serviceUrl, QueryParameters parameters) {
        QueryResponse<Procedure> response = new ProcedureQueryResponse(serviceUrl);
        TimeseriesParametersLookup lookup = getParametersLookupFor(serviceUrl);
        
        if ( !parameters.hasParameterFilter()) {
            response.setResults(lookup.getProceduresAsArray());
        } else {
            String procedureId = parameters.getProcedure();
            if (lookup.containsProcedure(procedureId)) {
                Procedure procedure = lookup.getProcedure(procedureId);
                response.setResults(new Procedure[] { procedure });
            }
        }
        
        if(parameters.hasSpatialFilter()) {
            
            // TODO apply spatial filter
            
        } 
        
        return response;
    }
	
	private QueryResponse<?> getPhenomenons(QueryRequest query) throws Exception {
		try {
			String serviceUrl = query.getServiceUrl();
            QueryParameters parameters = query.getQueryParameters();
            LOGGER.debug("Request -> getPhenomenons(sosUrl: {}, filter: {})", serviceUrl, parameters);
            return queryPhenomenons(serviceUrl, parameters);
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
	}

    private PhenomenonQueryResponse queryPhenomenons(String serviceUrl, QueryParameters parameters) {
        PhenomenonQueryResponse response = new PhenomenonQueryResponse(serviceUrl);
        TimeseriesParametersLookup lookup = getParametersLookupFor(serviceUrl);

        if ( !parameters.hasParameterFilter()) {
            response.setResults(lookup.getPhenomenonsAsArray());
        } else {
            String phenomenonId = parameters.getPhenomenon();
            if (lookup.containsPhenomenon(phenomenonId)) {
                Phenomenon phenomenon = lookup.getPhenomenon(phenomenonId);
                response.setResults(new Phenomenon[] { phenomenon });
            }
        }
        
        if(parameters.hasSpatialFilter()) {
            
            // TODO apply spatial filter
            
        } 
        
        return response;
    }
	
	private QueryResponse<?> getFeatures(FeatureQuery query) throws Exception {
	    try {
	    	String serviceUrl = query.getServiceUrl();
            QueryParameters parameters = query.getQueryParameters();
            LOGGER.debug("Request -> getFeatures(sosUrl: {}, filter: {})", serviceUrl, parameters);
	        return queryFeatures(serviceUrl, parameters);
	    } catch (Exception e) {
	        LOGGER.error("Exception occured on server side.", e);
	        throw e; // last chance to log on server side
	    }
	}

    private FeatureQueryResponse queryFeatures(String serviceUrl, QueryParameters parameters) {
        TimeseriesParametersLookup lookup = getParametersLookupFor(serviceUrl);
        FeatureQueryResponse response = new FeatureQueryResponse(serviceUrl);
        

        if ( !parameters.hasParameterFilter()) {
            response.setResults(lookup.getFeaturesAsArray());
        } else {
            String featureId = parameters.getFeature();
            if (lookup.containsFeature(featureId)) {
                FeatureOfInterest feature = lookup.getFeature(featureId);
                response.setResults(new FeatureOfInterest[] { feature });
            }
        }
        
        if(parameters.hasSpatialFilter()) {
            
            // TODO apply spatial filter
            
        } 
        
        return response;
    }

    private TimeseriesParametersLookup getParametersLookupFor(String serviceUrl) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadata(serviceUrl);
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParamtersLookup();
        return lookup;
    }
	
    private AReferencingHelper createReferenceHelper(boolean forceXYAxisOrder) {
        if (forceXYAxisOrder) {
            return AReferencingHelper.createEpsgForcedXYAxisOrder();
        } else {
            return AReferencingHelper.createEpsgStrictAxisOrder();
        }
    }
}
