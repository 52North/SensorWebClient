package org.n52.shared.requests.query;

import org.n52.shared.requests.query.builder.OfferingQueryRequestBuilder;

public class OfferingQuery extends QueryRequest {

	private static final long serialVersionUID = -1750426384949742997L;
	
	public OfferingQuery() {
		// for serialization
	}
	
	public OfferingQuery(OfferingQueryRequestBuilder offeringQueryRequestBuilder) {
		super(offeringQueryRequestBuilder);
	}

}
