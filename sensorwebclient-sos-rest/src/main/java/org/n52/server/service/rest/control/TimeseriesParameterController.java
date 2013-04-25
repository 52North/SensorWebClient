package org.n52.server.service.rest.control;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.QueryParameters;
import org.n52.server.service.rest.model.Point;
import org.n52.shared.requests.query.FeatureQuery;
import org.n52.shared.requests.query.OfferingQuery;
import org.n52.shared.requests.query.PhenomenonQuery;
import org.n52.shared.requests.query.ProcedureQuery;
import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class TimeseriesParameterController {
    
    private QueryService queryService;
    
    protected QueryRequest createOfferingQuery(QueryParameters parameters, String instance) {
        return createQuery(parameters, instance, new OfferingQuery());
    }
    
    protected QueryRequest createProcedureQuery(QueryParameters parameters, String instance) {
        return createQuery(parameters, instance, new ProcedureQuery());
    }
    
    protected QueryRequest createPhenomenonQuery(QueryParameters parameters, String instance) {
        return createQuery(parameters, instance, new PhenomenonQuery());
    }
    
    protected QueryRequest createFeatureQuery(QueryParameters parameters, String instance) {
        return createQuery(parameters, instance, new FeatureQuery());
    }

    private QueryRequest createQuery(QueryParameters parameters, String instance, QueryRequest request) {
        SOSMetadata metadata = getServiceMetadata(instance);
        request.setServiceUrl(metadata.getServiceUrl());
        request.setOfferingFilter(parameters.getOfferings());
        request.setProcedureFilter(parameters.getProcedures());
        request.setPhenomenonFilter(parameters.getPhenomenons());
        request.setFeatureOfInterestFilter(parameters.getFeatureOfInterests());
        createSpatialFilter(parameters, request);
//        request.setOffset(parameters.getOffset());
//        request.setSize(parameters.getTotal());
        return request;
    }

    private SOSMetadata getServiceMetadata(String itemName) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(itemName);
        if (metadata == null) {
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

    protected QueryResponse<?> doQuery(QueryRequest queryRequest) throws Exception {
        return queryService.doQuery(queryRequest);
    }
    
    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }
    
    

}
