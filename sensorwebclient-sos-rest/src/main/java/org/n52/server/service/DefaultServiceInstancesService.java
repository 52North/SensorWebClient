package org.n52.server.service;

import static org.n52.server.oxf.util.ConfigurationContext.getSOSMetadataForItemName;
import static org.n52.server.oxf.util.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.server.api.v0.ctrl.ResourceNotFoundException;
import org.n52.server.api.v0.output.ServiceInstance;
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
        SOSMetadata metadata = getSOSMetadataForItemName(id);
        if (metadata == null) {
            throw new ResourceNotFoundException();
        }
        return new ServiceInstance(metadata);
    }

}
