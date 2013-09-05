package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.api.v1.io.CategoryConverter;
import org.n52.io.v1.data.search.CategorySearchResult;
import org.n52.io.v1.data.search.FeatureSearchResult;
import org.n52.io.v1.data.search.OfferingSearchResult;
import org.n52.io.v1.data.search.PhenomenonSearchResult;
import org.n52.io.v1.data.search.ProcedureSearchResult;
import org.n52.io.v1.data.search.SearchResult;
import org.n52.io.v1.data.search.ServiceSearchResult;
import org.n52.io.v1.data.search.StationSearchResult;
import org.n52.io.v1.data.search.TimeseriesSearchResult;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.SearchService;

public class SearchAdapter implements SearchService {

	@Override
	public Collection<SearchResult> searchResources(String search) {
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		// services
		results.addAll(requestServices(search));
		// stations
		results.addAll(requestStations(search));
		// timeseries
		results.addAll(requestTimeseries(search));
		// timeseries parameter (offerings, features, procedures, phenomena, categories);
		results.addAll(requestTSParameters(search));
		return results;
	}

	private Collection<SearchResult> requestTimeseries(String search) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			SosTimeseries[] timeseries = metadata.getTimeseriesRelatedWith(QueryParameters.createEmptyFilterQuery());
			for (SosTimeseries ts : timeseries) {
				if (containsSearchString(ts.getFeature().getLabel(), search) ||
					containsSearchString(ts.getPhenomenon().getLabel(), search) ||
					containsSearchString(ts.getProcedure().getLabel(), search) ||
					containsSearchString(ts.getOffering().getLabel(), search) ||
					containsSearchString(ts.getCategory(), search)) {
					results.add(new TimeseriesSearchResult(ts.getTimeseriesId(), ts.getLabel()));
				}
			}
		}
		return results;
	}

	private Collection<SearchResult> requestStations(String search) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			for (Station station : metadata.getStations()) {
				if(containsSearchString(station.getLabel(), search)) {
					results.add(new StationSearchResult(station.getGlobalId(), station.getLabel()));
				}
			}
		}
		return results;
	}

	private Collection<SearchResult> requestTSParameters(String search) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			// offerings
			for (Offering offering : lookup.getOfferings()) {
				if (containsSearchString(offering.getLabel(), search)) {
					results.add(new OfferingSearchResult(offering.getGlobalId(), offering.getLabel()));
				}
			}
			// features
			for (Feature feature : lookup.getFeatures()) {
				if (containsSearchString(feature.getLabel(), search)) {
					results.add(new FeatureSearchResult(feature.getGlobalId(), feature.getLabel()));
				}
			}
			// procedures
			for (Procedure procedure : lookup.getProcedures()) {
				if (containsSearchString(procedure.getLabel(), search)) {
					results.add(new ProcedureSearchResult(procedure.getGlobalId(), procedure.getLabel()));
				}
			}
			// phenomena
			for (Phenomenon phenomenon : lookup.getPhenomenons()) {
				if (containsSearchString(phenomenon.getLabel(), search)) {
					results.add(new PhenomenonSearchResult(phenomenon.getGlobalId(), phenomenon.getLabel()));
				}
			}
			// categories
			SosTimeseries[] timeseries = metadata.getTimeseriesRelatedWith(QueryParameters.createEmptyFilterQuery());
			for (SosTimeseries sosTimeseries : timeseries) {
				if (containsSearchString(sosTimeseries.getCategory(), search)) {
					CategoryConverter converter = new CategoryConverter(metadata);
					String generateCategoryId = converter.generateId(sosTimeseries.getCategory());
					results.add(new CategorySearchResult(generateCategoryId, sosTimeseries.getCategory()));
				}
			}
		}
		return results;
	}

	private Collection<SearchResult> requestServices(String search) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			if (containsSearchString(metadata.getTitle(), search)) {
				results.add(new ServiceSearchResult(metadata.getGlobalId(), metadata.getTitle()));
			}
		}
		return results;
	}
	
	/**
	 * This method returns true it the <code>scrutinizedString</code> contains the <code>searchString</code> and ignores the case 
	 * @param scrutinizedString
	 * @param searchString
	 * @return 
	 */
	private boolean containsSearchString(String scrutinizedString, String searchString) {
		return scrutinizedString.toLowerCase().contains(searchString.toLowerCase());
	}

}
