/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;
import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import java.util.ArrayList;
import java.util.Collection;
import org.n52.sensorweb.spi.SearchResult;
import org.n52.sensorweb.spi.SearchService;
import org.n52.sensorweb.spi.search.CategorySearchResult;
import org.n52.sensorweb.spi.search.FeatureSearchResult;
import org.n52.sensorweb.spi.search.PhenomenonSearchResult;
import org.n52.sensorweb.spi.search.ProcedureSearchResult;
import org.n52.sensorweb.spi.search.ServiceSearchResult;
import org.n52.sensorweb.spi.search.v1.OfferingSearchResult;
import org.n52.sensorweb.spi.search.v1.StationSearchResult;
import org.n52.sensorweb.spi.search.v1.TimeseriesSearchResult;

import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class SearchAdapter implements SearchService {

    @Override
    public Collection<SearchResult> searchResources(String search, String locale) {
        // language specific search is not supported by the aggregation component
        return searchResources(search);
    }

    private Collection<SearchResult> searchResources(String search) {

        // TODO extend search logic to support composed search strings

        String[] searchTerms = search.split(",");
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        if (searchTerms.length <= 1) {
            results.addAll(requestServices(searchTerms));
            results.addAll(requestStations(searchTerms));
            results.addAll(requestTSParameters(searchTerms));
        }
        results.addAll(requestTimeseries(searchTerms));
        return results;
    }

    private Collection<SearchResult> requestTimeseries(String... searchTerms) {
        Collection<SearchResult> results = new ArrayList<SearchResult>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            SosTimeseries[] timeseries = metadata.getMatchingTimeseries(createEmptyFilterQuery());
            for (SosTimeseries ts : timeseries) {
                boolean matchTerms = true;
                for (String searchTerm : searchTerms) {
                    if (!(matchAnyId(ts, searchTerm) || matchAnyLabel(ts, searchTerm) ||
                          containsSearchString(ts.getServiceUrl(), searchTerm))) {
                        matchTerms = false;
                    }
                }
                if (matchTerms) {
                    results.add(new TimeseriesSearchResult(ts.getTimeseriesId(), ts.getLabel()));
                }
            }
        }
        return results;
    }

    private boolean matchAnyLabel(SosTimeseries ts, String searchTerm) {
        return containsSearchString(ts.getFeature().getLabel(), searchTerm)
                || containsSearchString(ts.getPhenomenon().getLabel(), searchTerm)
                || containsSearchString(ts.getProcedure().getLabel(), searchTerm)
                || containsSearchString(ts.getOffering().getLabel(), searchTerm)
                || containsSearchString(ts.getCategory().getLabel(), searchTerm);
    }

    private boolean matchAnyId(SosTimeseries ts, String searchTerm) {
        return containsSearchString(ts.getFeature().getFeatureId(), searchTerm)
                || containsSearchString(ts.getPhenomenon().getPhenomenonId(), searchTerm)
                || containsSearchString(ts.getProcedure().getProcedureId(), searchTerm)
                || containsSearchString(ts.getOffering().getOfferingId(), searchTerm);
    }

    private Collection<SearchResult> requestStations(String... searchTerms) {
        Collection<SearchResult> results = new ArrayList<SearchResult>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            for (Station station : metadata.getStations()) {
                for (String searchTerm : searchTerms) {
                    if (containsSearchString(station.getLabel(), searchTerm)) {
                        results.add(new StationSearchResult(station.getGlobalId(), station.getLabel()));
                    }
                }
            }
        }
        return results;
    }

    private Collection<SearchResult> requestTSParameters(String... searchTerms) {
        Collection<SearchResult> results = new ArrayList<SearchResult>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            for (String searchTerm : searchTerms) {
                TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
                // offerings
                for (Offering offering : lookup.getOfferings()) {
                    if (containsSearchString(offering.getLabel(), searchTerm)) {
                        results.add(new OfferingSearchResult(offering.getGlobalId(), offering.getLabel()));
                    }
                }
                // features
                for (Feature feature : lookup.getFeatures()) {
                    if (containsSearchString(feature.getLabel(), searchTerm)) {
                        results.add(new FeatureSearchResult(feature.getGlobalId(), feature.getLabel()));
                    }
                }
                // procedures
                for (Procedure procedure : lookup.getProcedures()) {
                    if (containsSearchString(procedure.getLabel(), searchTerm)) {
                        results.add(new ProcedureSearchResult(procedure.getGlobalId(), procedure.getLabel()));
                    }
                }
                // phenomena
                for (Phenomenon phenomenon : lookup.getPhenomenons()) {
                    if (containsSearchString(phenomenon.getLabel(), searchTerm)) {
                        results.add(new PhenomenonSearchResult(phenomenon.getGlobalId(), phenomenon.getLabel()));
                    }
                }
                // categories
                SosTimeseries[] timeseries = metadata.getMatchingTimeseries(createEmptyFilterQuery());
                for (SosTimeseries sosTimeseries : timeseries) {
                    if (containsSearchString(sosTimeseries.getCategory().getLabel(), searchTerm)) {
                        results.add(new CategorySearchResult(sosTimeseries.getCategory().getGlobalId(), sosTimeseries.getCategory().getLabel()));
                    }
                }
            }
        }
        return results;
    }

    private Collection<SearchResult> requestServices(String... searchTerms) {
        Collection<SearchResult> results = new ArrayList<SearchResult>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            for (String searchTerm : searchTerms) {
                if (containsSearchString(metadata.getTitle(), searchTerm)) {
                    results.add(new ServiceSearchResult(metadata.getGlobalId(), metadata.getTitle()));
                }
            }
        }
        return results;
    }

    /**
     * @param label
     *        the label to check.
     * @param searchToken
     *        the input search token.
     * @return <code>true</code> if the <code>label</code> contains the <code>searchToken</code> and ignores
     *         the case.
     */
    private boolean containsSearchString(String label, String searchToken) {
        return label.toLowerCase().contains(searchToken.toLowerCase());
    }

    @Override
    public void shutdown() {
        // nothing to shutdown
    }

}
