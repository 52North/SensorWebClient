/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.api.v0.srv;

import java.util.Collection;

import org.n52.api.v0.out.ServiceInstance;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.ResourceNotFoundException;

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
	 * similar to {@link ServicesParameterService.getServiceInstance}
	 */
	// TODO remove this method
	public SOSMetadata getSOSMetadataForItemName(String serviceInstance);

	public Collection<SOSMetadata> getSOSMetadatas();
}
