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

package org.n52.server.oxf.util.connector.eea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.OWSException;
import org.n52.oxf.ows.capabilities.OnlineResource;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOSAdapterByGET extends SOSAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSAdapterByGET.class);

    /**
     * Creates a SOS adapter to connect to ArcGIS Server SOS SOE extension points by GET. <br>
     * <br>
     * We use the overloaded constructor {@link SOSAdapter#SOSAdapter(String, ISOSRequestBuilder)} just to
     * satisfy reflection loading. Actually, there is <b>no parameter needed</b> for
     * <code>requestBuilder</code> and is not looked at at all (so it can be <code>null</code>). The
     * constructor creates its own {@link SOSRequestBuilderGET_200} instance internally by itself. <br>
     * <br>
     * TODO This however can for sure be part of a next refactoring ...
     * 
     * @param sosVersion
     *        the SOS version
     * @param requestBuilder
     *        only for satisfying reflection loading and can be <code>null</code>.
     */
    public SOSAdapterByGET(String sosVersion, ISOSRequestBuilder requestBuilder) {
        super(sosVersion, new SOSRequestBuilderGET_200());
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport, OXFException {
        InputStream responseStream = null;
        InputStream inputStream = null;
        try {
            String requestString = buildRequest(operation, parameters);
            String serviceUrl = getFirstDcpOnlineResourceForGET(operation).getHref();
            serviceUrl = fixServiceUrl(operation, serviceUrl);
            LOGGER.debug("Built GET request string: {}", requestString);
            GetMethod method = new GetMethod(serviceUrl);
            method.setQueryString(requestString);
            responseStream = IOHelper.execute(method).getResponseBodyAsStream();
            String responseString = inputStreamToString(responseStream);

            XmlObject response = parseToXmlObject(responseString);
            OperationResult result = new OperationResult(response.newInputStream(), parameters, requestString);
            checkForExceptionReport(result, response);
            return result;
        } catch (IOException e) {
            LOGGER.trace("Error while reading operation result", e);
            throw new OXFException("Received an invalid reponse.", e);
        } catch (XmlException e) {
            LOGGER.error("Could not parse response to XML.", e);
            throw new OXFException("Received an invalid reponse.", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new OXFException("Could not close response stream.", e);
            }
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
//            return new String(response.getBytes(), "UTF-8");
        } finally {
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
       } catch (XmlException e) {
           LOGGER.warn("Server returned non XML data: {}", responseString);
           throw e;
       }
    }

    private void checkForExceptionReport(OperationResult result, XmlObject response) throws XmlException, ExceptionReport {
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
            return href.replace("?", "").concat("GetCapabilities?");
        }
        else if (operation.getName().equals(GET_FEATURE_OF_INTEREST)) {
            return href.replace("?", "").concat("GetFeatureOfInterest?");
        }
        else if (operation.getName().equals(GET_OBSERVATION)) {
            return href.replace("?", "").concat("GetObservation?");
        }
        else if (operation.getName().equals(DESCRIBE_SENSOR)) {
            return href.replace("?", "").concat("DescribeSensor?");
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
