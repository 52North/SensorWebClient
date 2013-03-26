package org.n52.shared.requests.query.responses;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.shared.serializable.pojos.sos.Phenomenon;

public class PhenomenonQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private Collection<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
	
	public PhenomenonQueryResponse() {
		// for serialization
	}

	public Collection<Phenomenon> getPhenomenons() {
		return phenomenons;
	}

	public void setPhenomenons(Collection<Phenomenon> phenomenons) {
		this.phenomenons = phenomenons;
	}
	
	public void addPhenoemon(Phenomenon phenomenon) {
		this.phenomenons.add(phenomenon);
	}

}
