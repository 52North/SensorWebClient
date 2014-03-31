/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.web.GzipEnabledHttpClient;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosAdapterFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SosAdapterFactory.class);
    
    /**
     * Creates an adapter to make requests to an SOS instance. If the given metadata does not
     * contain a full qualified class name defining the adapter implementation the default one
     * is returned.
     * 
     * @param metadata the SOS metadata where to create the SOS adapter implementation from.
     * @return the custom adapter implementation, or the default {@link SOSAdapter}.
     */
    public static SOSAdapter createSosAdapter(SOSMetadata metadata) {
        String adapter = metadata.getAdapter();
        String sosVersion = metadata.getSosVersion();
        try {
            SOSAdapter sosAdapter = new SOSAdapter(sosVersion);
            sosAdapter.setHttpClient(createHttpClient(metadata));
            if (adapter == null) {
                return sosAdapter;
            }
            else {
                
                if (!SOSAdapter.class.isAssignableFrom(Class.forName(adapter))) {
                    LOGGER.warn("'{}' is not an SOSAdapter implementation! Create default.", adapter);
                    return sosAdapter;
                }
                @SuppressWarnings("unchecked") // unassignable case handled already
                Class<SOSAdapter> clazz = (Class<SOSAdapter>) Class.forName(adapter);
                Class< ? >[] arguments = new Class< ? >[] {String.class};
                Constructor<SOSAdapter> constructor = clazz.getConstructor(arguments);
                return constructor.newInstance(sosVersion);
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find Adapter class '" + adapter + "'.", e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid Adapter constructor for '" + adapter + "'.", e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Could not create Adapter for '" + adapter + "'.", e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Not allowed to create Adapter for '" + adapter + "'.", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Instantiation failed for Adapter " + adapter + "'.", e);
        }
    }
    
    private static HttpClient createHttpClient(SOSMetadata metadata) {
        int timeout = metadata.getTimeout();
        SimpleHttpClient simpleClient = new SimpleHttpClient(timeout, timeout);
        return new GzipEnabledHttpClient(new ProxyAwareHttpClient(simpleClient));
    }
    
}
