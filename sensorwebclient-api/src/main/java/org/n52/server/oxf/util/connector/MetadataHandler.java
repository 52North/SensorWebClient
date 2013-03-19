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

package org.n52.server.oxf.util.connector;

import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.oxf.util.parser.ConnectorUtils;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class MetadataHandler {
    
    // TODO pull up general methods and technics from extending handlers.
	private ServiceDescriptor serviceDescriptor;
	
	private SOSAdapter adapter;

    public abstract SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws Exception;

	protected SOSMetadata initMetadata(String sosUrl, String sosVersion) {
		SOSMetadata sosMetadata = ConfigurationContext.getServiceMetadatas().get(sosUrl);
		adapter = SosAdapterFactory.createSosAdapter(sosMetadata);
		serviceDescriptor = ConnectorUtils.getServiceDescriptor(
				sosUrl, adapter);
		String sosTitle = serviceDescriptor.getServiceIdentification().getTitle();
		String omFormat = ConnectorUtils.getOMFormat(serviceDescriptor);
		String smlVersion = ConnectorUtils.getSMLVersion(serviceDescriptor,
				sosVersion);
		// TODO check why no omFormat and smlVersion exists
		if (omFormat == null) {
			omFormat = "http://www.opengis.net/om/2.0";
		}
		// 
		if (smlVersion == null) {
			smlVersion = "http://www.opengis.net/sensorML/1.0.1";
		}
		
		ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle,
				sosVersion, omFormat, smlVersion);
		return sosMetadata;
	}
    
    protected Contents getServiceDescriptorContent() throws OXFException{
    	if (serviceDescriptor != null){
    		return serviceDescriptor.getContents();
    	} else {
    		throw new OXFException("No valid GetFeatureOfInterestREsponse");
    	}
    }
    
    protected SOSAdapter getSosAdapter() {
		return adapter;
	}
    
    /**
     * Creates an {@link AReferencingHelper} according to metadata settings (e.g. if XY axis order shall be
     * enforced during coordinate transformation).
     * 
     * @param metadata
     *        the SOS metadata containing SOS instance configuration.
     */
    protected AReferencingHelper createReferencingHelper(SOSMetadata metadata) {
        if (metadata.isForceXYAxisOrder()) {
            return AReferencingHelper.createEpsgForcedXYAxisOrder();
        }
        else {
            return AReferencingHelper.createEpsgStrictAxisOrder();
        }
    }
    
}
