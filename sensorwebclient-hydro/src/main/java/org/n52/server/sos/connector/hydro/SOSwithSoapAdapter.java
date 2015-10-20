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
package org.n52.server.sos.connector.hydro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import static org.apache.http.entity.ContentType.create;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_SERVICE_PARAMETER;
import org.n52.oxf.sos.adapter.SOSAdapter;
import static org.n52.oxf.sos.adapter.SOSAdapter.DESCRIBE_SENSOR;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_CAPABILITIES;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_OBSERVATION;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_OBSERVATION_BY_ID;
import static org.n52.oxf.sos.adapter.SOSAdapter.INSERT_OBSERVATION;
import static org.n52.oxf.sos.adapter.SOSAdapter.REGISTER_SENSOR;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.util.web.GzipEnabledHttpClient;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.HttpClientException;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class SOSwithSoapAdapter extends SOSAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSwithSoapAdapter.class);

    private static final ContentType SOAP_PLUS_XML = create("application/soap+xml", Charset.forName("UTF-8"));

    public static final String GET_DATA_AVAILABILITY = "GetDataAvailability";

    private static final int CONNECTION_TIMEOUT = 30000;

    private static final int SOCKET_TIMEOUT = 30000;

    private HttpClient httpClient = createHttpClient();

    private static HttpClient createHttpClient() {
        return new GzipEnabledHttpClient(new ProxyAwareHttpClient(new SimpleHttpClient(CONNECTION_TIMEOUT, SOCKET_TIMEOUT)));
    }

    /**
     * Creates an adapter to connect SOS with SOAP binding. <br>
     * <br>
     * Per default the Adapter uses {@link SoapSOSRequestBuilder_200} to build its request. Override via
     * {@link #setRequestBuilder(ISOSRequestBuilder)}.
     *
     * @param sosVersion
     *        the SOS version
     */
    public SOSwithSoapAdapter(String sosVersion) {
        super(sosVersion);
        setHttpClient(httpClient);
        setRequestBuilder(new SoapSOSRequestBuilder_200());
    }

    /**
     * Creates an adapter to connect SOS with SOAP binding. <br>
     * <br>
     * We use the overloaded constructor {@link SOSAdapter#SOSAdapter(String, ISOSRequestBuilder)} just to
     * satisfy reflection loading. Actually, there is <b>no parameter needed</b> for
     * <code>requestBuilder</code> and is not looked at at all (so it can be <code>null</code>). The
     * constructor creates its own {@link SOSRequestBuilderGET_200} instance internally by itself. <br>
     *
     * @deprecated use {@link #SOSwithSoapAdapter(String)} instead
     * @param sosVersion
     *        the SOS version
     * @param requestBuilder
     *        only for satisfying reflection loading and can be <code>null</code>.
     */
    public SOSwithSoapAdapter(String sosVersion, ISOSRequestBuilder requestBuilder) {
        super(sosVersion, new SoapSOSRequestBuilder_200());
        setHttpClient(httpClient);
        LOGGER.warn("This is a deprecated constructor and will be removed soon w/o notice.");
    }

    @Override
    public void setHttpClient(HttpClient httpclient) {
        this.httpClient = httpclient;
        super.setHttpClient(httpClient);
    }

    @Override
    public ServiceDescriptor initService(String url) throws ExceptionReport, OXFException {
        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(GET_CAPABILITIES_SERVICE_PARAMETER, "SOS");
        paramCon.addParameterShell(GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER, serviceVersion);
        Operation operation = new Operation(SOSAdapter.GET_CAPABILITIES, url, url);
        OperationResult opResult = doOperation(operation, paramCon);
        return initService(opResult);
    }

    @Override
    public ServiceDescriptor initService(OperationResult getCapabilitiesResult) throws ExceptionReport, OXFException {
        ByteArrayInputStream resultStream = getCapabilitiesResult.getIncomingResultAsStream();
        try {
            XmlObject capsDoc = XmlObject.Factory.parse(resultStream);
            if (SosUtil.isVersion100(serviceVersion)) {
                if (capsDoc instanceof net.opengis.sos.x10.CapabilitiesDocument) {
                    return initService((net.opengis.sos.x10.CapabilitiesDocument) capsDoc);
                }
            }
            else if (SosUtil.isVersion200(serviceVersion)) {
                if (capsDoc instanceof net.opengis.sos.x20.CapabilitiesDocument) {
                    return initService((net.opengis.sos.x20.CapabilitiesDocument) capsDoc);
                }
            }
            throw new OXFException("Version is not supported: " + serviceVersion);
        }
        catch (XmlException e) {
            // BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream));
            // LOGGER.debug("First line of response: {}", reader.readLine());
            throw new OXFException("Unparsable XML response.", e);
        }
        catch (IOException e) {
            throw new OXFException("Could not read from stream.", e);
        }
    }

    private String readContent(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
        HttpEntity responseEntity = null;
        String request = buildRequest(operation, parameters);
        try {
            String url = operation.getDcps()[0].getHTTPPostRequestMethods().get(0).getOnlineResource().getHref();
            responseEntity = sendSoapRequest(url, request);
       } catch (HttpClientException e) {
           throw new OXFException("Could not send SOAP request.", e);
       }

        OperationResult result = null;
        try {
            result = new OperationResult(responseEntity.getContent(), parameters, request);
            ByteArrayInputStream resultStream = result.getIncomingResultAsStream();
            XmlObject result_xb = XmlObject.Factory.parse(resultStream);
            LOGGER.trace("Reveived response: {}", result_xb.xmlText());
            if (result_xb instanceof EnvelopeDocument) {
                EnvelopeDocument envelopeDoc = (EnvelopeDocument) result_xb;
                XmlObject body = SoapUtil.readBodyNodeFrom(envelopeDoc, null);
                return new OperationResult(body.newInputStream(),
                                           result.getUsedParameters(),
                                           result.getSendedRequest());
            } else {
                LOGGER.warn("Unexpected raw response (w/o SOAP Envolope): {}", result_xb.xmlText());
            }
            return result;
        }
        catch (XmlException e) {
            String response = result == null ? "NULL" : readContent(result.getIncomingResultAsStream());
            throw new OXFException("Unparsable XML response: " + response, e);
        }
        catch (IOException e) {
            throw new OXFException("Could not read from stream.", e);
        }
    }

    private String buildRequest(final Operation operation, final ParameterContainer parameters) throws OXFException {
        SoapSOSRequestBuilder_200 builder = (SoapSOSRequestBuilder_200) getRequestBuilder();
        if (operation.getName().equals(GET_CAPABILITIES)) {
            return builder.buildGetCapabilitiesRequest(parameters);
        }
        else if (operation.getName().equals(GET_OBSERVATION)) {
            return builder.buildGetObservationRequest(parameters);
        }
        else if (operation.getName().equals(DESCRIBE_SENSOR)) {
            return builder.buildDescribeSensorRequest(parameters);
        }
        else if (operation.getName().equals(GET_FEATURE_OF_INTEREST)) {
            return builder.buildGetFeatureOfInterestRequest(parameters);
        }
        else if (operation.getName().equals(GET_DATA_AVAILABILITY)) {
            return builder.buildGetDataAvailabilityRequest(parameters);
        }
        else {
            throw new OXFException(format("Operation '%s' not supported.", operation.getName()));
        }
    }

    private HttpEntity sendSoapRequest(String url, String request) throws HttpClientException {
        HttpResponse httpResponse = httpClient.executePost(url, request, SOAP_PLUS_XML);
        return httpResponse.getEntity();
    }

}