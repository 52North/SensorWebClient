package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.out.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.FeaturesParameterService;

public class RestFeaturesAPIAdapter implements FeaturesParameterService {

	@Override
	public Feature[] getFeatures(int offset, int size) {
		List<Feature> allFeatures = new ArrayList<Feature>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allFeatures.addAll(converter.convertFeatures(lookup.getFeatures()));
		}
		return allFeatures.toArray(new Feature[0]);
	}

	@Override
	public Feature getFeature(String item) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			org.n52.shared.serializable.pojos.sos.Feature result = lookup.getFeature(item);
			if(result != null) {
				return converter.convertFeature(result);
			}
		}
		return null;
	}

}
