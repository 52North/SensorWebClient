/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.n52.ext.link.sos.PermalinkGeneratorTestUtil.BASE_URL;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.ext.link.AccessLinkFactory;

public class PermalinkAdapterTest {

	private PermalinkGeneratorTestUtil testUtil = new PermalinkGeneratorTestUtil();
	
	private AccessLinkFactory permalinkGenerator;

	private PermalinkParser permalinkParser;

	private List<TimeSeriesParameters> addedTimeSeries;

	@Before
	public void setUp() throws Exception {
		permalinkGenerator = testUtil.getPermalinkGenerator();
		String permalink = permalinkGenerator.createAccessURL(BASE_URL);
		permalinkParser = new PermalinkParser(permalink);
		addedTimeSeries = testUtil.getAddedTimeSeriesParameters();
	}
	
	@Test
	public void testCreatingWithEmptyQueryString() {
	    assertNotNull(new PermalinkParser("http://foobar.de/?"));
        assertNotNull(new PermalinkParser("http://foobar.de/"));
	}
	
	@Test
	public void testGetServices() {
		Collection<String> parsedServices = permalinkParser.getServices();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String service = timeSeries.getService();
			assertTrue("Service not found: " + service, parsedServices.contains(service));
		}
	}
	
	@Test
    public void testGetServiceVersions() {
        Collection<String> parsedVersions = permalinkParser.getVersions();
        for (TimeSeriesParameters timeSeries : addedTimeSeries) {
            // TODO add versions test
        }
    }

	@Test
	public void testGetOfferings() {
		Collection<String> parsedOfferings = permalinkParser.getOfferings();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String offering = timeSeries.getOffering();
			assertTrue("Offering not found: " + offering, parsedOfferings.contains(offering));
		}
	}

	@Test
	public void testGetProcedures() {
		Collection<String> parsedProcedures = permalinkParser.getProcedures();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String procedure = timeSeries.getProcedure();
			assertTrue("Procedure not found:" + procedure, parsedProcedures.contains(procedure));
		}
	}

	@Test
	public void testGetPhenomenons() {
		Collection<String> parsedPhenomenons = permalinkParser.getPhenomenons();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String phenomenon = timeSeries.getPhenomenon();
			assertTrue("Phenomenon not found: " + phenomenon, parsedPhenomenons.contains(phenomenon));
		}
	}

	@Test
	public void testGetFeatures() {
		Collection<String> parsedFeatures = permalinkParser.getFeatures();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String feature = timeSeries.getFeature();
			assertTrue("Station not found: " + feature, parsedFeatures.contains(feature));
		}
	}

}
