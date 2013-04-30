
package org.n52.server.service.rest.control;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class TimeseriesParameterQueryController {

    private QueryService queryService;

    /**
     * Creates a query factory for the given service instance.
     * 
     * @param serviceInstance
     *        the service instance to create queries for.
     * @return a query factory for a given service instance to create queries for.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given item name.
     */
    public QueryFactory getQueryFactoryFor(String serviceInstance) {
        return new QueryFactory(findServiceMetadataForItemName(serviceInstance));
    }

    /**
     * @param serviceInstance
     *        the item name of the configured service instance.
     * @return returns the metadata associated to the service instance.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given item name.
     */
    private SOSMetadata findServiceMetadataForItemName(String serviceInstance) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(serviceInstance);
        if (metadata == null) {
            throw new ResourceNotFoundException();
        }
        return metadata;
    }
    
    protected abstract QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception;

    protected QueryResponse< ? > doQuery(QueryRequest queryRequest) throws Exception {
        return queryService.doQuery(queryRequest);
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }

}
