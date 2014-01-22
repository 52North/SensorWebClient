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
package org.n52.api.v1.io;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashGenerator;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public class CategoryConverter extends OutputConverter<String, CategoryOutput> {

	public CategoryConverter(SOSMetadata metadata) {
		super(metadata);
	}

	@Override
	public CategoryOutput convertExpanded(String category) {
		CategoryOutput convertedCategory = convertCondensed(category);
		convertedCategory.setService(convertCondensedService());
		return convertedCategory;
	}

	@Override
	public CategoryOutput convertCondensed(String category) {
		CategoryOutput convertedCategory = new CategoryOutput();
		convertedCategory.setId(generateId(category));
		convertedCategory.setLabel(category);
		return convertedCategory;
	}

	public String generateId(String category) {
		IdGenerator idGenerator = new MD5HashGenerator("cat_");
		return idGenerator.generate(new String[]{category, getMetadata().getServiceUrl()});
	}

	public CategoryOutput getCategorieByID(String categoryId) {
		SosTimeseries[] timeseries = getMetadata().getMatchingTimeseries(createEmptyFilterQuery());
		for (SosTimeseries sosTimeseries : timeseries) {
			if (generateId(sosTimeseries.getCategory()).equals(categoryId)) {
				return convertExpanded(sosTimeseries.getCategory());
			}
		}
		return null;
	}

}
