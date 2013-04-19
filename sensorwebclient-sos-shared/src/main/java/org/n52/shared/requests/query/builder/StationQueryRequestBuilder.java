package org.n52.shared.requests.query.builder;

import org.n52.shared.requests.query.StationQuery;

public class StationQueryRequestBuilder extends QueryRequestBuilder {

	@Override
	public StationQuery build() {
		return new StationQuery(this);
	}

}
