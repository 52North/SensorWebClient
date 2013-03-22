package org.n52.shared.requests.query.builder;

import org.n52.shared.requests.query.OfferingQuery;

public class OfferingQueryRequestBuilder extends QueryRequestBuilder {

	@Override
	public OfferingQuery build() {
		return new OfferingQuery(this);
	}

}
