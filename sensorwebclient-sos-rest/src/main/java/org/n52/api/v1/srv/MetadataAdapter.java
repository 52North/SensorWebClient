package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.HashSet;
import java.util.Set;

import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.MetadataService;

public class MetadataAdapter implements MetadataService {

	@Override
	public int getServiceCount() {
		return getSOSMetadatas().size();
	}

	@Override
	public int getStationsCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getStations().size();
		}
		return count;
	}

	@Override
	public int getTimeseriesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			for (Station station : metadata.getStations()) {
				count += station.getObservedTimeseries().size();
			}
		}
		return count;
	}

	@Override
	public int getOfferingsCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getOfferings().size();
		}
		return count;
	}

	@Override
	public int getCategoriesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			Set<String> categorieSet = new HashSet<String>();
			SosTimeseries[] timeseries = metadata.getTimeseriesRelatedWith(QueryParameters.createEmptyFilterQuery());
			for (SosTimeseries timeserie : timeseries) {
				categorieSet.add(timeserie.getCategory());
			}
			count += categorieSet.size();
		}
		return count;
	}

	@Override
	public int getFeaturesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getFeatures().size();
		}
		return count;
	}

	@Override
	public int getProceduresCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getProcedures().size();
		}
		return count;
	}

	@Override
	public int getPhenomenaCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getPhenomenons().size();
		}
		return count;
	}

}
