
package org.n52.server.service.rest.control;

import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

import javax.servlet.http.HttpServletRequest;

import org.n52.client.service.QueryService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class QueryController {

    private QueryService queryService;

    /**
     * @param kvpDetailsValue
     *        the value of KVP parameter <code>details</code>.
     * @return <code>true</code> if parameter indicates to create a full expanded list of objects,
     *         <code>false</code> if condensed output shall be generated (default).
     */
    protected boolean shallShowCompleteResults(String kvpDetailsValue) {
        return kvpDetailsValue != null && "complete".equalsIgnoreCase(kvpDetailsValue);
    }

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
    protected SOSMetadata findServiceMetadataForItemName(String serviceInstance) {
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

    /**
     * Determines the identifier of an individuum of the given collection. Instead of
     * 
     * @param collectionName
     *        the name of the collection (e.g. <code>features</code>).
     * @param request
     *        the incoming request.
     * @return the individuum identifier.
     * @see RestfulUrls
     */
    protected String getIndididuumIdentifierFor(String collectionName, HttpServletRequest request) {
        String path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return path.substring(path.indexOf(collectionName) + collectionName.length() + 1);
    }

    /**
     * Strips the file extension from the given parameter. The file extension can only be stripped if known.
     * Currently considered extensions are:
     * <ul>
     * <li><code>.json</code></li>
     * <li><code>.html</code></li>
     * </ul>
     * 
     * 
     * @param toStrip
     *        to strip the file extension from.
     * @return a stripped version of the given parameter (without file extension).
     */
    protected String stripKnownFileExtensionFrom(String toStrip) {
        if (toStrip.lastIndexOf(".json") > 0) {
            return toStrip.substring(0, toStrip.lastIndexOf(".json"));
        }
        else if (toStrip.lastIndexOf(".html") > 0) {
            return toStrip.substring(0, toStrip.lastIndexOf(".html"));
        }
        return toStrip;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }

}
