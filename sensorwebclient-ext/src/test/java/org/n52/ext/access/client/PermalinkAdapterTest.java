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
package org.n52.ext.access.client;

import static junit.framework.Assert.*;
import static org.n52.ext.access.client.PermalinkGeneratorTestUtil.BASE_URL;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.ext.access.AccessLinkFactory;

public class PermalinkAdapterTest {

	private PermalinkGeneratorTestUtil testUtil = new PermalinkGeneratorTestUtil();
	
	private AccessLinkFactory permalinkGenerator;

	private PermalinkAdapter permalinkAdapter;

	private List<TimeSeriesParameters> addedTimeSeries;

	@Before
	public void setUp() throws Exception {
		this.permalinkGenerator = testUtil.getPermalinkGenerator();
		String permalink = permalinkGenerator.createAccessURL(BASE_URL);
		this.permalinkAdapter = new PermalinkAdapter(permalink);
		this.addedTimeSeries = testUtil.getAddedTimeSeriesParameters();
	}
	
	@Test
	public void testCreatingWithEmptyQueryString() {
	    assertNotNull(new PermalinkAdapter("http://foobar.de/?"));
        assertNotNull(new PermalinkAdapter("http://foobar.de/"));
	}
	
	@Test
	public void testGetServiceURLs() {
		Collection<String> parsedServiceURLs = permalinkAdapter.getServiceURLs();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String serviceURL = timeSeries.getServiceURL();
			assertTrue("Service not found: " + serviceURL, parsedServiceURLs.contains(serviceURL));
		}
	}
	
	@Test
    public void testGetServiceVersions() {
        Collection<String> parsedVersions = permalinkAdapter.getVersions();
        for (TimeSeriesParameters timeSeries : addedTimeSeries) {
            // TODO add versions test
        }
    }

	@Test
	public void testGetOfferings() {
		Collection<String> parsedOfferings = permalinkAdapter.getOfferings();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String offering = timeSeries.getOffering();
			assertTrue("Offering not found: " + offering, parsedOfferings.contains(offering));
		}
	}

	@Test
	public void testGetProcedures() {
		Collection<String> parsedProcedures = permalinkAdapter.getProcedures();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String procedure = timeSeries.getProcedure();
			assertTrue("Procedure not found:" + procedure, parsedProcedures.contains(procedure));
		}
	}

	@Test
	public void testGetPhenomenons() {
		Collection<String> parsedPhenomenons = permalinkAdapter.getPhenomenons();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String phenomenon = timeSeries.getPhenomenon();
			assertTrue("Phenomenon not found: " + phenomenon, parsedPhenomenons.contains(phenomenon));
		}
	}

	@Test
	public void testGetStations() {
		Collection<String> parsedStations = permalinkAdapter.getStations();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String station = timeSeries.getStation();
			assertTrue("Station not found: " + station, parsedStations.contains(station));
		}
	}

}
