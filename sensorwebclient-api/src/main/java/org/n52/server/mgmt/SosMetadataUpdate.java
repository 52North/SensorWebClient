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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosMetadataUpdate {

    private static Logger LOGGER = LoggerFactory.getLogger(SosMetadataUpdate.class);

    public static void updateSosServices(Iterable<String> sosServices) throws Exception {
        long startTimeInMillis = System.currentTimeMillis();
        performServiceUpdateFor(sosServices);
        float secondsElapsed = getSecondsElapsedSince(startTimeInMillis);
        LOGGER.debug("Cache update took {} seconds.", secondsElapsed);
    }

    private static void performServiceUpdateFor(Iterable<String> sosUrls) throws Exception {
        for (String url : sosUrls) {
            try {
                updateService(url);
            } catch (Exception e) {
                LOGGER.error("Could not cache metadata for service " + url, e);
                continue; // ignore service and try next one.
            }
        }
    }

    private static float getSecondsElapsedSince(long since) {
        long durationInMilliseconds = System.currentTimeMillis() - since;
        return (durationInMilliseconds / 1000f); // to seconds
    }

    public static void updateService(String serviceUrl) throws Exception {
        LOGGER.debug("Update service metadata for '{}'", serviceUrl);
        File cache = getCacheTarget(serviceUrl);
        if (isCacheAvailable(cache)) {
            try {
                loadMetadataFromCache(cache);
            } catch (IOException e) {
                LOGGER.info("Could not read cache. Recreate cache ...", e);
                cache.delete();
                cacheMetadata(cache, serviceUrl);
            }
        }
        else {
            prepareCacheTargetDirectory();
            cacheMetadata(cache, serviceUrl);
        }
    }

    protected static File getCacheTarget(String serviceUrl) {
        String postfix = createPostfix(serviceUrl);
        return new File(generateCacheFilePath(postfix));
    }

    private static String generateCacheFilePath(String postfix) {
        return ConfigurationContext.CACHE_DIR + "/meta_" + postfix;
    }

    /**
     * Creates a postfix for the cache files by given url
     */
    protected static String createPostfix(String url) {
        if (url.startsWith("http://")) {
            url = url.substring("http://".length());
        }
        return url.replaceAll("/", "_").replaceAll("\\?", "_");
    }

    private static boolean isCacheAvailable(File file) {
        return /*ConfigurationContext.USE_DEVEL_CACHING &&*/ file.exists();
    }

    protected static void loadMetadataFromCache(File file) throws IOException, ClassNotFoundException {
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        ObjectInput input = new ObjectInputStream(stream);
        try {
            // deserialize the List
            SOSMetadata metadata = (SOSMetadata) input.readObject();
            ConfigurationContext.addNewSOSMetadata(metadata);
        }
        finally {
            input.close();
        }
    }

    /**
     * Checks if the dedicated caching target exists already. The target is defined as configurable parameter
     * in {@link ConfigurationContext#CACHE_DIR}. If not all necessary directories are created.
     * 
     * @throws IOException if subdirectories could not be created.
     */
    protected static void prepareCacheTargetDirectory() throws IOException {
        File cacheDirectory = new File(ConfigurationContext.CACHE_DIR);
        if ( !cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
            throw new IOException("Unable to create cache directory.");
        }
    }

    protected static void cacheMetadata(File cache, String serviceUrl) throws Exception {
        OutputStream os = new FileOutputStream(cache);
        ObjectOutput serializer = new ObjectOutputStream(new BufferedOutputStream(os));
        try {
            serializer.writeObject(getServiceMetadataFor(serviceUrl));
        }
        finally {
            serializer.close();
        }
    }

    protected static SOSMetadata getServiceMetadataFor(String serviceUrl) throws Exception {
        return ConfigurationContext.getServiceMetadata(serviceUrl);
    }

}
