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

package org.n52.api.v0.ctrl;

import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.n52.api.v0.srv.ServiceInstancesService;
import org.n52.client.service.QueryService;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.ResourceNotFoundException;

public abstract class QueryController {

    private QueryService queryService;
    
    private ServiceInstancesService serviceInstancesService;
    
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
    	SOSMetadata metadata = serviceInstancesService.getSOSMetadataForItemName(serviceInstance);
        if (metadata == null) {
            throw new ResourceNotFoundException();
        }
        return metadata;
    }
    
    protected boolean containsServiceInstance(String serviceInstance) {
    	return serviceInstancesService.containsServiceInstance(serviceInstance);
    }
    
    protected abstract QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception;

    protected QueryResponse< ? > doQuery(QueryRequest queryRequest) throws Exception {
        return queryService.doQuery(queryRequest);
    }

    /**
     * Determines the decoded identifier of an individuum of the given collection.
     * 
     * @param collectionName
     *        the name of the collection (e.g. <code>features</code>).
     * @param request
     *        the incoming request.
     * @return the individuum identifier.
     * @throws UnsupportedEncodingException
     *         if encoding is not supported.
     * @see RestfulUrls
     */
    protected String getDecodedIndividuumIdentifierFor(String collectionName, HttpServletRequest request) throws UnsupportedEncodingException {
        String path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return decode(path.substring(path.indexOf(collectionName) + collectionName.length() + 1));
    }

    /**
     * Decodes given URL encoded string. Assumes and uses UTF-8 encoding.
     * 
     * @param toDecode
     *        the string to decode.
     * @return a decoded version of the input.
     * @throws UnsupportedEncodingException if encoding is not supported.
     */
    protected String decode(String toDecode) throws UnsupportedEncodingException {
        return URLDecoder.decode(toDecode, "UTF-8");
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

	public ServiceInstancesService getServiceInstancesService() {
		return serviceInstancesService;
	}

	public void setServiceInstancesService(
			ServiceInstancesService serviceInstancesService) {
		this.serviceInstancesService = serviceInstancesService;
	}

}
