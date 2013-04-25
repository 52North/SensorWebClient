package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Phenomenon;

public class PhenomenonQueryResponse extends QueryResponse<Phenomenon> {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	public PhenomenonQueryResponse(String serviceUrl, Phenomenon[] results) {
        super(serviceUrl, results);
    }

    public PhenomenonQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

    PhenomenonQueryResponse() {
		// for serialization
	}
}
