package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Procedure;

public class ProcedureQueryResponse extends QueryResponse<Procedure> {
	
	private static final long serialVersionUID = -19629388867993311L;

	public ProcedureQueryResponse(String serviceUrl, Procedure[] results) {
        super(serviceUrl, results);
    }

    public ProcedureQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

    ProcedureQueryResponse() {
		// for serialization
	}
	
}
