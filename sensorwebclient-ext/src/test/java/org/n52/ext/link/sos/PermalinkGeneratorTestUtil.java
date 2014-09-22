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
package org.n52.ext.link.sos;

import java.util.ArrayList;
import java.util.List;

import org.n52.ext.link.AccessLinkCompressor;
import org.n52.ext.link.AccessLinkFactory;

public final class PermalinkGeneratorTestUtil {
    
	static final String MALFORMED_BASE_URL = "http://malformed:url/context";
	
	static final String BASE_URL = "http://example.org:8943/context";
	
	private AccessLinkFactory permalinkGenerator;

	private PermalinkCompressor compressedPermalinkGenerator;

	private List<TimeSeriesParameters> addedTimeSeriesParameters;

	public PermalinkGeneratorTestUtil() {
		TimeSeriesPermalinkBuilder builder = new TimeSeriesPermalinkBuilder();
		String proc = "urn:ogc:generalizationMethod:IFGI:SkipEverySecond";
		addedTimeSeriesParameters = new ArrayList<TimeSeriesParameters>();
		addedTimeSeriesParameters.add(new TimeSeriesParameters("url1", "1.0.0", "off1", proc, "phen1", "feat1"));
		addedTimeSeriesParameters.add(new TimeSeriesParameters("url1", "1.0.0", "off2", proc, "phen2", "feat2"));
		addedTimeSeriesParameters.add(new TimeSeriesParameters("url3", "2.0.0", "off3", proc, "phen3", "feat1"));
		for (TimeSeriesParameters parameters : addedTimeSeriesParameters) {
			builder.addParameters(parameters);
		}
		permalinkGenerator = builder.build();
		compressedPermalinkGenerator = new PermalinkCompressor(builder);
	}
	
	public List<TimeSeriesParameters> getAddedTimeSeriesParameters() {
		return addedTimeSeriesParameters;
	}

	AccessLinkFactory getPermalinkGenerator() {
		return permalinkGenerator;
	}

	public AccessLinkCompressor getCompressedPermalinkGenerator() {
		return compressedPermalinkGenerator;
	}
}