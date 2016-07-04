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
package org.n52.server.parser;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ows.capabilities.OperationsMetadata;
import org.n52.oxf.ows.capabilities.Parameter;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.valueDomains.StringValueDomain;
import org.n52.server.da.AccessorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorUtils.class);


    /**
     * @param sosUrl the service endpoint
     * @param adapter the adapter to use
     * @return the service's {@link ServiceDescriptor} representing its capabilities
     * @throws IllegalStateException if service's {@link ServiceDescriptor} cannot be loaded from service endpoint.
     */
    public static ServiceDescriptor getServiceDescriptor(final String sosUrl, final SOSAdapter adapter) {
        try {
            /* TODO SOSWrapper is not capable of intercepting custom IRequestBuilders yet! */
//            ServiceDescriptor descriptor = SOSWrapper.doGetCapabilities(sosUrl, adapter.getServiceVersion());
            final Callable<ServiceDescriptor> callable = new Callable<ServiceDescriptor>() {
                @Override
                public ServiceDescriptor call() throws Exception {
                    return adapter.initService(sosUrl);
                }
            };
            final FutureTask<ServiceDescriptor> t = new FutureTask<ServiceDescriptor>(callable);
            AccessorThreadPool.execute(t);
            return t.get(SERVER_TIMEOUT, MILLISECONDS);
        } catch (final InterruptedException e) {
            LOGGER.warn("Requesting capabilities of '{}' was interrupted.", sosUrl, e);
//            throw new IllegalStateException(String.format("Service descriptor unaccessable: %s ", sosUrl));
        }
        catch (final ExecutionException e) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Error executing capabilities request,  ");
            sb.append("SOS URL: '").append(sosUrl).append("', ");
            sb.append("SOSAdapter: ").append(adapter.getClass().getName());
            LOGGER.warn(sb.toString(), e.getCause());
//            throw new IllegalStateException(String.format("Service descriptor unaccessable: %s ", sosUrl));
        }
        catch (final TimeoutException e) {
            LOGGER.warn("Server '{}' did not repond.", sosUrl, e);
//            throw new IllegalStateException(String.format("Service descriptor unaccessable: %s ", sosUrl));
        }
        /* TODO do not return null (causes many other exceptions) => handle parsing exception appropriatly
         *
         * make possible to remove not parsable services. Now, runtime exceptions are masked by signatures which
         * catch just Exception => have to make sure that not valid services are not accessed by the client
         * over and over again ...
         */
        return null;
    }

    public static String getResponseFormat(final ServiceDescriptor serviceDesc, final String matchingPattern) {
        String respFormat = null;
        final OperationsMetadata metadata = serviceDesc.getOperationsMetadata();
        if (metadata != null) {
        	final Operation op = metadata.getOperationByName("GetObservation");
            final Parameter parameter = op.getParameter("responseFormat");
            if (parameter != null) {
                final StringValueDomain respDomain = (StringValueDomain) parameter.getValueDomain();
                for (final String elem : respDomain.getPossibleValues()) {
                    if (elem.toLowerCase().contains(matchingPattern.toLowerCase())) {
                        respFormat = elem;
                    }
                }
            }
        }
        return respFormat;
    }

    // TODO Review for the case of multiple SensorML versions
    public static String getSMLVersion(final ServiceDescriptor serviceDesc, final String sosVersion) {
        String smlVersion = null;
        final OperationsMetadata metadata = serviceDesc.getOperationsMetadata();
        if (metadata != null) {
        	final Operation opSensorML = metadata.getOperationByName("DescribeSensor");
            Parameter outputFormat = null;
            if (SosUtil.isVersion100(sosVersion)) { // SOS 1.0
                outputFormat = opSensorML.getParameter("outputFormat");
            } else if (SosUtil.isVersion200(sosVersion)) { // SOS 2.0
                outputFormat = opSensorML.getParameter("procedureDescriptionFormat");
            }
            if (outputFormat != null) {
                final StringValueDomain sensorMLDomain = (StringValueDomain) outputFormat.getValueDomain();
                for (final String elem : sensorMLDomain.getPossibleValues()) {
                    if (elem.contains("sensorML")) {
                        smlVersion = elem;
                    }
                }
            }
        }
        return smlVersion;
    }

    public static IBoundingBox createBbox(final ObservationOffering offering) {
        return createBbox(null, offering);
    }

    public static IBoundingBox createBbox(IBoundingBox sosBbox, final ObservationOffering offering) {
        if (sosBbox == null) {
            sosBbox = offering.getBoundingBoxes()[0];
        } else {
            if (!sosBbox.containsValue(offering.getBoundingBoxes()[0])) {
                final IBoundingBox newBbox = offering.getBoundingBoxes()[0];
                // lower left
                if (sosBbox.getLowerCorner()[0] > newBbox.getLowerCorner()[0]) {
                    sosBbox.getLowerCorner()[0] = newBbox.getLowerCorner()[0];
                }
                if (sosBbox.getLowerCorner()[1] > newBbox.getLowerCorner()[1]) {
                    sosBbox.getLowerCorner()[1] = newBbox.getLowerCorner()[1];
                }
                // upper right
                if (sosBbox.getUpperCorner()[0] < newBbox.getUpperCorner()[0]) {
                    sosBbox.getUpperCorner()[0] = newBbox.getUpperCorner()[0];
                }
                if (sosBbox.getUpperCorner()[1] < newBbox.getUpperCorner()[1]) {
                    sosBbox.getUpperCorner()[1] = newBbox.getUpperCorner()[1];
                }
            }
        }
        return sosBbox;
    }

}