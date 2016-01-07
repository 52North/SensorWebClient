/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.OWSException;
import org.n52.oxf.ows.capabilities.OnlineResource;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.web.GzipEnabledHttpClient;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.HttpClientException;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOSAdapterByGET extends SOSAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSAdapterByGET.class);

    private static final int SOCKET_TIMEOUT = 30000;

    private HttpClient httpClient;
    
    /**
     * Creates an adapter to connect SOS with GET binding as specified by 52n ArcGIS SOS SOE. <br>
     * <br>
     * Per default the Adapter uses {@link SosRequestBuilderGET_200} to build its request. Override via
     * {@link #setRequestBuilder(ISOSRequestBuilder)}.
     * 
     * @param sosVersion
     *        the SOS version
     */
    public SOSAdapterByGET(String sosVersion) {
        super(sosVersion, new SimpleHttpClient(5000, SOCKET_TIMEOUT));
        setRequestBuilder(new SOSRequestBuilderGET_200());
        HttpClient proxyAwareClient = new ProxyAwareHttpClient(new SimpleHttpClient());
        httpClient = new GzipEnabledHttpClient(proxyAwareClient);
    }

    /**
     * Creates a SOS adapter to connect to ArcGIS Server SOS SOE extension points by GET. <br>
     * <br>
     * We use the overloaded constructor {@link SOSAdapter#SOSAdapter(String, ISOSRequestBuilder)} just to
     * satisfy reflection loading. Actually, there is <b>no parameter needed</b> for
     * <code>requestBuilder</code> and is not looked at at all (so it can be <code>null</code>). The
     * constructor creates its own {@link SosRequestBuilderGET_200} instance internally by itself. <br>
     * 
     * 
     * @deprecated use {@link #SOSwithSoapAdapter(String)} instead
     * @param sosVersion
     *        the SOS version
     * @param requestBuilder
     *        only for satisfying reflection loading and can be <code>null</code>.
     */
    public SOSAdapterByGET(String sosVersion, ISOSRequestBuilder requestBuilder) {
        super(sosVersion, new SOSRequestBuilderGET_200());
        setHttpClient(new SimpleHttpClient(5000, SOCKET_TIMEOUT));
        HttpClient proxyAwareClient = new ProxyAwareHttpClient(new SimpleHttpClient());
        httpClient = new GzipEnabledHttpClient(proxyAwareClient);
        LOGGER.warn("This is a deprecated constructor and will be removed soon w/o notice.");
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
        try {
            String requestString = buildRequest(operation, parameters);
            if (requestString == null) {
                throw new OXFException("No supported request!");
            }
            String serviceUrl = getFirstDcpOnlineResourceForGET(operation).getHref();
            serviceUrl = fixServiceUrl(operation, serviceUrl);
            HttpResponse httpResponse = httpClient.executeGet(serviceUrl + "?" + requestString);
            HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = inputStreamToString(responseEntity.getContent());
            XmlObject response = parseToXmlObject(responseString);
            OperationResult result = new OperationResult(response.newInputStream(), parameters, requestString);
            checkForExceptionReport(result, response);
            return result;
        }
        catch (IOException e) {
            throw new OXFException("Error while reading operation result.", e);
        }
        catch (XmlException e) {
            throw new OXFException("Could not parse response to XML.", e);
        }
        catch (HttpClientException e) {
            throw new OXFException("Could not send request.", e);
        }
    }

    private String inputStreamToString(InputStream reponseStream) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(reponseStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ( (line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            bufferedReader.close();
            String response = stringBuilder.toString();
            response = response.replace("&", "&amp;");

            response = replaceArtifact(response, "</output>\"", "</output>");
            response = replaceArtifact(response, "output-\"0", "output-0");

            return response;
            // return new String(response.getBytes(), "UTF-8");
        }
        finally {
            if (reponseStream != null) {
                reponseStream.close();
            }
        }
    }

    private String replaceArtifact(String response, String artifact, String replacement) {
        if (response.contains(artifact)) {
            LOGGER.warn("Response still contains errornous artifact: {}", artifact);
            response = response.replace(artifact, replacement);
        }
        return response;
    }

    private XmlObject parseToXmlObject(String responseString) throws XmlException {
        try {
            return XmlObject.Factory.parse(responseString);
        }
        catch (XmlException e) {
            LOGGER.warn("Server returned non XML data: {}", responseString);
            throw e;
        }
    }

    private void checkForExceptionReport(OperationResult result, XmlObject response) throws XmlException,
            ExceptionReport {
        if (isExceptionReportV11(response)) {
            ExceptionReport execRep = parseExceptionReport_100(result);
            OWSException ex = execRep.getExceptionsIterator().next();
            if (ex.getExceptionTexts().length > 0) {
                for (int i = 0; i < ex.getExceptionTexts().length; i++) {
                    LOGGER.warn(ex.getExceptionTexts()[i]);
                }
            }
            throw execRep;
        }
    }

    private boolean isExceptionReportV11(XmlObject object) {
        return object.schemaType() == ExceptionReportDocument.type;
    }

    private ExceptionReport parseExceptionReport_100(OperationResult result) throws XmlException {
        String requestResult = new String(result.getIncomingResult());
        ExceptionReportDocument exceptionReportDocument = ExceptionReportDocument.Factory.parse(requestResult);
        String language = exceptionReportDocument.getExceptionReport().getLang();
        String version = exceptionReportDocument.getExceptionReport().getVersion();
        ExceptionReport exceptionReport = new ExceptionReport(version, language);
        ExceptionType[] exceptions = exceptionReportDocument.getExceptionReport().getExceptionArray();
        for (ExceptionType exceptionType : exceptions) {
            String exceptionCode = exceptionType.getExceptionCode();
            String[] exceptionMessages = exceptionType.getExceptionTextArray();
            String locator = exceptionType.getLocator();
            String sentRequest = result.getSendedRequest();

            OWSException owsException = new OWSException(exceptionMessages, exceptionCode, sentRequest, locator);
            exceptionReport.addException(owsException);
        }
        return exceptionReport;
    }

    private OnlineResource getFirstDcpOnlineResourceForGET(Operation operation) {
        return operation.getDcps()[0].getHTTPGetRequestMethods().get(0).getOnlineResource();
    }

    private String fixServiceUrl(Operation operation, String href) throws OXFException {
        if (operation.getName().equals(GET_CAPABILITIES)) {
            return href.replace("?", "").concat("GetCapabilities");
        }
        else if (operation.getName().equals(GET_FEATURE_OF_INTEREST)) {
            return href.replace("?", "").concat("GetFeatureOfInterest");
        }
        else if (operation.getName().equals(GET_OBSERVATION)) {
            return href.replace("?", "").concat("GetObservation");
        }
        else if (operation.getName().equals(DESCRIBE_SENSOR)) {
            return href.replace("?", "").concat("DescribeSensor");
        }
        else {
            throw new OXFException("The Operation '" + operation.getName() + "' is not supported");
        }
    }

    private String buildRequest(Operation operation, ParameterContainer parameters) throws OXFException {
        if (operation.getName().equals(GET_CAPABILITIES)) {
            return getRequestBuilder().buildGetCapabilitiesRequest(parameters);
        }
        else if (operation.getName().equals(GET_OBSERVATION)) {
            return getRequestBuilder().buildGetObservationRequest(parameters);
        }
        else if (operation.getName().equals(DESCRIBE_SENSOR)) {
            return getRequestBuilder().buildDescribeSensorRequest(parameters);
        }
        else if (operation.getName().equals(GET_FEATURE_OF_INTEREST)) {
            return getRequestBuilder().buildGetFeatureOfInterestRequest(parameters);
        }
        else if (operation.getName().equals(INSERT_OBSERVATION)) {
            return getRequestBuilder().buildInsertObservation(parameters);
        }
        else if (operation.getName().equals(REGISTER_SENSOR)) {
            return getRequestBuilder().buildRegisterSensor(parameters);
        }
        else if (operation.getName().equals(GET_OBSERVATION_BY_ID)) {
            return getRequestBuilder().buildGetObservationByIDRequest(parameters);
        }
        else {
            throw new OXFException("The operation '" + operation.getName() + "' is not supported.");
        }
    }

}
