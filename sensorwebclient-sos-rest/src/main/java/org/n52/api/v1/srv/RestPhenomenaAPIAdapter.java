package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.out.Phenomenon;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.PhenomenaParameterService;

public class RestPhenomenaAPIAdapter implements PhenomenaParameterService {

	@Override
	public Phenomenon[] getPhenomena(int offset, int size) {
		List<Phenomenon> allPhenomenons = new ArrayList<Phenomenon>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allPhenomenons.addAll(converter.convertPhenomenon(lookup.getPhenomenons()));
		}
		return allPhenomenons.toArray(new Phenomenon[0]);
	}

	@Override
	public Phenomenon getPhenomenon(String item) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			org.n52.shared.serializable.pojos.sos.Phenomenon result = lookup.getPhenomenon(item);
			if(result != null) {
				return converter.convertPhenomenon(result);
			}
		}
		return null;
	}

}
