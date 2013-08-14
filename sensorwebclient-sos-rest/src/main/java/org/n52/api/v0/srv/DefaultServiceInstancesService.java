package org.n52.api.v0.srv;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.api.v0.out.ServiceInstance;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.ResourceNotFoundException;

public class DefaultServiceInstancesService implements ServiceInstancesService {

    @Override
    public Collection<ServiceInstance> getServiceInstances() {
        ArrayList<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (SOSMetadata metadata : ConfigurationContext.getSOSMetadatas()) {
            serviceInstances.add(new ServiceInstance(metadata));
        }
        return serviceInstances;
    }

    @Override
    public ServiceInstance getServiceInstance(String id) {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadataForItemName(id);
        if (metadata == null) {
            throw new ResourceNotFoundException();
        }
        return new ServiceInstance(metadata);
    }

	@Override
	public boolean containsServiceInstance(String serviceInstance) {
		return ConfigurationContext.containsServiceInstance(serviceInstance);
	}

	@Override
	public SOSMetadata getSOSMetadataForItemName(String serviceInstance) {
		SOSMetadata sosMetadata = ConfigurationContext.getSOSMetadataForItemName(serviceInstance);
		return sosMetadata;
	}

	@Override
	public Collection<SOSMetadata> getSOSMetadatas() {
		return ConfigurationContext.getSOSMetadatas();
	}

}
