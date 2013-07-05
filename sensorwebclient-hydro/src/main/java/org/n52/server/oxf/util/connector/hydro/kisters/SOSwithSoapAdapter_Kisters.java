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

package org.n52.server.oxf.util.connector.hydro.kisters;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.server.oxf.util.connector.hydro.SOSwithSoapAdapter;

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
