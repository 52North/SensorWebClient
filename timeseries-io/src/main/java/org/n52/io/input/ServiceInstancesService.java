
package org.n52.io.input;

import java.util.Collection;

import org.n52.io.v0.output.ServiceInstance;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

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

	public boolean containsServiceInstance(String serviceInstance);

	
	/** 
	 * similar to {@link ServiceInstancesService.getServiceInstance}
	 */
	// TODO remove this method
	public SOSMetadata getSOSMetadataForItemName(String serviceInstance);
}
