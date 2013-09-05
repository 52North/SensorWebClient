package org.n52.api.v1.srv;

import static org.n52.api.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.api.v1.io.CategoryConverter;
import org.n52.io.v1.data.CategoryOutput;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.web.v1.ctrl.QueryMap;
import org.n52.web.v1.srv.ParameterService;

public class CategoryOutputAdapter implements ParameterService<CategoryOutput> {

	@Override
	public CategoryOutput[] getExpandedParameters(QueryMap map) {
		QueryParameters query = createQueryParameters(map);
        List<CategoryOutput> allCategories = new ArrayList<CategoryOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
			CategoryConverter converter = new CategoryConverter(metadata);
		    allCategories.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allCategories.toArray(new CategoryOutput[0]);
	}

	@Override
	public CategoryOutput[] getCondensedParameters(QueryMap map) {
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
        for (SosTimeseries timeseries : metadata.getTimeseriesRelatedWith(query)) {
        	allCategories.add(timeseries.getCategory());
        }
        return allCategories.toArray(new String[0]);
    }

	@Override
	public CategoryOutput[] getParameters(String[] categories) {
		return getParameters(categories, QueryMap.createDefaults());
	}

	@Override
    public CategoryOutput[] getParameters(String[] categories, QueryMap query) {
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
		return getParameter(categoryId, QueryMap.createDefaults());
	}

    @Override
    public CategoryOutput getParameter(String categoryId, QueryMap query) {
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
