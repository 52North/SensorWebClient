package org.n52.shared.requests.query.builder;

import org.n52.shared.requests.query.PhenomenonQuery;

public class PhenomenonQueryRequestBuilder extends QueryRequestBuilder {

	@Override
	public PhenomenonQuery build() {
		return new PhenomenonQuery(this);
	}

}
