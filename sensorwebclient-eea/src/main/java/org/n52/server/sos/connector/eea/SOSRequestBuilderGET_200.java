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
package org.n52.server.sos.connector.eea;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.oxf.valueDomains.time.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOSRequestBuilderGET_200 implements ISOSRequestBuilder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SOSRequestBuilderGET_200.class);

	@Override
	public String buildGetCapabilitiesRequest(ParameterContainer parameters) {
		return "request=GetCapabilities&service=SOS&f=xml";
	}

	@Override
	public String buildGetObservationRequest(ParameterContainer parameters) throws OXFException {
    	ITime specifiedTime = getSpecifiedTime(parameters);
    	if (isSetFirstOrLatestTime(specifiedTime.toISO8601Format())) {
    		LOGGER.warn("'latest' und 'getFirst' are not supported for this SOS connection");
    		return null;
		}
    	
    	String service = getSpecifiedStringValue(parameters, GET_OBSERVATION_SERVICE_PARAMETER);
		String sosVersion = getSpecifiedStringValue(parameters, GET_OBSERVATION_VERSION_PARAMETER);
//		String[] offerings = getSpecifiedStringArrayValue(parameters, GET_OBSERVATION_OFFERING_PARAMETER);
        String[] procedures =  getSpecifiedStringArrayValue(parameters, GET_OBSERVATION_PROCEDURE_PARAMETER);
		String[] observedProperties =  getSpecifiedStringArrayValue(parameters, GET_OBSERVATION_OBSERVED_PROPERTY_PARAMETER);
        String[] features =  getSpecifiedStringArrayValue(parameters, GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER);

        StringBuilder request = new StringBuilder();
        request.append("request=GetObservation&");
		request.append("service=").append(encode(service)).append("&");
		request.append("version=").append(encode(sosVersion)).append("&");
//		request.append("offering=").append(encode(offerings[0])).append("&"));
		request.append("procedure=").append(encode(procedures[0])).append("&");
		request.append("featureOfInterest=").append(encode(features[0])).append("&");
        request.append("observedProperty=").append(encode(observedProperties[0])).append("&");
        request.append("temporalFilter=").append(encode(createTemporalFilter(specifiedTime))).append("&");
        request.append("spatialFilter=&");
		request.append("namespaces=&");
		return request.append("f=xml").toString();
	}

	private ITime getSpecifiedTime(ParameterContainer parameters) {
	    Object tempFilter = parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_TEMPORAL_FILTER_PARAMETER).getSpecifiedValue();
        if (tempFilter instanceof ITime) {
            return (ITime) tempFilter;
        } else {
            return TimeFactory.createTime((String) tempFilter);
        }
    }

    private boolean isSetFirstOrLatestTime(String specifiedTime) {
        return specifiedTime.equals("latest") || specifiedTime.equals("getFirst");
    }

    private String[] getSpecifiedStringArrayValue(ParameterContainer parameters, String parameterName) {
	    ParameterShell parameterShell = parameters.getParameterShellWithServiceSidedName(parameterName);
        return parameterShell.getSpecifiedTypedValueArray(String[].class);
    }

    private String createTemporalFilter(ITime specifiedTime) {
		if (specifiedTime instanceof TimePeriod) {
			TimePeriod timePeriod = (TimePeriod) specifiedTime;
			ITimePosition start2 = timePeriod.getStart();
            ITimePosition end = timePeriod.getEnd();
			return createIso8601Duration(start2.toISO8601Format(), end.toISO8601Format());
		} else {
			LOGGER.error("Check the temporalFilter");
			return "";
		}
	}
    
    String createIso8601Duration(String start, String end) {
        StringBuilder sb = new StringBuilder();
        sb.append(fixTimeZone(start)).append("/");
        return sb.append(fixTimeZone(end)).toString();
    }
    
    String fixTimeZone(String timeString) {
        StringBuilder sb = new StringBuilder(timeString);
        int insertionIndex = timeString.length() - 2;
        if (sb.charAt(insertionIndex - 1) != ':') {
            sb.insert(insertionIndex, ":");
        }
        return sb.toString();
    }

	@Override
	public String buildDescribeSensorRequest(ParameterContainer parameters) {
		String service = getSpecifiedStringValue(parameters, DESCRIBE_SENSOR_SERVICE_PARAMETER);
		String sosVersion = getSpecifiedStringValue(parameters, DESCRIBE_SENSOR_VERSION_PARAMETER);
		String procedure = getSpecifiedStringValue(parameters, DESCRIBE_SENSOR_PROCEDURE_PARAMETER);
		String format = getSpecifiedStringValue(parameters, DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT);

        StringBuffer request = new StringBuffer();
        request.append("request=DescribeSensor&");
		request.append("service=" + encode(service) + "&");
		request.append("version=" + encode(sosVersion) + "&");
        request.append("procedure=" + encode(procedure) + "&");
		request.append("procedureDescriptionFormat=" + encode(format) + "&");
		return request.append("f=xml").toString();
	}

	private String getSpecifiedStringValue(ParameterContainer parameters, String parameterName) {
        ParameterShell parameter = parameters.getParameterShellWithServiceSidedName(parameterName);
        return (String) parameter.getSpecifiedValue();
    }

    @Override
	public String buildGetFeatureOfInterestRequest(ParameterContainer parameters) {
        String sosVersion = getSpecifiedStringValue(parameters, GET_FOI_VERSION_PARAMETER);
        String phenomenon = getSpecifiedStringValue(parameters, "phenomenon");
        String procedure = getSpecifiedStringValue(parameters, "procedure");
        String bbox = getSpecifiedStringValue(parameters, "bbox");
        
        StringBuilder request = new StringBuilder("service=SOS&");
        request.append("version=").append(encode(sosVersion)).append("&");
        request.append("procedure=").append(encode(procedure)).append("&");
        // TODO check this, spatial filter works with EEA-SOS, but not with the SYKE-SOS
//        request.append("spatialFilter=").append(encode(bbox)).append("&");
        request.append("observedProperty=").append(encode(phenomenon)).append("&");
        request.append("request=GetFeatureOfInterest&");
        return request.append("f=xml").toString();
	}

    public String encode(String parameter) {
        try {
            return encodePlusInParameter(URLEncoder.encode(parameter, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Could not encode parameter {}!", parameter);
            return parameter;
        }
    }

    String encodePlusInParameter(String parameter) {
        return parameter.replace("+", "%2B");
    }

    @Override
    public String buildGetObservationByIDRequest(ParameterContainer parameters) throws OXFException {
        throw new UnsupportedOperationException("get observation by id is not implemented.");
    }

	@Override
	public String buildInsertObservation(ParameterContainer parameters) {
		throw new UnsupportedOperationException("insert is not implemented.");
	}

	@Override
	public String buildRegisterSensor(ParameterContainer parameters) throws OXFException {
		throw new UnsupportedOperationException("register sensor is not implemented.");
	}

}
