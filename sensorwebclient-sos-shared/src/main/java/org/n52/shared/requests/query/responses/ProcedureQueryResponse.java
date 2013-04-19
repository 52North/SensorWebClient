package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Procedure;

public class ProcedureQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private Procedure[] procedures = new Procedure[0];
	
	public ProcedureQueryResponse() {
		// for serialization
	}

	public Procedure[] getProcedure() {
		return procedures;
	}

	public void setProcedure(Procedure[] procedures) {
		this.procedures = procedures;
	}
	
}
