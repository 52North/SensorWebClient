package org.n52.server.service;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.QuerySet;
import org.n52.server.service.rest.control.ResourceNotFoundException;
import org.n52.shared.requests.query.FeatureQuery;
import org.n52.shared.requests.query.OfferingQuery;
import org.n52.shared.requests.query.PhenomenonQuery;
import org.n52.shared.requests.query.ProcedureQuery;
import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.requests.query.StationQuery;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMetadataService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetMetadataService.class);
	
	private QueryService queryService;

	public Object getPhenomenons(QuerySet query, String instance) throws Exception {
		PhenomenonQuery request = (PhenomenonQuery) createQuery(query, instance, new PhenomenonQuery()); 
		return queryService.doQuery(request);
	}

	public Object getProcedures(QuerySet query, String instance) throws Exception {
		ProcedureQuery request = (ProcedureQuery) createQuery(query, instance, new ProcedureQuery()); 
		return queryService.doQuery(request);
	}
	
	public Object getOfferings(QuerySet query, String instance) throws Exception {
		OfferingQuery request = (OfferingQuery) createQuery(query, instance, new OfferingQuery()); 
		return queryService.doQuery(request);
	}
	
	public Object getFeatures(QuerySet query, String instance) throws Exception {
		FeatureQuery request = (FeatureQuery) createQuery(query, instance, new FeatureQuery()); 
		return queryService.doQuery(request);
	}
	
	public Object getStations(QuerySet query, String instance) throws Exception {
		StationQuery request = (StationQuery) createQuery(query, instance, new StationQuery()); 
		return queryService.doQuery(request);
	}
	
	private QueryRequest createQuery(QuerySet query, String instance, QueryRequest request) {
		SOSMetadata metadata = getServiceMetadata(instance);
		request.setServiceUrl(metadata.getServiceUrl());
		request.setOfferingFilter(query.getOfferingFilter());
		request.setProcedureFilter(query.getProcedureFilter());
		request.setFeatureOfInterestFilter(query.getFeatureOfInterestFilter());
		request.setPhenomenonFilter(query.getPhenomenonFilter());
		request.setPagingInterval(query.getPagingInterval());
		request.setPagingStartIndex(query.getPagingStartIndex());
//		request.setSpatialFilter(query.getSpatialFilter());
		return request;
	}

	protected SOSMetadata getServiceMetadata(String instance) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(instance);
        if (metadata == null) {
            LOGGER.warn("Could not find configured SOS instance for itemName '{}'" + instance);
            throw new ResourceNotFoundException();
        }
        return metadata;
    }
	
	public QueryService getQueryService() {
		return queryService;
	}

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

}
