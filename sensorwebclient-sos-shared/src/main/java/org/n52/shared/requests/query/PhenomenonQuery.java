package org.n52.shared.requests.query;

import org.n52.shared.requests.query.builder.PhenomenonQueryRequestBuilder;

public class PhenomenonQuery extends QueryRequest {

	private static final long serialVersionUID = -1750426384949742997L;
	
	public PhenomenonQuery() {
		// for serialization
	}
	
	public PhenomenonQuery(PhenomenonQueryRequestBuilder phenomenonQueryRequestBuilder) {
		super(phenomenonQueryRequestBuilder);
	}

}
