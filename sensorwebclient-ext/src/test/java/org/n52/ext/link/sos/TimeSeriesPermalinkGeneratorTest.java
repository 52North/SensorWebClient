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

package org.n52.ext.link.sos;

import static org.n52.ext.link.sos.PermalinkGeneratorTestUtil.BASE_URL;
import junit.framework.Assert;

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
            Assert.fail(String.format("Failed, in spite of valid base URL: '%s'", BASE_URL));
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
            Assert.fail(String.format("Failed, in spite of valid base URL: '%s'", BASE_URL));
        }
    }

}
