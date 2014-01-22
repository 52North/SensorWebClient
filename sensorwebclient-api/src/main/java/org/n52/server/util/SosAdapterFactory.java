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
package org.n52.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.n52.oxf.sos.adapter.SOSAdapter;
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
            sosAdapter.setHttpClient(new ProxyAwareHttpClient(new SimpleHttpClient()));
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
}
