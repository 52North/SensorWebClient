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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.series.api.proxy.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.n52.series.api.proxy.v1.io.CategoryConverter;
import org.n52.io.request.IoParameters;
import org.n52.io.response.OutputCollection;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.v1.CategoryOutput;
import org.n52.sensorweb.spi.ParameterService;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public class CategoryOutputAdapter implements ParameterService<CategoryOutput> {

    private OutputCollection<CategoryOutput> createOutputCollection() {
        return new OutputCollection<CategoryOutput>() {
                @Override
                protected Comparator<CategoryOutput> getComparator() {
                    return ParameterOutput.defaultComparator();
                }
            };
    }
    
	@Override
	public OutputCollection<CategoryOutput> getExpandedParameters(IoParameters map) {
		QueryParameters query = createQueryParameters(map);
        OutputCollection<CategoryOutput> outputCollection = createOutputCollection();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			CategoryConverter converter = new CategoryConverter(metadata);
		    outputCollection.addItems(converter.convertExpanded(filter(metadata, query)));
		}
		return outputCollection;
	}

	@Override
	public OutputCollection<CategoryOutput> getCondensedParameters(IoParameters map) {
		QueryParameters query = createQueryParameters(map);
        OutputCollection<CategoryOutput> outputCollection = createOutputCollection();
        for (SOSMetadata metadata : getSOSMetadatas()) {
        	CategoryConverter converter = new CategoryConverter(metadata);
            outputCollection.addItems(converter.convertCondensed(filter(metadata, query)));
        }
        return outputCollection;
	}

	private Category[] filter(SOSMetadata metadata, QueryParameters query) {

	    // TODO consider query

        Set<Category> allCategories = new HashSet<Category>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
        	allCategories.add(timeseries.getCategory());
        }
        return allCategories.toArray(new Category[0]);
    }

	@Override
	public OutputCollection<CategoryOutput> getParameters(String[] categories) {
		return getParameters(categories, IoParameters.createDefaults());
	}

	@Override
    public OutputCollection<CategoryOutput> getParameters(String[] categories, IoParameters query) {
        OutputCollection<CategoryOutput> outputCollection = createOutputCollection();
        for (String categoryId : categories) {
            CategoryOutput category = getParameter(categoryId);
            if (category != null) {
                outputCollection.addItem(category);
            }
        }
        return outputCollection;
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
