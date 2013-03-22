package org.n52.shared.requests.query.responses;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.shared.serializable.pojos.sos.Procedure;

public class ProcedureQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private Collection<Procedure> procedures = new ArrayList<Procedure>();
	
	public ProcedureQueryResponse() {
		// for serialization
	}

	public Collection<Procedure> getProcedure() {
		return procedures;
	}

	public void setProcedure(Collection<Procedure> procedures) {
		this.procedures = procedures;
	}
	
	public void addProcedure(Procedure procedure) {
		this.procedures.add(procedure);
	}
}
