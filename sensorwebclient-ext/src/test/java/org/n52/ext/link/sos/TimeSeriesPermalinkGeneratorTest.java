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
package org.n52.ext.link.sos;

import static org.junit.Assert.fail;
import static org.n52.ext.link.sos.PermalinkGeneratorTestUtil.BASE_URL;

import org.junit.Before;
import org.junit.Test;
import org.n52.ext.ExternalToolsException;
import org.n52.ext.link.AccessLinkCompressor;
import org.n52.ext.link.AccessLinkFactory;

public class TimeSeriesPermalinkGeneratorTest {

    private PermalinkGeneratorTestUtil testUtil = new PermalinkGeneratorTestUtil();

    private AccessLinkFactory permalinkGenerator;

    private AccessLinkCompressor compressedPermalinkGenerator;

    @Before
    public void setUp() throws Exception {
        this.permalinkGenerator = testUtil.getPermalinkGenerator();
        this.compressedPermalinkGenerator = testUtil.getCompressedPermalinkGenerator();
    }

    @Test
    public void testAccessURLGeneration() {
        try {
            String permalink = permalinkGenerator.createAccessURL(BASE_URL);
            // TODO test query
            String externalForm = permalink;
            // TODO test if equals to BASE_URL
        }
        catch (ExternalToolsException e) {
            e.printStackTrace();
            fail(String.format("Failed, in spite of valid base URL: '%s'", BASE_URL));
        }

    }

    @Test
    public void testCreateCompressedAccessURL() {
        try {
            String permalink = compressedPermalinkGenerator.createCompressedAccessURL(BASE_URL);

            // TODO finish testing
            // fail("Not yet implemented");
        }
        catch (ExternalToolsException e) {
            e.printStackTrace();
            fail(String.format("Failed, in spite of valid base URL: '%s'", BASE_URL));
        }
    }

}
