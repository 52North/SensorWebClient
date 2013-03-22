package org.n52.shared.requests.query.builder;

import org.n52.shared.requests.query.FeatureQuery;

public class FeatureQueryRequestBuilder extends QueryRequestBuilder {

	@Override
	public FeatureQuery build() {
		return new FeatureQuery(this);
	}

}
