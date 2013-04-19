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
package org.n52.server.updates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.server.oxf.util.ConfigurationContext;
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
    
    @Test
    public void testCacheMetadata() throws Exception {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        SOSMetadata metadata = builder.addServiceURL(validServiceUrl).addServiceVersion("1.0.0").build();
//        SosMetadataUpdate.cacheMetadata(temporalCacheDirectory, validServiceUrl);
        // TODO have to create mockup to test serialization?
    }
    
}
