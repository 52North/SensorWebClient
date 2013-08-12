package org.n52.api.v1.srv;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.out.Service;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.v1.srv.ServicesParameterService;

public class RestServicesAPIAdapter implements ServicesParameterService {

	@Override
	public boolean isKnownTimeseries(String id) {
		if (ConfigurationContext.getSOSMetadataForItemName(id) != null) {
			return true;
		}
		return false;
	}

	@Override
	public Service[] getServices(int offset, int size) {
		List<Service> allServices = new ArrayList<Service>();
		for (SOSMetadata metadata : ConfigurationContext.getSOSMetadatas()) {
			ParameterConverter converter = new ParameterConverter(metadata);
			allServices.add(converter.convertService());
		}
		return allServices.toArray(new Service[0]);
	}

	@Override
	public Service getService(String item) {
		SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(item);
		if (metadata != null){
			ParameterConverter converter = new ParameterConverter(metadata);
			return converter.convertService();
		}
		return null;
	}

}
