package org.n52.server.service;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.service.rest.model.ServiceInstance;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

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
        return (metadata != null) ? new ServiceInstance(metadata) : null;
    }

}
