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
package org.n52.server.da.oxf;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.OWSException;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOSAdapter_OXFExtension extends SOSAdapter {

    private static Logger log = LoggerFactory.getLogger(SOSAdapter_OXFExtension.class);
    
    private static final int CONNECTION_TIMEOUT = 5000;
    
    private static final int SOCKET_TIMEOUT = 30000;

    /**
     * @param sosVersion the SOS version the adapter shall connect to.
     */
    public SOSAdapter_OXFExtension(String sosVersion) {
        super(sosVersion, new SimpleHttpClient(CONNECTION_TIMEOUT, SOCKET_TIMEOUT));
        setRequestBuilder(SosRequestBuilderFactory.createRequestBuilder(sosVersion));
    }

    /**
     * @deprecated use {@link #SOSAdapter_OXFExtension(String)} instead
     * @param sosVersion
     *        the SOS version.
     * @param requestBuilder
     *        the custom request builder
     */
    @Deprecated
    public SOSAdapter_OXFExtension(String sosVersion, ISOSRequestBuilder requestBuilder) {
        super(sosVersion, requestBuilder);
        setHttpClient(new SimpleHttpClient(CONNECTION_TIMEOUT, SOCKET_TIMEOUT));
    }

    @Override
    public ServiceDescriptor initService(String url) throws ExceptionReport, OXFException {
        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell("version", serviceVersion);
        paramCon.addParameterShell("service", "SOS");
        Operation operation = new Operation("GetCapabilities", url.toString(), url.toString());
        OperationResult opResult = doOperation(operation, paramCon);
        return initService(opResult);
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
        try {
            return super.doOperation(operation, parameters);
        }
        catch (ExceptionReport e) {
            handleExceptionReport(e);
            return null;
        }
    }

    private void handleExceptionReport(ExceptionReport report) throws ExceptionReport {
        OWSException ex = report.getExceptionsIterator().next();
        if (ex.getExceptionTexts().length > 0) {
            for (int i = 0; i < ex.getExceptionTexts().length; i++) {
                log.warn(ex.getExceptionTexts()[i]);
            }
        }

        if (ex.getLocator() != null) {
            if ( !ex.getLocator().equals("procedure") && !ex.getExceptionCode().equals("InvalidParameterValue")) {
                throw report;
            }
            else {
                // ignore generalization "procedures"
            }
        }
    }

}
