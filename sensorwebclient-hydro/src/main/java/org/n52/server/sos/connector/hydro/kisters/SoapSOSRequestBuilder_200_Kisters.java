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
import org.n52.server.da.oxf.TimePosition_OXFExtension;
import org.n52.server.sos.connector.hydro.SoapSOSRequestBuilder_200;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapSOSRequestBuilder_200_Kisters extends SoapSOSRequestBuilder_200 {

	private String sosUrl;

	private static final Logger LOGGER = LoggerFactory.getLogger(SoapSOSRequestBuilder_200_Kisters.class);

	@Override
	public String buildGetObservationRequest(ParameterContainer parameters) throws OXFException {
		// check the temporal filter
		ParameterShell temporalFilter = parameters.getParameterShellWithServiceSidedName("temporalFilter");
		if (temporalFilter == null) {
            return super.buildGetObservationRequest(parameters);
        }

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
                    LOGGER.trace("Received response: {}", resultStr);
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
