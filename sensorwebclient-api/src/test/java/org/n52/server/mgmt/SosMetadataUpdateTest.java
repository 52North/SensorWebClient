/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.server.mgmt.SosMetadataUpdate;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

public class SosMetadataUpdateTest {
    
    private static final String validServiceUrl = "http://my.server.net/52n-SOS-Vx.x/sos";
    
    private File temporalCacheDirectory;
    
    private String cacheTargetFile;
    
    @Before
    public void setUp() throws Exception {
        URL resource = getClass().getResource("/");
        ConfigurationContext.CACHE_DIR = resource.getFile();
        temporalCacheDirectory = new File(ConfigurationContext.CACHE_DIR);
        cacheTargetFile = temporalCacheDirectory + File.separator + "meta_my.server.net_52n-SOS-Vx.x_sos";
    }
    
    @After
    public void tearDown() throws IOException {
//        boolean doesExist = temporalCacheDirectory.exists();
//        if (doesExist && !temporalCacheDirectory.delete()) {
//            throw new IOException("Could not delete temporal cache directory.");
//        }
    }

    @Test
    public void testCreatePostfix() {
        String postfix = SosMetadataUpdate.createPostfix(validServiceUrl);
        assertEquals("my.server.net_52n-SOS-Vx.x_sos", postfix);
    }
    
    @Test
    public void testGetCacheTarget() {
        File expected = new File(cacheTargetFile);
        File cacheTarget = SosMetadataUpdate.getCacheTarget(validServiceUrl);
        assertEquals(expected.getAbsoluteFile(), cacheTarget.getAbsoluteFile());
    }
    
    @Test
    public void testPrepareCacheTarget() throws IOException {
        SosMetadataUpdate.prepareCacheTargetDirectory();
        assertTrue(temporalCacheDirectory.exists());
    }
    
    //@Test
    public void testCacheMetadata() throws Exception {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        SOSMetadata metadata = builder.addServiceURL(validServiceUrl).addServiceVersion("1.0.0").build();
//        SosMetadataUpdate.cacheMetadata(temporalCacheDirectory, validServiceUrl);
        // TODO have to create mockup to test serialization?
    }
    
}
