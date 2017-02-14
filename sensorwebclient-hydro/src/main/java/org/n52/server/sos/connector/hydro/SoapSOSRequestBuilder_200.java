/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import net.opengis.sos.x20.GetFeatureOfInterestDocument;
import net.opengis.sos.x20.GetFeatureOfInterestType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.da.oxf.SOSRequestBuilder_200_OXFExtension;
import org.n52.server.mgmt.ConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class SoapSOSRequestBuilder_200 extends SOSRequestBuilder_200_OXFExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapSOSRequestBuilder_200.class);

    // TODO extract to a common request wrapper

	/** Soap header actions for the sos requests */
	private static final String GET_CAPABILITIES_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetCapabilities";
	private static final String GET_FOI_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/foiRetrieval/2.0/GetFeatureOfInterest";
	private static final String DESCRIBE_SENSOR_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/DescribeSensor"; // spec says: http://www.opengis.net/swes/2.0/DescribeSensor
	private static final String GET_OBS_SOAP_HEADER_ACTION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetObservation";
	private static final String GET_DATA_AVAILABILITY = "http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailability";

    private static final String WATERML_20_NS = "http://www.opengis.net/waterml/2.0";
    static final String SOS_GDA_10_PREFINAL_NS = "http://www.opengis.net/sos/2.0";
    static final String SOS_GDA_10_NS = "http://www.opengis.net/sosgda/1.0";

	protected String sosUrl;

	@Override
	public String buildGetCapabilitiesRequest(ParameterContainer parameters) {
		String request = super.buildGetCapabilitiesRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_CAPABILITIES_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	@Override
	public String buildGetFeatureOfInterestRequest(ParameterContainer parameters) {
		//String request = super.buildGetFeatureOfInterestRequest(parameters);
		//EnvelopeDocument envelope = addSoapEnvelope(request, GET_FOI_SOAP_HEADER_ACTION);
		//return envelope.xmlText(XmlUtil.PRETTYPRINT);
		GetFeatureOfInterestDocument xb_getFOIDoc = GetFeatureOfInterestDocument.Factory.newInstance();
    	GetFeatureOfInterestType xb_getFOI = xb_getFOIDoc.addNewGetFeatureOfInterest();
    	xb_getFOI.setService((String) parameters.getParameterShellWithServiceSidedName(GET_FOI_SERVICE_PARAMETER).getSpecifiedValue());
    	xb_getFOI.setVersion((String) parameters.getParameterShellWithServiceSidedName(GET_FOI_VERSION_PARAMETER).getSpecifiedValue());
    	if (parameters.containsParameterShellWithServiceSidedName("observedProperty")) {
    		xb_getFOI.addObservedProperty((String) parameters.getParameterShellWithServiceSidedName("observedProperty").getSpecifiedValue());
    	}
    	if (parameters.containsParameterShellWithServiceSidedName("procedure")) {
    		xb_getFOI.addProcedure((String) parameters.getParameterShellWithServiceSidedName("procedure").getSpecifiedValue());
    	}
		EnvelopeDocument envelope = addSoapEnvelope(xb_getFOIDoc.xmlText(), GET_FOI_SOAP_HEADER_ACTION);
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
        parameters.addParameterShell(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER, WATERML_20_NS);

		String request = super.buildGetObservationRequest(parameters);
		EnvelopeDocument envelope = addSoapEnvelope(request, GET_OBS_SOAP_HEADER_ACTION);
		return envelope.xmlText(XmlUtil.PRETTYPRINT);
	}

	public String buildGetDataAvailabilityRequest(ParameterContainer parameters) throws OXFException {
		StringBuilder sb = new StringBuilder();
	    ParameterShell observedProperty = parameters.getParameterShellWithCommonName("observedProperty");
	    ParameterShell procedure = parameters.getParameterShellWithCommonName("procedure");
	    ParameterShell offering = parameters.getParameterShellWithCommonName("offering");
	    ParameterShell feature = parameters.getParameterShellWithCommonName("featureOfInterest");
	    ParameterShell version = parameters.getParameterShellWithCommonName("version");
        ParameterShell phenomenonTime = parameters.getParameterShellWithCommonName("phenomenonTime");
	    sb.append("<gda:GetDataAvailability service=\"SOS\"");
        sb.append(" version=\"").append(version.getSpecifiedValue()).append("\"");
        sb.append(" xmlns:gda=\"");

	    boolean gdaPrefinal = false;
        if (parameters.containsParameterShellWithCommonName("gdaPrefinalNamespace")) {
            ParameterShell gdaNamespace = parameters.getParameterShellWithCommonName("gdaPrefinalNamespace");
            gdaPrefinal = Boolean.parseBoolean((String)gdaNamespace.getSpecifiedValue());
        }
        if (gdaPrefinal) {
            LOGGER.warn("The correct GDA namespace is now: {}", SOS_GDA_10_NS);
            LOGGER.warn("Instance is configured to use the prefinal GDA namespace '{}'.", SOS_GDA_10_PREFINAL_NS);
            LOGGER.warn("You will get an exception once the SOS has been updated and dropped the old namespace.");
            sb.append(SOS_GDA_10_PREFINAL_NS);
        } else {
            sb.append(SOS_GDA_10_NS);
        }
        sb.append("\"  >");
	    if (observedProperty != null) {
	    	sb.append("<gda:observedProperty>").append(observedProperty.getSpecifiedValue()).append("</gda:observedProperty>");
	    }
	    if (procedure != null) {
	    	sb.append("<gda:procedure>").append(procedure.getSpecifiedValue()).append("</gda:procedure>");
	    }
	    if (offering != null) {
	    	sb.append("<gda:offering>").append(offering.getSpecifiedValue()).append("</gda:offering>");
	    }
	    if (feature != null) {
	    	sb.append("<gda:featureOfInterest>").append(feature.getSpecifiedValue()).append("</gda:featureOfInterest>");
	    }
            if (phenomenonTime != null) {
                sb.append("<swes:extension xmlns:swes=\"http://www.opengis.net/swes/2.0\">");
                sb.append("<fes:During xmlns:fes=\"http://www.opengis.net/fes/2.0\">");
                sb.append("<fes:ValueReference>phenomenonTime</fes:ValueReference>");
                sb.append("<gml:TimePeriod gml:id=\"tp_1\" xmlns:gml=\"http://www.opengis.net/gml/3.2\">");
                String[] phenomTime = phenomenonTime.getSpecifiedTypedValueArray(String[].class);
                sb.append("<gml:beginPosition>").append(phenomTime[0]).append("</gml:beginPosition>");
                sb.append("<gml:endPosition>").append(phenomTime[1]).append("</gml:endPosition>");
                sb.append("</gml:TimePeriod>");
                sb.append("</fes:During>");
                sb.append("</swes:extension>");
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
