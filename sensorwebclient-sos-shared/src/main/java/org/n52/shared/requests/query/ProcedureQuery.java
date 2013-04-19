package org.n52.shared.requests.query;

import org.n52.shared.requests.query.builder.ProcedureQueryRequestBuilder;

public class ProcedureQuery extends QueryRequest {

	private static final long serialVersionUID = -1750426384949742997L;
	
	public ProcedureQuery() {
		// for serialization
	}
	
	public ProcedureQuery(ProcedureQueryRequestBuilder procedureQueryRequestBuilder) {
		super(procedureQueryRequestBuilder);
	}

}
