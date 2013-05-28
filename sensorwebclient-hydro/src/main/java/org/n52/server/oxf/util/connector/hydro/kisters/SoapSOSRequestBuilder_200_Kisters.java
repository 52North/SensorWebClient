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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.n52.server.oxf.util.access.oxfExtensions.TimePosition_OXFExtension;
import org.n52.server.oxf.util.connector.hydro.SoapSOSRequestBuilder_200;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapSOSRequestBuilder_200_Kisters extends SoapSOSRequestBuilder_200 {
    
	private String sosUrl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SoapSOSRequestBuilder_200_Kisters.class);
	
	@Override
	public String buildGetObservationRequest(ParameterContainer parameters) throws OXFException {
		// check the temporal filter
		ParameterShell temporalFilter = parameters.getParameterShellWithServiceSidedName("temporalFilter");
		Object specifiedValue = temporalFilter.getSpecifiedValue();
		
		if (specifiedValue instanceof TimePosition_OXFExtension ) {
			TimePosition_OXFExtension timePosition = (TimePosition_OXFExtension) specifiedValue;
			// time parameter is latest
			if (timePosition.toISO8601Format().equals(TimePosition_OXFExtension.GET_OBSERVATION_TIME_PARAM_LAST)) {
				// remove temporal filter and send getObs request
				parameters.removeParameterShell(temporalFilter);
			}
			// time parameter is first
			if (timePosition.toISO8601Format().equals(TimePosition_OXFExtension.GET_OBSERVATION_TIME_PARAM_FIRST)) {
				// first send GetDataAvailability to get the start time then use this start time to get the first observation by getObs
				String getDataAvailability = buildGetDataAvailabilityRequest(parameters);
				try {
					HttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
					HttpResponse httpResponse = httpClient.executePost(this.sosUrl.trim(), getDataAvailability, ContentType.TEXT_XML);
					HttpEntity responseEntity = httpResponse.getEntity();
					OperationResult result = new OperationResult(responseEntity.getContent(), parameters, getDataAvailability);
					XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
					String resultStr = result_xb.xmlText();
					String timeString = resultStr.substring(resultStr.indexOf("<gml:beginPosition>") + 19, resultStr.indexOf("</gml:beginPosition>"));
					// add start time to an GetObservation request
					parameters.removeParameterShell(temporalFilter);
					parameters.addParameterShell(ISOSRequestBuilder.GET_OBSERVATION_TEMPORAL_FILTER_PARAMETER, timeString);
				} catch (Exception e) {
					LOGGER.error("Exception occured on server side, while requesting the timestamp of the first observation.", e.getCause());
				}
			}
		}
		return super.buildGetObservationRequest(parameters);
	}

	public void setUrl(String sosUrl) {
		this.sosUrl = sosUrl;
	}

}
