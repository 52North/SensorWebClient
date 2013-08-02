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
package org.n52.server.sos.parser;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.jfree.util.Log;
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
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
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
            Callable<ServiceDescriptor> callable = new Callable<ServiceDescriptor>() {
                @Override
                public ServiceDescriptor call() throws Exception {
                    return adapter.initService(sosUrl);
                }
            };
            FutureTask<ServiceDescriptor> t = new FutureTask<ServiceDescriptor>(callable);
            AccessorThreadPool.execute(t);
            return t.get(SERVER_TIMEOUT, MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Requesting capabilities of '{}' was interrupted.", sosUrl, e);
//            throw new IllegalStateException(String.format("Service descriptor unaccessable: %s ", sosUrl));
        }
        catch (ExecutionException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error executing capabilities request,  ");
            sb.append("SOS URL: '").append(sosUrl).append("', ");
            sb.append("SOSAdapter: ").append(adapter.getClass().getName());
            LOGGER.warn(sb.toString(), e.getCause());
//            throw new IllegalStateException(String.format("Service descriptor unaccessable: %s ", sosUrl));
        }
        catch (TimeoutException e) {
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

    public static String getResponseFormat(ServiceDescriptor serviceDesc, String matchingPattern) {
        String respFormat = null;
        OperationsMetadata metadata = serviceDesc.getOperationsMetadata();
        if (metadata != null) {
        	Operation op = metadata.getOperationByName("GetObservation");
            Parameter parameter = op.getParameter("responseFormat");
            StringValueDomain respDomain = (StringValueDomain) parameter.getValueDomain();
            for (String elem : respDomain.getPossibleValues()) {
                if (elem.toLowerCase().contains(matchingPattern.toLowerCase())) {
                    respFormat = elem;
                }
            }
        }
        return respFormat;
    }

    public static String getSMLVersion(ServiceDescriptor serviceDesc, String sosVersion) {
        String smlVersion = null;
        OperationsMetadata metadata = serviceDesc.getOperationsMetadata();
        if (metadata != null) {
        	Operation opSensorML = metadata.getOperationByName("DescribeSensor");
            Parameter outputFormat = null;
            if (SosUtil.isVersion100(sosVersion)) { // SOS 1.0
                outputFormat = opSensorML.getParameter("outputFormat");
            } else if (SosUtil.isVersion200(sosVersion)) { // SOS 2.0
                outputFormat = opSensorML.getParameter("procedureDescriptionFormat");
            }
            StringValueDomain sensorMLDomain = (StringValueDomain) outputFormat.getValueDomain();
            for (String elem : sensorMLDomain.getPossibleValues()) {
                if (elem.contains("sensorML")) {
                    smlVersion = elem;
                }
            }        	
        }
        return smlVersion;
    }

    public static void setVersionNumbersToMetadata(String sosUrl, String title, String sosVersion, String omFormat, String smlVersion){
        SOSMetadata metadata = null;
        try {
            metadata = (SOSMetadata) ConfigurationContext.getServiceMetadatas().get(sosUrl);
        } catch (Exception e) {
            Log.error("Cannot cast SOSMetadata", e);
        }
        if (metadata != null) {
        	metadata.setTitle(title);
            metadata.setSensorMLVersion(smlVersion);
            metadata.setSosVersion(sosVersion);
            metadata.setOmVersion(omFormat);
            metadata.setInitialized(true);
        } else {
            ConfigurationContext.initializeMetadata(new SOSMetadata(sosUrl, sosVersion, smlVersion, omFormat, title));
        }
    }
    
    public static IBoundingBox createBbox(ObservationOffering offering) {
        return createBbox(null, offering);
    }

    public static IBoundingBox createBbox(IBoundingBox sosBbox, ObservationOffering offering) {
        if (sosBbox == null) {
            sosBbox = offering.getBoundingBoxes()[0];
        } else {
            if (!sosBbox.containsValue(offering.getBoundingBoxes()[0])) {
                IBoundingBox newBbox = offering.getBoundingBoxes()[0];
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