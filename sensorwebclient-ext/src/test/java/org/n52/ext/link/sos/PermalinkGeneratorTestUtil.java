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

import java.util.ArrayList;
import java.util.List;

import org.n52.ext.link.AccessLinkCompressor;
import org.n52.ext.link.AccessLinkFactory;
import org.n52.ext.link.sos.PermalinkCompressor;
import org.n52.ext.link.sos.TimeSeriesParameters;
import org.n52.ext.link.sos.TimeSeriesPermalinkBuilder;

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