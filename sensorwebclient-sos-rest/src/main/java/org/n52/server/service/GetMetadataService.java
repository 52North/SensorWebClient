package org.n52.server.service;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.QueryParameters;
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
 
	public QueryResponse<?> getPhenomenons(QueryParameters query, String instance) throws Exception {
		return queryService.doQuery(createQuery(query, instance, new PhenomenonQuery()));
	}

//	public QueryResponse<?> getProcedures(QuerySet query, String instance) throws Exception {
//		return queryService.doQuery(createQuery(query, instance, new ProcedureQuery()));
//	}
	
	public QueryResponse<?> getOfferings(QueryParameters query, String instance) throws Exception {
		return queryService.doQuery(createQuery(query, instance, new OfferingQuery()));
	}
	
	public QueryResponse<?> getFeatures(QueryParameters query, String instance) throws Exception {
		return queryService.doQuery(createQuery(query, instance, new FeatureQuery()));
	}
	
	public QueryResponse<?> getStations(QueryParameters query, String instance) throws Exception {
		return queryService.doQuery(createQuery(query, instance, new StationQuery()));
	}
	
	private QueryRequest createQuery(QueryParameters query, String instance, QueryRequest request) {
		SOSMetadata metadata = getServiceMetadata(instance);
		request.setServiceUrl(metadata.getServiceUrl());
		request.setOfferingFilter(query.getOfferings());
		request.setProcedureFilter(query.getProcedures());
        request.setPhenomenonFilter(query.getPhenomenons());
		request.setFeatureOfInterestFilter(query.getFeatureOfInterests());
        createSpatialFilter(query, request);
		request.setOffset(query.getOffset());
        request.setSize(query.getTotal());
		return request;
	}

    private SOSMetadata getServiceMetadata(String itemName) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(itemName);
        if (metadata == null) {
            LOGGER.warn("Could not find configured SOS instance for itemName '{}'" + itemName);
            throw new ResourceNotFoundException();
        }
        return metadata;
    }
    
    private void createSpatialFilter(QueryParameters query, QueryRequest request) {
        if (query.getSpatialFilter() != null) {
			Point lowerLeft = query.getSpatialFilter().getLowerLeft();
            Point upperRight = query.getSpatialFilter().getUpperRight();
			String srs = query.getSpatialFilter().getSrs();
            EastingNorthing ll = new EastingNorthing(lowerLeft.getEasting(), lowerLeft.getNorthing(), srs);
			EastingNorthing ur = new EastingNorthing(upperRight.getEasting(), upperRight.getNorthing(), srs);
			BoundingBox spatialFilter = new BoundingBox(ll, ur);
			request.setSpatialFilter(spatialFilter);
		}
    }

	public QueryService getQueryService() {
		return queryService;
	}

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

}
