package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getServiceMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.out.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.OfferingsParameterService;

public class RestOfferingsAPIAdapter implements OfferingsParameterService {

	@Override
	public Offering[] getOfferings(int offset, int size) {
		List<Offering> allOfferings = new ArrayList<Offering>();
		for (SOSMetadata metadata : getServiceMetadatas().values()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allOfferings.addAll(converter.convertOfferings(lookup.getOfferings()));
		}
		return allOfferings.toArray(new Offering[0]);
	}

	@Override
	public Offering getOffering(String item) {
		for (SOSMetadata metadata : getServiceMetadatas().values()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			org.n52.shared.serializable.pojos.sos.Offering result = lookup.getOffering(item);
			if(result != null) {
				return converter.convertOffering(result);
			}
		}
		return null;
	}

}
