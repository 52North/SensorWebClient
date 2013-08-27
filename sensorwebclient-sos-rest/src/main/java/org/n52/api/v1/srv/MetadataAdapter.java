package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;
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
		// TODO Auto-generated method stub
		return 0;
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
