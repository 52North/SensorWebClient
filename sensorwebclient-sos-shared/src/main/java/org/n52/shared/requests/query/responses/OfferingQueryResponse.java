package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Offering;

public class OfferingQueryResponse extends QueryResponse<Offering> {
    
    private static final long serialVersionUID = -19629388867993311L;
	
	public OfferingQueryResponse(String serviceUrl, Offering[] results) {
        super(serviceUrl, results);
    }

    public OfferingQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

	OfferingQueryResponse() {
		// for serialization
	}
	
}
