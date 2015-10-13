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
package org.n52.server.parser;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.util.ArrayList;
import java.util.List;
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
    
    private static final String SML_101 = "sensorML/1.0.1";
    
    private static final String SML_20 = "sensorml/2.0";


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
        for (String elem : getResponseFormats(serviceDesc)) {
        	 if (elem.toLowerCase().contains(matchingPattern.toLowerCase())) {
               respFormat = elem;
           }
		}
        return respFormat;
    }
    
	/**
	 * Get supported observation response formats
	 * 
	 * @param serviceDesc
	 *            {@link ServiceDescriptor}
	 * @return {@link List} of supported observation response formats
	 */
    public static List<String> getResponseFormats(final ServiceDescriptor serviceDesc) {
        final OperationsMetadata metadata = serviceDesc.getOperationsMetadata();
        if (metadata != null) {
        	final Operation op = metadata.getOperationByName("GetObservation");
            final Parameter parameter = op.getParameter("responseFormat");
            if (parameter != null) {
                final StringValueDomain respDomain = (StringValueDomain) parameter.getValueDomain();
                if (respDomain.getPossibleValues() != null) {
                	return respDomain.getPossibleValues();
                }
            }
        }
        return new ArrayList<String>(0);
    }
    

    // TODO Review for the case of multiple SensorML versions
    public static String getSMLVersion(final ServiceDescriptor serviceDesc, final String sosVersion) {
        String smlVersion = null;
        List<String> outputFormats = getProcedureDescriptionFormats(serviceDesc, sosVersion);
        if (SosUtil.isVersion100(sosVersion)) { // SOS 1.0
            smlVersion = checkSMLVersionForSOS100(outputFormats);
        } else if (SosUtil.isVersion200(sosVersion)) { // SOS 2.0
            smlVersion = checkSMLVersionForSOS20(outputFormats);
        }
        return smlVersion;
    }
    
    public static List<String> getProcedureDescriptionFormats(final ServiceDescriptor serviceDesc, final String sosVersion) {
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
                if (sensorMLDomain.getPossibleValues() != null) {
                	return sensorMLDomain.getPossibleValues();
                }
            }
        }
        return new ArrayList<String>(0);
    }
    
    private static String checkSMLVersionForSOS100(List<String> outputFormats) {
    	if (outputFormats != null) {
        	// Prefer SensorML 2.0 if supported, default is SensorML 1.0.1
            String sml101 = null;
            String sml20 = null;
            for (final String elem : outputFormats) {
            	if (elem.startsWith("text/xml")) {
	                if (elem.contains(SML_101)) {
	                	sml101 = elem;
	                } else if (elem.contains(SML_20)) {
	                	sml20 = elem;
	                }
            	}
            }
            // prefer SensorML 2.0 if supported
            if (sml20 != null) {
            	return sml20;
            }
            return sml101;
        }
		return null;
	}

	private static String checkSMLVersionForSOS20(List<String> outputFormats) {
		if (outputFormats != null) {
        	// Prefer SensorML 2.0 if supported, default is SensorML 1.0.1
            String sml101 = null;
            String sml20 = null;
            for (final String elem : outputFormats) {
            	if (elem.startsWith("http")) {
	                if (elem.contains(SML_101)) {
	                	sml101 = elem;
	                } else if (elem.contains(SML_20)) {
	                	sml20 = elem;
	                }
            	}
            }
            // prefer SensorML 2.0 if supported
            if (sml20 != null) {
            	return sml20;
            }
            return sml101;
        }
		return null;
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