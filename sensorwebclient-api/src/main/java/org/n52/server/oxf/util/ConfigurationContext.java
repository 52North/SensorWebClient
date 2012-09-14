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

package org.n52.server.oxf.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.n52.server.oxf.util.connector.SOSConnector;
import org.n52.server.oxf.util.logging.Statistics;
import org.n52.server.oxf.util.parser.DefaultSosConnector;
import org.n52.server.updates.SosMetadataUpdate;
import org.n52.shared.Constants;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.ServiceMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationContext extends HttpServlet {

    private static final long serialVersionUID = 88509894213362579L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationContext.class);

    private static Map<String, ServiceMetadata> serviceMetadatas = Collections.synchronizedMap(new HashMap<String, ServiceMetadata>());
    
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
    
    public static List<Double> NO_DATA_VALUES;
    
    @Override
    public void init() throws ServletException {
		String webappDirectory = getServletContext().getRealPath("/");
		parsePreConfiguredServices(webappDirectory + "ds");
        XSL_DIR = webappDirectory + File.separator + "xslt" + File.separator;
        CACHE_DIR = webappDirectory + "cache" + File.separator;
        GEN_DIR = webappDirectory + "generated" + File.separator;
        IMAGE_SERVICE = getAndCheckInitParameter("IMAGE_SERVICE");
        ZIP_POSTFIX = getAndCheckInitParameter("ZIP_POSTFIX");
        GEN_URL = getAndCheckInitParameter("GENERATE_URL");
        COPYRIGHT = getAndCheckInitParameter("COPYRIGHT");
        try {
            // parameters which have to be parsed
            THREAD_POOL_SIZE = new Integer(getAndCheckInitParameter("THREAD_POOL_SIZE")).intValue();
            SERVER_TIMEOUT = new Long(getAndCheckInitParameter("SERVER_TIMEOUT")).longValue();
            IS_DEV_MODE = new Boolean(getAndCheckInitParameter("DEV_MODE")).booleanValue();
            FACADE_COMPRESSION = new Boolean(getAndCheckInitParameter("FACADE_COMPRESSION")).booleanValue();
            STATISTICS_INTERVAL = new Integer(getAndCheckInitParameter("STATISTICS_INTERVAL")).intValue();
            STARTUP_DELAY = new Integer(getAndCheckInitParameter("STARTUP_DELAY")).intValue();
            TOOLTIP_MIN_COUNT = new Integer(getAndCheckInitParameter("TOOLTIP_MIN_COUNT")).intValue();
            NO_DATA_VALUES = getNoDataValues(getAndCheckInitParameter("NO_DATA_VALUES"));
        }
        catch (Exception e) {
            LOGGER.error("Could not read context parameter", e);
        }

        GEN_DIR_ZIP = GEN_DIR + "/zipped";
        USE_DEVEL_CACHING = IS_DEV_MODE;

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
    	} catch (Exception e) {
    		LOGGER.error("Could not parse preconfigured sos instances.", e);
    	}
    }

    /**
     * @param initParameter
     *        the name of the initParameter
     * @return the resolved parameter
     * @throws IllegalStateException
     *         if no parameter (null or empty) could be resolved.
     */
    private String getAndCheckInitParameter(String initParameter) {
        String p = getServletContext().getInitParameter(initParameter);
        if (p == null) {
            String msg = String.format("Parameter '%s' was invalid!", initParameter);
            throw new IllegalStateException(msg);
        }
        if (p.isEmpty()) {
			LOGGER.warn(String.format("Parameter '%s' is empty!", initParameter));
		}
        LOGGER.debug(String.format("initParameter '%s' => " + p, initParameter.trim()));
        return p.trim();
    }

    private List<Double> getNoDataValues(String noDatas) {
        List<Double> values = new ArrayList<Double>();
        if (noDatas.length() > 1) {
            String[] seperatedNoDatas = noDatas.split(",");
            for (String sepNoData : seperatedNoDatas) {
                try {
                    sepNoData = sepNoData.trim()
                        .replaceAll("\n", "")
                        .replaceAll("\t", "")
                        .replaceAll(" ", "");
                    values.add(new Double(sepNoData));
                }
                catch (NumberFormatException e) {
                    LOGGER.error("NumberFormatException in the NoDataValues");
                }
            }
        }
        return values;
    }

    public synchronized static Map<String, ServiceMetadata> getServiceMetadatas() {
        return serviceMetadatas;
    }
    
    public synchronized static Collection<SOSMetadata> getSOSMetadatas() {
    	List<SOSMetadata> sosMetadatas = new ArrayList<SOSMetadata>();
    	for (ServiceMetadata metadata : serviceMetadatas.values()) {
			if (metadata instanceof SOSMetadata) {
				sosMetadatas.add((SOSMetadata)metadata);
			}
		}
    	return sosMetadatas;
    }

    public synchronized static SOSMetadata getSOSMetadata(String url) {
        if (isMetadataAvailable(url)) {
            return (SOSMetadata) getServiceMetadatas().get(url);
        }
        try {
            DefaultSosConnector parser = new DefaultSosConnector();
            SOSMetadataResponse resp = parser.buildUpServiceMetadata(url, Constants.DEFAULT_SOS_VERSION);
            return resp.getServiceMetadata();
        }
        catch (Exception e) {
            // throw new RuntimeException("Error building server metadata", e);
            LOGGER.error("Error building server metadata", e);
            return null; // TODO do not return null
        }
    }

    public synchronized static ServiceMetadata getServiceMetadata(String url) throws Exception {
        url = url.trim();
        if (!containsServiceMetadata(url)) {
            new URL(url);
            serviceMetadatas.put(url, new SOSMetadata(url, url, Constants.DEFAULT_SOS_VERSION));
//            throw new IllegalArgumentException("Unkown service url!");
        }
        if (isMetadataAvailable(url)) {
            return getServiceMetadatas().get(url);
        } else {
        	String connectorClassString = serviceMetadatas.get(url).getConnector();
        	if (connectorClassString == null) {
                LOGGER.error("No connector configured for SOS '{}'", url);
        	    throw new IllegalStateException("No connector found for SOS.");
			}
			Class<SOSConnector> connectorClass = (Class<SOSConnector>) Class.forName(connectorClassString);
        	Constructor<SOSConnector> constructor = connectorClass.getConstructor();
        	SOSConnector connector = constructor.newInstance();
        	connector.buildUpServiceMetadata(url, getVersion(url));
            ServiceMetadata sm = ConfigurationContext.getServiceMetadatas().get(url);
            if (sm instanceof SOSMetadata) {
                SOSMetadata meta = (SOSMetadata) sm;
                if (!meta.hasDonePositionRequest()) {
                    SosMetadataUpdate.updateService(url);
                }
            }
            return sm;
        }
    }

    public static boolean isMetadataAvailable(String sosURL) {
        return containsServiceMetadata(sosURL) && serviceMetadatas.get(sosURL).isInitialized();
    }
    
    public static boolean containsServiceMetadata(String sosURL) {
        return serviceMetadatas.containsKey(sosURL) && serviceMetadatas.get(sosURL) != null;
    }
    
    /**
     * @param itemName the configured item name of the SOS.
     * @return the associated {@link SOSMetadata} or <code>null</code> if not found.
     */
    public static SOSMetadata getSOSMetadataForItemName(String itemName) {
        for (SOSMetadata metadata  : getSOSMetadatas()) {
            if (metadata.getConfiguredItemName().equals(itemName)) {
                return metadata;
            }
        }
        return null;
    }
    
    private static String getVersion(String sosURL) throws TimeoutException, Exception {
        ServiceMetadata serviceMetadata = serviceMetadatas.get(sosURL);
        if (serviceMetadata != null) {
            return serviceMetadata.getVersion();
        } else {
            return getServiceMetadata(sosURL).getVersion();
        }
    }

    public static void initializeMetadata(ServiceMetadata metadata) {
        ServiceMetadata old = serviceMetadatas.put(metadata.getId(), metadata);
        LOGGER.debug("Replace old serviceMetadata: " + old);
        metadata.setInitialized(true);
    }

	public static void addNewSOSMetadata(SOSMetadata metadata) {
		try {
			String serviceURL = metadata.getId();
			LOGGER.debug(String.format("Add new SOS metadata for '%s' ", serviceURL));
			serviceMetadatas.put(serviceURL, metadata);
//			SosMetadataUpdate.loadLocation(metadata.getId());
		} catch (Exception e) {
			LOGGER.error("Could not load SOS from " + metadata, e);
		}
	}
}
