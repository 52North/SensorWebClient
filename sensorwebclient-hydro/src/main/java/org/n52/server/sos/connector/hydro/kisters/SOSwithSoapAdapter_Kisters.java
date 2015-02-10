/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.server.sos.connector.hydro.kisters;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.server.sos.connector.hydro.SOSwithSoapAdapter;

public class SOSwithSoapAdapter_Kisters extends SOSwithSoapAdapter {

	public SOSwithSoapAdapter_Kisters(String sosVersion) {
		super(sosVersion);
		setRequestBuilder(new SoapSOSRequestBuilder_200_Kisters());
	}
	
	@Override
	public OperationResult doOperation(Operation operation,
			ParameterContainer parameters) throws ExceptionReport, OXFException {
		// set sos url to used it for an extra GetObs 
    	if (operation.getDcps()[0].getHTTPPostRequestMethods().size() > 0) {
    		ISOSRequestBuilder requestBuilder = this.getRequestBuilder();
    		if (requestBuilder instanceof SoapSOSRequestBuilder_200_Kisters) {
    			SoapSOSRequestBuilder_200_Kisters tempBuilder = (SoapSOSRequestBuilder_200_Kisters) requestBuilder;
    			tempBuilder.setUrl(operation.getDcps()[0].getHTTPPostRequestMethods().get(0).getOnlineResource().getHref());
    		}
        }
		return super.doOperation(operation, parameters);
	}

}
