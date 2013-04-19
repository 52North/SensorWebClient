package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Offering;

public class OfferingQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -19629388867993311L;

	private Offering[] offerings = new Offering[0];
	
	public OfferingQueryResponse() {
		// for serialization
	}
	
	public Offering[] getOfferings() {
		return offerings;
	}

	public void setOffering(Offering[] offerings) {
		this.offerings = offerings;
	}
	
}
