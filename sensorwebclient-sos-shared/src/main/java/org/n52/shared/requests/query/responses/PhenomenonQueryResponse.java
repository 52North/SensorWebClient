package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Phenomenon;

public class PhenomenonQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private Phenomenon[] phenomenons = new Phenomenon[0];
	
	public PhenomenonQueryResponse() {
		// for serialization
	}

	public Phenomenon[] getPhenomenons() {
		return phenomenons;
	}

	public void setPhenomenons(Phenomenon[] phenomenons) {
		this.phenomenons = phenomenons;
	}
	
}
