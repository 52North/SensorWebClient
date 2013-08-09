package org.n52.server.service;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.io.input.ResourceNotFoundException;
import org.n52.io.input.ServiceInstancesService;
import org.n52.io.v0.output.ServiceInstance;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class DefaultServiceInstancesService implements ServiceInstancesService {

    @Override
    public Collection<ServiceInstance> getServiceInstances() {
        ArrayList<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
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

}
