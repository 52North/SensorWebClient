
package org.n52.server.service;

import java.util.Collection;

import org.n52.server.service.rest.model.ServiceInstance;

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
     */
    public ServiceInstance getServiceInstance(String id);
}
