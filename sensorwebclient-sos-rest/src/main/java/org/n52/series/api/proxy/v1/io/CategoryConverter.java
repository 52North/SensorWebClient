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
package org.n52.series.api.proxy.v1.io;

import org.n52.io.response.v1.CategoryOutput;
import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashGenerator;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public class CategoryConverter extends OutputConverter<Category, CategoryOutput> {

	public CategoryConverter(SOSMetadata metadata) {
		super(metadata);
	}

	@Override
	public CategoryOutput convertExpanded(Category category) {
		CategoryOutput convertedCategory = convertCondensed(category);
		convertedCategory.setService(convertCondensedService());
		return convertedCategory;
	}

	@Override
	public CategoryOutput convertCondensed(Category category) {
		CategoryOutput convertedCategory = new CategoryOutput();
		convertedCategory.setId(category.getGlobalId());
		convertedCategory.setLabel(category.getLabel());
		return convertedCategory;
	}

	public String generateId(String category) {
		IdGenerator idGenerator = new MD5HashGenerator("cat_");
		return idGenerator.generate(new String[]{category, getMetadata().getServiceUrl()});
	}

	public CategoryOutput getCategorieByID(String categoryId) {
		SosTimeseries[] timeseries = getMetadata().getMatchingTimeseries(createEmptyFilterQuery());
		for (SosTimeseries sosTimeseries : timeseries) {
			if(sosTimeseries.getCategory().getGlobalId().equals(categoryId)) {
				return convertExpanded(sosTimeseries.getCategory());
			}
		}
		return null;
	}

}
