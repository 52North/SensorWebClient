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

import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_SERVICE_PARAMETER;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class SOSwithSoapAdapter extends SOSAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSwithSoapAdapter.class);
    
    private static final int SOCKET_TIMEOUT = 30000;
    
    private SoapSOSRequestBuilder_200 requestBuilder;

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
        super(sosVersion, new SimpleHttpClient(5000, SOCKET_TIMEOUT));
        requestBuilder = new SoapSOSRequestBuilder_200();
        setRequestBuilder(requestBuilder);
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
        setHttpClient(new SimpleHttpClient(5000, SOCKET_TIMEOUT));
        LOGGER.warn("This is a deprecated constructor and will be removed soon w/o notice.");
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
//            BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream));
//            LOGGER.debug("First line of response: {}", reader.readLine());
            throw new OXFException("Unparsable XML response.", e);
        }
        catch (IOException e) {
            throw new OXFException("Could not read from stream.", e);
        }
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
    	// set sos url to used it for an extra GetObs 
    	if (operation.getDcps()[0].getHTTPPostRequestMethods().size() > 0) {
    		requestBuilder.setUrl(operation.getDcps()[0].getHTTPPostRequestMethods().get(0).getOnlineResource().getHref());
        }
        OperationResult result = super.doOperation(operation, parameters);
        ByteArrayInputStream resultStream = result.getIncomingResultAsStream();
        try {
            XmlObject result_xb = XmlObject.Factory.parse(resultStream);
            XmlObject document = null;
            if (result_xb instanceof EnvelopeDocument) {
                EnvelopeDocument envelopeDoc = (EnvelopeDocument) result_xb;
                document = SoapUtil.readBodyNodeFrom(envelopeDoc, null);
                // TODO change, its very dirty!!!
                return new OperationResult(new ByteArrayInputStream(document.xmlText().getBytes()),
                                           result.getUsedParameters(),
                                           result.getSendedRequest());
            }
        }
        catch (XmlException e) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream));
//            LOGGER.debug("First line of response: {}", reader.readLine());
            throw new OXFException("Unparsable XML response.", e);
        }
        catch (IOException e) {
            throw new OXFException("Could not read from stream.", e);
        }
        return result;
    }

}
