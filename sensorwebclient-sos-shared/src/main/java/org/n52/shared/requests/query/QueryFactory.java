/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.shared.requests.query;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.shared.requests.query.queries.FeatureQuery;
import org.n52.shared.requests.query.queries.OfferingQuery;
import org.n52.shared.requests.query.queries.PhenomenonQuery;
import org.n52.shared.requests.query.queries.ProcedureQuery;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.queries.StationQuery;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

/**
 * Factory to create particular request queries for one specific SOS instance.
 */
public class QueryFactory {

    private SOSMetadata metadata;

    /**
     * Creates a {@link QueryFactory} instance for one specific service instance.
     * 
     * @param metadata
     *        the service's metadata.
     */
    public QueryFactory(SOSMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     * @see QueryParameters#createEmptyFilterQuery()
     */
    public QueryRequest createAllProceduresQuery() {
        return createFilteredProcedureQuery(createEmptyFilterQuery());
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    public QueryRequest createFilteredProcedureQuery(QueryParameters parameters) {
        QueryRequest request = new ProcedureQuery(metadata.getServiceUrl(), parameters);
        return createQuery(parameters, request);
    }

    /**
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     * @see QueryParameters#createEmptyFilterQuery()
     */
    public QueryRequest createAllOfferingQuery() {
        return createFilteredOfferingQuery(createEmptyFilterQuery());
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    public QueryRequest createFilteredOfferingQuery(QueryParameters parameters) {
        QueryRequest request = new OfferingQuery(metadata.getServiceUrl(), parameters);
        return createQuery(parameters, request);
    }

    /**
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     * @see QueryParameters#createEmptyFilterQuery()
     */
    public QueryRequest createAllPhenomenonsQuery() {
        return createFilteredPhenomenonQuery(createEmptyFilterQuery());
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    public QueryRequest createFilteredPhenomenonQuery(QueryParameters parameters) {
        QueryRequest request = new PhenomenonQuery(metadata.getServiceUrl(), parameters);
        return createQuery(parameters, request);
    }

    /**
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     * @see QueryParameters#createEmptyFilterQuery()
     */
    public QueryRequest createAllFeaturesQuery() {
        return createFilteredFeaturesQuery(createEmptyFilterQuery());
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    public QueryRequest createFilteredFeaturesQuery(QueryParameters parameters) {
        QueryRequest request = new FeatureQuery(metadata.getServiceUrl(), parameters);
        return createQuery(parameters, request);
    }

    /**
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     * @see QueryParameters#createEmptyFilterQuery()
     */
    public QueryRequest createAllStationQuery() {
        return createFilteredStationQuery(createEmptyFilterQuery());
    }

    /**
     * @param parameters
     *        the query parameters to create the request with.
     * @return the query request ready to be used in a {@link #doQuery(QueryRequest)}.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    public QueryRequest createFilteredStationQuery(QueryParameters parameters) {
        QueryRequest request = new StationQuery(metadata.getServiceUrl(), parameters);
        return createQuery(parameters, request);
    }

    /**
     * Creates a {@link QueryRequest} from given {@link QueryParameters}.
     * 
     * @param parameters
     *        the query parameters to fill the template with.
     * @param request
     * @return the query request.
     * @throws ResourceNotFoundException
     *         if no service is configured with the given instance name.
     */
    private QueryRequest createQuery(QueryParameters parameters, QueryRequest request) {
        request.setOffset(parameters.getOffset());
        request.setPageSize(parameters.getPageSize());
        request.setQueryParameters(parameters);
        return request;
    }

}
