package org.n52.shared.requests.query.builder;

import org.n52.shared.requests.query.ProcedureQuery;

public class ProcedureQueryRequestBuilder extends QueryRequestBuilder {

	@Override
	public ProcedureQuery build() {
		return new ProcedureQuery(this);
	}

}
