/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.sos.connector.hydro;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.da.oxf.SOSRequestBuilder_200_OXFExtension;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class SoapSOSRequestBuilder_200 extends SOSRequestBuilder_200_OXFExtension {

    // TODO extract to a common request wrapper

	/** Soap header actions for the sos requests */
	private static final String GET_CAPABILITIES_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetCapabilities";
	private static final String GET_FOI_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/foiRetrieval/2.0/GetFeatureOfInterest";
	private static final String DESCRIBE_SENSOR_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/DescribeSensor"; // spec says: http://www.opengis.net/swes/2.0/DescribeSensor
	private static final String GET_OBS_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetObservation";
	private static final String GET_DATA_AVAILABILITY = "http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailability";

	protected String sosUrl;

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
        parameters.removeParameterShell(parameters.getParameterShellWithCommonName(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER));
        parameters.addParameterShell(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER, "http://www.opengis.net/waterml/2.0");

        String request = super.buildGetObservationRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_OBS_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	public String buildGetDataAvailabilityRequest(ParameterContainer parameters) throws OXFException {
		StringBuffer sb = new StringBuffer();
	    ParameterShell observedProperty = parameters.getParameterShellWithCommonName("observedProperty");
	    ParameterShell procedure = parameters.getParameterShellWithCommonName("procedure");
	    ParameterShell offering = parameters.getParameterShellWithCommonName("offering");
	    ParameterShell feature = parameters.getParameterShellWithCommonName("featureOfInterest");
	    ParameterShell version = parameters.getParameterShellWithCommonName("version");
	    sb.append("<gda:GetDataAvailability service=\"SOS\"");
        sb.append(" version=\"" + version.getSpecifiedValue() + "\"");
        sb.append(" xmlns:sos=\"http://www.opengis.net/sos/2.0\"");
        sb.append(" xmlns:gda=\"http://www.opengis.net/sosgda/1.0\"");
        sb.append(" >");
	    if (observedProperty != null) {
	    	sb.append("<sos:observedProperty>" + observedProperty.getSpecifiedValue() + "</sos:observedProperty>");
	    }
	    if (procedure != null) {
	    	sb.append("<sos:procedure>" + procedure.getSpecifiedValue() + "</sos:procedure>");
	    }
	    if (offering != null) {
	    	sb.append("<sos:offering>" + offering.getSpecifiedValue() + "</sos:offering>");
	    }
	    if (feature != null) {
	    	sb.append("<sos:featureOfInterest>" + feature.getSpecifiedValue() + "</sos:featureOfInterest>");
	    }
	    sb.append("</gda:GetDataAvailability>");
	    EnvelopeDocument envelope = addSoapEnvelope(sb.toString(), GET_DATA_AVAILABILITY);
	    return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	protected EnvelopeDocument addSoapEnvelope(String request, String action) {
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
