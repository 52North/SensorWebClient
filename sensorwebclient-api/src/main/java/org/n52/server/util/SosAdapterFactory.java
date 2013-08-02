package org.n52.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.n52.oxf.sos.adapter.SOSAdapter;
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
            if (adapter == null) {
                return new SOSAdapter(sosVersion);
            }
            else {
                
                if (!SOSAdapter.class.isAssignableFrom(Class.forName(adapter))) {
                    LOGGER.warn("'{}' is not an SOSAdapter implementation! Create default.", adapter);
                    return new SOSAdapter(sosVersion);
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
