package org.n52.api.v1.io;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashIdGenerator;
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
		IdGenerator idGenerator = new MD5HashIdGenerator("cat_");
		return idGenerator.generate(new String[]{category, getMetadata().getServiceUrl()});
	}

	public CategoryOutput getCategorieByID(String categoryId) {
		SosTimeseries[] timeseries = getMetadata().getTimeseriesRelatedWith(QueryParameters.createEmptyFilterQuery());
		for (SosTimeseries sosTimeseries : timeseries) {
			if (generateId(sosTimeseries.getCategory()).equals(categoryId)) {
				return convertExpanded(sosTimeseries.getCategory());
			}
		}
		return null;
	}

}
