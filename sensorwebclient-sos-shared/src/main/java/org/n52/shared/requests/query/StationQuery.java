package org.n52.shared.requests.query;

import org.n52.shared.requests.query.builder.StationQueryRequestBuilder;

public class StationQuery extends QueryRequest {

	private static final long serialVersionUID = -7373163198354019173L;

	public StationQuery() {
		// for serialization
	}
	
	public StationQuery(StationQueryRequestBuilder stationQueryRequestBuilder) {
		super(stationQueryRequestBuilder);
	}

}
