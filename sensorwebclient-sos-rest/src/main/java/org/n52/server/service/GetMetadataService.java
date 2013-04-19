package org.n52.server.service;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.QuerySet;
import org.n52.server.service.rest.control.ResourceNotFoundException;
import org.n52.server.service.rest.model.Point;
import org.n52.shared.requests.query.FeatureQuery;
import org.n52.shared.requests.query.OfferingQuery;
import org.n52.shared.requests.query.PhenomenonQuery;
import org.n52.shared.requests.query.ProcedureQuery;
import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.requests.query.StationQuery;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetMetadataService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetMetadataService.class);
	
	private QueryService queryService;
 
	public QueryResponse getPhenomenons(QuerySet query, String instance) throws Exception {
		PhenomenonQuery request = (PhenomenonQuery) createQuery(query, instance, new PhenomenonQuery()); 
		return queryService.doQuery(request);
	}

	public QueryResponse getProcedures(QuerySet query, String instance) throws Exception {
		ProcedureQuery request = (ProcedureQuery) createQuery(query, instance, new ProcedureQuery()); 
		return queryService.doQuery(request);
	}
	
	public QueryResponse getOfferings(QuerySet query, String instance) throws Exception {
		return queryService.doQuery(createQuery(query, instance, new OfferingQuery()));
	}
	
	public QueryResponse getFeatures(QuerySet query, String instance) throws Exception {
		FeatureQuery request = (FeatureQuery) createQuery(query, instance, new FeatureQuery()); 
		return queryService.doQuery(request);
	}
	
	public QueryResponse getStations(QuerySet query, String instance) throws Exception {
		StationQuery request = (StationQuery) createQuery(query, instance, new StationQuery()); 
		return queryService.doQuery(request);
	}
	
	private QueryRequest createQuery(QuerySet query, String instance, QueryRequest request) {
		SOSMetadata metadata = getServiceMetadata(instance);
		request.setServiceUrl(metadata.getServiceUrl());
		request.setOfferingFilter(query.getOfferings());
		request.setProcedureFilter(query.getProcedures());
		request.setFeatureOfInterestFilter(query.getFeatureOfInterests());
		request.setPhenomenonFilter(query.getPhenomenonS());
		request.setSize(query.getTotal());
		request.setOffset(query.getOffset());
		if (query.getSpatialFilter() != null) {
			Point lowerLeft = query.getSpatialFilter().getLowerLeft();
			EastingNorthing ll = new EastingNorthing(lowerLeft.getEasting(), lowerLeft.getNorthing(), query.getSpatialFilter().getSrs());
			Point upperRight = query.getSpatialFilter().getUpperRight();
			EastingNorthing ur = new EastingNorthing(upperRight.getEasting(), upperRight.getNorthing(), query.getSpatialFilter().getSrs());
			BoundingBox spatialFilter = new BoundingBox(ll, ur);
			request.setSpatialFilter(spatialFilter);
		}
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
