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
package org.n52.server.mgmt;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(SosMetadataUpdate.class);

    public static void updateSosServices(Iterable<String> sosServices) throws Exception {
        long startTimeInMillis = System.currentTimeMillis();
        performServiceUpdateFor(sosServices);
        float secondsElapsed = getSecondsElapsedSince(startTimeInMillis);
        LOGGER.info("Cache update took {} seconds.", secondsElapsed);
    }

    private static void performServiceUpdateFor(Iterable<String> sosUrls) throws Exception {
        for (String url : sosUrls) {
            try {
                updateService(url);
            } catch (Exception e) {
                LOGGER.error("Could not cache metadata for service " + url, e);
            }
        }
    }

    private static float getSecondsElapsedSince(long since) {
        long durationInMilliseconds = System.currentTimeMillis() - since;
        return (durationInMilliseconds / 1000f); // to seconds
    }

    public static void updateService(String serviceUrl) throws Exception {
        LOGGER.info("Update service metadata for '{}'", serviceUrl);
        File cache = getCacheTarget(serviceUrl);
        if (cache.exists() && cache.length() != 0) {
            try {
                loadMetadataFromCache(cache);
            } catch (Exception e) {
                LOGGER.info("Could not read cache. Recreate cache ...", e);
                cache.delete();
                cacheMetadata(cache, serviceUrl);
            }
        } else {
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
     *
     * @param url the service url
     * @return a service identifier based on the service url
     */
    protected static String createPostfix(String url) {
        if (url.startsWith("http://")) {
            url = url.substring("http://".length());
        }
        return url.replaceAll("/", "_").replaceAll("\\?", "_").replaceAll(":", "_").replaceAll("@", "_");
    }

    protected static void loadMetadataFromCache(File file) throws IOException, ClassNotFoundException {
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        ObjectInput input = new ObjectInputStream(stream);
        try {
            // deserialize the List
            SOSMetadata metadata = (SOSMetadata) input.readObject();
            ConfigurationContext.addNewSOSMetadata(metadata);
        } finally {
            input.close();
        }
    }

    /**
     * Checks if the dedicated caching target exists already. The target is defined as configurable parameter in
     * {@link ConfigurationContext#CACHE_DIR}. If not all necessary directories are created.
     *
     * @throws IOException if subdirectories could not be created.
     */
    protected static void prepareCacheTargetDirectory() throws IOException {
        File cacheDirectory = getCacheDir();
        if (!cacheDirectory.exists() && !cacheDirectory.mkdirs()) {
            throw new IOException("Unable to create cache directory.");
        }
    }

    protected static void cacheMetadata(File cache, String serviceUrl) throws Exception {
        OutputStream os = new FileOutputStream(cache);
        ObjectOutput serializer = new ObjectOutputStream(new BufferedOutputStream(os));
        try {
            serializer.writeObject(getSOSMetadata(serviceUrl));
        } finally {
            serializer.close();
            os.close();
        }
    }

    public static void invalidateCache() throws IOException {
        File cacheDir = getCacheDir();
        if (cacheDir.exists()) {
            for (File cacheFile : cacheDir.listFiles()) {
                if ( !cacheFile.delete()) {
                    throw new IOException("Could not delete '" + cacheDir.getAbsolutePath() + "'.");
                }
            }
        }
    }

    private static File getCacheDir() {
        return new File(ConfigurationContext.CACHE_DIR);
    }

}
