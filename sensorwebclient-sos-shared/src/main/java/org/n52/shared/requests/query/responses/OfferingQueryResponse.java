package org.n52.shared.requests.query.responses;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.shared.serializable.pojos.sos.Offering;

public class OfferingQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -19629388867993311L;

	private Collection<Offering> offerings = new ArrayList<Offering>();
	
	public OfferingQueryResponse() {
		// for serialization
	}
	
	public Collection<Offering> getOffering() {
		return offerings;
	}

	public void setOffering(Collection<Offering> offerings) {
		this.offerings = offerings;
	}
	
	public void addOffering(Offering offering) {
		this.offerings.add(offering);
	}

}
