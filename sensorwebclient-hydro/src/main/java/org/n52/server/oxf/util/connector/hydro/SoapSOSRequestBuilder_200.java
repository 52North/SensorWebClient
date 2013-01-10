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
package org.n52.server.oxf.util.connector.hydro;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.oxf.util.access.oxfExtensions.SOSRequestBuilder_200_OXFExtension;
import org.n52.server.oxf.util.access.oxfExtensions.TimePosition_OXFExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class SoapSOSRequestBuilder_200 extends SOSRequestBuilder_200_OXFExtension {
    
    // TODO extract to a common request wrapper
	
	/** Soap header actions for the sos requests */
	private static final String GET_CAPABILITIES_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetCapabilities";
	private static final String GET_FOI_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/foiRetrieval/2.0/GetFeatureOfInterest";
	private static final String DESCRIBE_SENSOR_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/DescribeSensor"; // spec says: http://www.opengis.net/swes/2.0/DescribeSensor
	private static final String GET_OBS_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetObservation";
	private static final String GET_DATA_AVAILABILITY = "http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailability";
	
	private String sosUrl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SoapSOSRequestBuilder_200.class);
	
	@Override
	public String buildGetCapabilitiesRequest(ParameterContainer parameters) {
		String request = super.buildGetCapabilitiesRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_CAPABILITIES_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	@Override
	public String buildGetFeatureOfInterestRequest(ParameterContainer parameters) {
		String request = super.buildGetFeatureOfInterestRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_FOI_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);  
	}

	@Override
	public String buildDescribeSensorRequest(ParameterContainer parameters) {
		String request = super.buildDescribeSensorRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, DESCRIBE_SENSOR_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}
	
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
				return createGetObsSoapRequest(parameters);
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
					return createGetObsSoapRequest(parameters);
				} catch (Exception e) {
					LOGGER.error("Exception occured on server side, while requesting the timestamp of the first observation.", e.getCause());
				}
			}
		}
		return createGetObsSoapRequest(parameters);
	}

	private String createGetObsSoapRequest(ParameterContainer parameters) throws OXFException {
		String request = super.buildGetObservationRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_OBS_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}
	
	public String buildGetDataAvailabilityRequest(ParameterContainer parameters) throws OXFException {
		StringBuffer sb = new StringBuffer();
	    String observedProperty = (String) parameters.getParameterShellWithCommonName(ISOSRequestBuilder.GET_OBSERVATION_OBSERVED_PROPERTY_PARAMETER).getSpecifiedValue();
	    String procedure = (String) parameters.getParameterShellWithCommonName(ISOSRequestBuilder.GET_OBSERVATION_PROCEDURE_PARAMETER).getSpecifiedValue();
	    String offering = (String) parameters.getParameterShellWithCommonName(ISOSRequestBuilder.GET_OBSERVATION_OFFERING_PARAMETER).getSpecifiedValue();
	    String feature = (String) parameters.getParameterShellWithCommonName(ISOSRequestBuilder.GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER).getSpecifiedValue();
	    sb.append("<sos:GetDataAvailability service=\"SOS\" version=\"2.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\">");
	    sb.append("<sos:observedProperty>" + observedProperty + "</sos:observedProperty>");
	    sb.append("<sos:procedure>" + procedure + "</sos:procedure>");
	    sb.append("<sos:offering>" + offering + "</sos:offering>");
	    sb.append("<sos:featureOfInterest>" + feature + "</sos:featureOfInterest>");
	    sb.append("</sos:GetDataAvailability>");
	    EnvelopeDocument envelope = addSoapEnvelope(sb.toString(), GET_DATA_AVAILABILITY);
	    return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	private EnvelopeDocument addSoapEnvelope(String request, String action) {
		XmlObject xb_request = null;
		try {
			xb_request = XmlObject.Factory.parse(request);
		} catch (XmlException e) {
			e.printStackTrace();
		}
		EnvelopeDocument envelope = SoapUtil.wrapToSoapEnvelope(xb_request, action);
		return envelope;
	}

	public void setUrl(String sosUrl) {
		this.sosUrl = sosUrl;
	}
	

}
