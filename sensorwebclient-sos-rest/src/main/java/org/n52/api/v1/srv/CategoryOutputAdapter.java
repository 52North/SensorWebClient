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
package org.n52.api.v1.srv;

import static org.n52.api.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.CategoryConverter;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.CategoryOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.web.v1.srv.ParameterService;

public class CategoryOutputAdapter implements ParameterService<CategoryOutput> {

	@Override
	public CategoryOutput[] getExpandedParameters(IoParameters map) {
		QueryParameters query = createQueryParameters(map);
        List<CategoryOutput> allCategories = new ArrayList<CategoryOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			CategoryConverter converter = new CategoryConverter(metadata);
		    allCategories.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allCategories.toArray(new CategoryOutput[0]);
	}

	@Override
	public CategoryOutput[] getCondensedParameters(IoParameters map) {
		QueryParameters query = createQueryParameters(map);
        List<CategoryOutput> allCategories = new ArrayList<CategoryOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
        	CategoryConverter converter = new CategoryConverter(metadata);
            allCategories.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allCategories.toArray(new CategoryOutput[0]);
	}
	
	private String[] filter(SOSMetadata metadata, QueryParameters query) {
	    
	    // TODO consider query
	    
        Set<String> allCategories = new HashSet<String>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
        	allCategories.add(timeseries.getCategory());
        }
        return allCategories.toArray(new String[0]);
    }

	@Override
	public CategoryOutput[] getParameters(String[] categories) {
		return getParameters(categories, IoParameters.createDefaults());
	}

	@Override
    public CategoryOutput[] getParameters(String[] categories, IoParameters query) {
	    List<CategoryOutput> selectedCategories = new ArrayList<CategoryOutput>();
        for (String categoryId : categories) {
            CategoryOutput category = getParameter(categoryId);
            if (category != null) {
                selectedCategories.add(category);
            }
        }
        return selectedCategories.toArray(new CategoryOutput[0]);
    }

    @Override
	public CategoryOutput getParameter(String categoryId) {
		return getParameter(categoryId, IoParameters.createDefaults());
	}

    @Override
    public CategoryOutput getParameter(String categoryId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            CategoryConverter converter = new CategoryConverter(metadata);
            CategoryOutput result = converter.getCategorieByID(categoryId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
