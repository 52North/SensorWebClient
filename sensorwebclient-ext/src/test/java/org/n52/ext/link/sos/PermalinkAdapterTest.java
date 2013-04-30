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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
