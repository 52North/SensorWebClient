package org.n52.shared.requests.query;

import org.n52.shared.requests.query.builder.FeatureQueryRequestBuilder;

public class FeatureQuery extends QueryRequest {

	private static final long serialVersionUID = -1750426384949742997L;
	
	public FeatureQuery() {
		// for serialization
	}
	
	public FeatureQuery(FeatureQueryRequestBuilder featureQueryRequestBuilder) {
		super(featureQueryRequestBuilder);
	}

}
