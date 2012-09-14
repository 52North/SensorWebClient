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
package org.n52.api.access.client;

import static org.n52.api.access.client.PermalinkGeneratorTestUtil.BASE_URL;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.n52.api.access.AccessLinkFactory;

public class PermalinkAdapterTest {

	private PermalinkGeneratorTestUtil testUtil = new PermalinkGeneratorTestUtil();
	
	private AccessLinkFactory permalinkGenerator;

	private PermalinkAdapter permalinkAdapter;

	private List<TimeSeriesParameters> addedTimeSeries;

	@Before
	public void setUp() throws Exception {
		this.permalinkGenerator = testUtil.getPermalinkGenerator();
		URL permalink = permalinkGenerator.createAccessURL(BASE_URL);
		this.permalinkAdapter = new PermalinkAdapter(permalink);
		this.addedTimeSeries = testUtil.getAddedTimeSeriesParameters();
	}
	
	private boolean containsParameter(Iterator<String> parsedValues, String parameter) {
		while (parsedValues.hasNext()) {
			String value = parsedValues.next();
			if (value.equals(parameter)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void testGetServiceURLs() {
		boolean found = false;
		Iterable<String> parsedServiceURLs = permalinkAdapter.getServiceURLs();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String serviceURL = timeSeries.getServiceURL();
			if (containsParameter(parsedServiceURLs.iterator(), serviceURL)) {
				found = true;
				break;
			};
		}
		Assert.assertTrue("ServiceURLs do not match.", found);
	}

	@Test
	public void testGetOfferings() {
		boolean found = false;
		Iterable<String> parsedOfferings = permalinkAdapter.getOfferings();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String offering = timeSeries.getOffering();
			if (containsParameter(parsedOfferings.iterator(), offering)) {
				found = true;
				break;
			};
		}
		Assert.assertTrue("Offerings do not match.", found);
	}

	@Test
	public void testGetProcedures() {
		boolean found = false;
		Iterable<String> parsedProcedures = permalinkAdapter.getProcedures();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String procedure = timeSeries.getProcedure();
			if (containsParameter(parsedProcedures.iterator(), procedure)) {
				found = true;
				break;
			};
		}
		Assert.assertTrue("Procedures do not match.", found);
	}

	@Test
	public void testGetPhenomenons() {
		boolean found = false;
		Iterable<String> parsedPhenomenons = permalinkAdapter.getPhenomenons();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String phenomenon = timeSeries.getPhenomenon();
			if (containsParameter(parsedPhenomenons.iterator(), phenomenon)) {
				found = true;
				break;
			};
		}
		Assert.assertTrue("Phenomenons do not match.", found);
	}

	@Test
	public void testGetStations() {
		boolean found = false;
		Iterable<String> parsedStations = permalinkAdapter.getStations();
		for (TimeSeriesParameters timeSeries : addedTimeSeries) {
			String station = timeSeries.getStation();
			if (containsParameter(parsedStations.iterator(), station)) {
				found = true;
				break;
			};
		}
		Assert.assertTrue("Stations do not match.", found);
	}

}
