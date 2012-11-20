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

import static org.n52.ext.link.sos.PermalinkParameter.SERVICES;
import static org.n52.ext.link.sos.PermalinkParameter.VERSIONS;
import static org.n52.ext.link.sos.PermalinkParameter.FEATURES;
import static org.n52.ext.link.sos.PermalinkParameter.OFFERINGS;
import static org.n52.ext.link.sos.PermalinkParameter.PROCEDURES;
import static org.n52.ext.link.sos.PermalinkParameter.PHENOMENONS;


import org.n52.ext.link.AccessLinkCompressor;

public class PermalinkCompressor extends PermalinkFactory implements AccessLinkCompressor {

    PermalinkCompressor(TimeSeriesPermalinkBuilder builder) {
    	super(builder);
    }
	
    @Override
    public String createCompressedAccessURL(String baseUrl) {
        
        // TODO refactor to a custom permalinkBuilder implementation
        
        queryBuilder.initialize(baseUrl);
        queryBuilder.appendCompressedParameters(buildParameter("", SERVICES), services);
        queryBuilder.appendCompressedParameters(buildParameter("&", VERSIONS), versions);
        queryBuilder.appendCompressedParameters(buildParameter("&", FEATURES), features);
        queryBuilder.appendCompressedParameters(buildParameter("&", OFFERINGS), offerings);
        queryBuilder.appendCompressedParameters(buildParameter("&", PROCEDURES), procedures);
        queryBuilder.appendCompressedParameters(buildParameter("&", PHENOMENONS), phenomenons);
        queryBuilder.appendTimeRangeParameters(timeRange);
        queryBuilder.appendCompressedParameter();
        return queryBuilder.toString();
    }
}
