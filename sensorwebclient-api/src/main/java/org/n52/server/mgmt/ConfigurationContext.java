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

package org.n52.server.mgmt;

import static org.n52.shared.Constants.DEFAULT_SOS_VERSION;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.n52.server.da.MetadataHandler;
import org.n52.server.da.oxf.DefaultMetadataHandler;
import org.n52.server.util.Statistics;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

public class ConfigurationContext implements ServletContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationContext.class);

    @Autowired
    private ServletContext servletContext;
    
    
    

    private static Map<String, SOSMetadata> serviceMetadatas = Collections.synchronizedMap(new HashMap<String, SOSMetadata>());

    public static int STARTUP_DELAY;

    public static String COPYRIGHT;

    public static String XSL_DIR;

    public static String CACHE_DIR;

    public static String GEN_DIR;

    public static String GEN_URL;

    public static String ZIP_POSTFIX;

    public static String IMAGE_SERVICE;

    public static String GEN_DIR_ZIP;

    public static boolean USE_DEVEL_CACHING = false;

    public static boolean IS_DEV_MODE = true;

    public static final int FILE_KEEPING_TIME = 30 * 60 * 1000;

    public static long SERVER_TIMEOUT = 60 * 1000;

    public static int THREAD_POOL_SIZE = 10;

    public static boolean UPDATE_TASK_RUNNING = false;

    public static boolean FACADE_COMPRESSION = false;

    public static int STATISTICS_INTERVAL = 60;

    public static int TOOLTIP_MIN_COUNT = 50;

    public static List<String> NO_DATA_VALUES;
    
    public ConfigurationContext() {
        LOGGER.debug("Create ConfigurationContext ...");
    }
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    

    void init() {
        LOGGER.debug("Initialize ConfigurationContext ...");
        String webappDirectory = servletContext.getRealPath("/");
        parsePreConfiguredServices(webappDirectory + "ds");
        XSL_DIR = webappDirectory + File.separator + "xslt" + File.separator;
        CACHE_DIR = webappDirectory + "cache" + File.separator;
        GEN_DIR = webappDirectory + "generated" + File.separator;
        IMAGE_SERVICE = getMandatoryParameterValue("IMAGE_SERVICE");
        ZIP_POSTFIX = getMandatoryParameterValue("ZIP_POSTFIX");
        GEN_URL = getMandatoryParameterValue("GENERATE_URL");
        COPYRIGHT = getMandatoryParameterValue("COPYRIGHT");
        try {
            // parameters which have to be parsed
            THREAD_POOL_SIZE = new Integer(getMandatoryParameterValue("THREAD_POOL_SIZE")).intValue();
            SERVER_TIMEOUT = new Long(getMandatoryParameterValue("SERVER_TIMEOUT")).longValue();
            IS_DEV_MODE = new Boolean(getMandatoryParameterValue("DEV_MODE")).booleanValue();
            FACADE_COMPRESSION = new Boolean(getMandatoryParameterValue("FACADE_COMPRESSION")).booleanValue();
            STATISTICS_INTERVAL = new Integer(getMandatoryParameterValue("STATISTICS_INTERVAL")).intValue();
            STARTUP_DELAY = new Integer(getMandatoryParameterValue("STARTUP_DELAY")).intValue();
            TOOLTIP_MIN_COUNT = new Integer(getMandatoryParameterValue("TOOLTIP_MIN_COUNT")).intValue();
            NO_DATA_VALUES = getNoDataValues(getMandatoryParameterValue("NO_DATA_VALUES"));
        }
        catch (Exception e) {
            LOGGER.error("Could not read context parameter", e);
        }

        GEN_DIR_ZIP = GEN_DIR + "/zipped";
        // USE_DEVEL_CACHING = IS_DEV_MODE;

        LOGGER.info("INITIALIZED SERVER APPLICATION SUCESSFULLY");
        if (IS_DEV_MODE) {
            LOGGER.info("GOING INTO DEV MODE!");
        }
        Statistics.scheduleStatisticsLog(STATISTICS_INTERVAL);
    }

    private void parsePreConfiguredServices(String dsDirectory) {
        try {
            File sosDataSource = new File(dsDirectory + File.separator + "sos-instances.data.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(sosDataSource, new SosInstanceContentHandler());
        }
        catch (Exception e) {
            LOGGER.error("Could not parse preconfigured sos instances.", e);
        }
    }

    /**
     * Gets the value of the passed in parameter which is considered to be mandatory. If a parameter is
     * missing an exception is being thrown. If the parameter was found but empty a WARN message is logged.
     * 
     * @param parameter
     *        the name of the parameter
     * @return the resolved parameter value
     * @throws IllegalStateException
     *         if no parameter (null or empty) could be resolved.
     */
    private String getMandatoryParameterValue(String parameter) {
        String value = servletContext.getInitParameter(parameter);
        if (value == null) {
            throw new IllegalStateException("Parameter '" + parameter + "' was invalid!");
        }
        if (value.isEmpty()) {
            LOGGER.warn("Empty parameter value for parameter {}.", parameter);
        }
        LOGGER.info("Set mandatory parameter {}={}.", parameter, value.trim());
        return value.trim();
    }

    /**
     * Gets the value of the passed in parameter which is considered to be optional. Logs an INFO message if
     * parameter was not set, i.e. that a default value is being used instead. Logs a WARN message if
     * parameter is empty.
     * 
     * @param parameter
     *        an optional parameter value.
     * @return the parameter value or <code>null</code> if parameter was not defined.
     */
    public String getOptionalParameterValue(String parameter) {
        String value = servletContext.getInitParameter(parameter);
        if (value == null) {
            LOGGER.info("Using default of parameter {}.", parameter);
        }
        if (value.isEmpty()) {
            LOGGER.warn("Empty parameter value for parameter {}.", parameter);
        }
        LOGGER.info("Set optional parameter {}={}.", parameter, value.trim());
        return value;
    }

    private List<String> getNoDataValues(String noDatas) {
        List<String> values = new ArrayList<String>();
        if (noDatas.length() > 1) {
            String[] seperatedNoDatas = noDatas.split(",");
            for (String sepNoData : seperatedNoDatas) {
                try {
                    sepNoData = sepNoData.trim()
                            .replaceAll("\n", "")
                            .replaceAll("\t", "")
                            .replaceAll(" ", "");
                    values.add(sepNoData);
                }
                catch (NumberFormatException e) {
                    LOGGER.error("NumberFormatException in the NoDataValues");
                }
            }
        }
        return values;
    }

    
    
    

    public synchronized static Map<String, SOSMetadata> getServiceMetadatas() {
        return serviceMetadatas;
    }

    public synchronized static Collection<SOSMetadata> getSOSMetadatas() {
        List<SOSMetadata> sosMetadatas = new ArrayList<SOSMetadata>();
        for (SOSMetadata metadata : serviceMetadatas.values()) {
            sosMetadatas.add(metadata);
        }
        return sosMetadatas;
    }

    public synchronized static SOSMetadata getSOSMetadata(String url) {
        url = url.trim();
        if (isMetadataAvailable(url)) {
            return (SOSMetadata) getServiceMetadatas().get(url);
        }
        try {
            if (containsServiceMetadata(url)) {
                SOSMetadata metadata = getServiceMetadatas().get(url);
                MetadataHandler handler = createSosMetadataHandler(metadata);
                handler.performMetadataCompletion(url, getVersion(url));
                if ( !metadata.hasDonePositionRequest()) {
                    SosMetadataUpdate.updateService(url);
                }
                return metadata;
            } else {
                // try to get metadata with default SOS version.
                SOSMetadata metadata = new SOSMetadata(url, url, DEFAULT_SOS_VERSION);
                serviceMetadatas.put(url, metadata);
                return getServiceMetadatas().get(url); // repeat call
            }
        }
        catch (Exception e) {
            // throw new RuntimeException("Error building server metadata", e);
            LOGGER.error("Error building server metadata", e);
            return null; // TODO do not return null
        }
    }

    @SuppressWarnings("unchecked")
    private static MetadataHandler createSosMetadataHandler(SOSMetadata metadata) {
        String handler = metadata.getSosMetadataHandler();
        if (handler == null) {
            LOGGER.info("Using default SOS metadata handler for '{}'", metadata.getServiceUrl());
            return new DefaultMetadataHandler(metadata);
        }
        else {
            try {
                Class<MetadataHandler> clazz = (Class<MetadataHandler>) Class.forName(handler);
                Constructor<MetadataHandler> constructor = clazz.getConstructor(SOSMetadata.class);
                return constructor.newInstance(metadata);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find metadata handler class.", e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("Invalid metadata handler constructor. ", e);
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Could not create metadata handler.", e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Not allowed to create metadata handler.", e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Instantiation of metadata handler failed.", e);
            }
        }
    }

    public static boolean isMetadataAvailable(String sosURL) {
        return containsServiceMetadata(sosURL) && serviceMetadatas.get(sosURL).isInitialized();
    }

    public static boolean containsServiceMetadata(String sosURL) {
        return serviceMetadatas.containsKey(sosURL) && serviceMetadatas.get(sosURL) != null;
    }

    public static boolean containsServiceInstance(String instance) {
        return getSOSMetadataForItemName(instance) != null;
    }

    /**
     * @param itemName
     *        the configured item name of the SOS.
     * @return the associated {@link SOSMetadata} or <code>null</code> if not found.
     */
    public static SOSMetadata getSOSMetadataForItemName(String itemName) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            if (metadata.getConfiguredItemName().equals(itemName)) {
                return metadata;
            }
        }
        return null;
    }

    private static String getVersion(String sosURL) throws TimeoutException, Exception {
        SOSMetadata serviceMetadata = serviceMetadatas.get(sosURL);
        if (serviceMetadata != null) {
            return serviceMetadata.getVersion();
        }
        else {
            return getSOSMetadata(sosURL).getVersion();
        }
    }

    public static void initializeMetadata(SOSMetadata metadata) {
        SOSMetadata old = serviceMetadatas.put(metadata.getServiceUrl(), metadata);
        LOGGER.debug("Replace old metadata for: " + old);
        metadata.setInitialized(true);
    }

    public static void addNewSOSMetadata(SOSMetadata metadata) {
        try {
            String serviceURL = metadata.getServiceUrl();
            LOGGER.debug(String.format("Add new SOS metadata for '%s' ", serviceURL));
            serviceMetadatas.put(serviceURL, metadata);
            // SosMetadataUpdate.loadLocation(metadata.getId());
        }
        catch (Exception e) {
            LOGGER.error("Could not load SOS from " + metadata, e);
        }
    }

    public static Map<String, SOSMetadata> updateSOSMetadata() {
        LOGGER.debug("Update protected services");
        Map<String, SOSMetadata> updatedMetadatas = new HashMap<String, SOSMetadata>();
        for (String metadataKey : serviceMetadatas.keySet()) {
            SOSMetadata sosMetadata = serviceMetadatas.get(metadataKey);
            if (sosMetadata.isProtectedService()) {
                try {
                    MetadataHandler metadataHandler = ConfigurationContext.createSosMetadataHandler(sosMetadata);
                    SOSMetadata updatedMetadata = metadataHandler.updateMetadata(sosMetadata);
                    updatedMetadatas.put(updatedMetadata.getServiceUrl(), updatedMetadata);
                    LOGGER.debug("Update metadata for service with url '{}'", updatedMetadata.getServiceUrl());
                }
                catch (Exception e) {
                    LOGGER.error("Could not update {} ", sosMetadata, e);
                }
            }
        }
        LOGGER.debug("Update #{} protected services", updatedMetadatas.size());
        return updatedMetadatas;
    }
}
