package org.n52.server.service;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadataForItemName;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.io.v0.output.ServiceInstance;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.v0.ctrl.ResourceNotFoundException;
import org.n52.web.v0.srv.ServiceInstancesService;

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
