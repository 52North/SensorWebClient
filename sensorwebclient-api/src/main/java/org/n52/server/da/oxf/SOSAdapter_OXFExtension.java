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
    
    private static final int SOCKET_TIMEOUT = 30000;

    /**
     * @param sosVersion the SOS version the adapter shall connect to.
     */
    public SOSAdapter_OXFExtension(String sosVersion) {
        super(sosVersion, new SimpleHttpClient(5000, SOCKET_TIMEOUT));
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
        setHttpClient(new SimpleHttpClient(5000, SOCKET_TIMEOUT));
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
