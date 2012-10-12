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
package org.n52.server.oxf.util.parser;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.jfree.util.Log;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ows.capabilities.Parameter;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.valueDomains.StringValueDomain;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorUtils.class);
    
    public static ServiceDescriptor getServiceDescriptor(final String sosUrl, final SOSAdapter adapter) {
        ServiceDescriptor serviceDesc = null;
        try {
            Callable<ServiceDescriptor> callable = new Callable<ServiceDescriptor>() {
                @Override
                public ServiceDescriptor call() throws Exception {
                    return adapter.initService(sosUrl);
                }
            };
            FutureTask<ServiceDescriptor> t = new FutureTask<ServiceDescriptor>(callable);
            AccessorThreadPool.execute(t);
            serviceDesc = t.get(ConfigurationContext.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.info("Could not get SOS Capabilities.", e);
        }
        return serviceDesc;
    }

    public static String getServiceTitle(ServiceDescriptor serviceDesc) {
        String title = "";
        try {
            title = serviceDesc.getServiceIdentification().getTitle();
        } catch (Exception e) {
            LOGGER.error("Could not get a SOS title from capabilities.", e);
        }
        return title;
    }

    public static String getOMFormat(ServiceDescriptor serviceDesc) {
        String respFormat = null;
        Operation op = serviceDesc.getOperationsMetadata().getOperationByName("GetObservation");
        Parameter parameter = op.getParameter("responseFormat");
        StringValueDomain respDomain = (StringValueDomain) parameter.getValueDomain();
        for (String elem : respDomain.getPossibleValues()) {
            if (elem.contains("OM") || elem.contains("om")) {
                respFormat = elem;
            }
        }
        return respFormat;
    }

    public static String getSMLVersion(ServiceDescriptor serviceDesc, String sosVersion) {
        String smlVersion = null;
        Operation opSensorML = serviceDesc.getOperationsMetadata().getOperationByName("DescribeSensor");
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

    public static IBoundingBox createBbox(IBoundingBox sosBbox, ObservationOffering offering) {
        try {
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
        } catch (Exception e) {
            LOGGER.info(String.format("Could not parse BBox for offering '%s'.", offering), e);
            return sosBbox; // ignore this offering
        }
        return sosBbox;
    }
    
}