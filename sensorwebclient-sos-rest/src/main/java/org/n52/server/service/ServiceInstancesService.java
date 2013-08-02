
package org.n52.server.service;

import java.util.Collection;

import org.n52.server.api.v0.ctrl.ResourceNotFoundException;
import org.n52.server.api.v0.output.ServiceInstance;

public interface ServiceInstancesService {

    /**
     * @return gets all known service instances.
     */
    public Collection<ServiceInstance> getServiceInstances();

    /**
     * Returns a single service instance if known to the service.
     * 
     * @param id
     *        the {@link ServiceInstance}'s id.
     * @return the {@link ServiceInstance} or <code>null</code> if <code>id</code> could not be associated
     *         with a known service instance.
     * @throws if
     *         service instance is not available.
     */
    public ServiceInstance getServiceInstance(String id) throws ResourceNotFoundException;
}
