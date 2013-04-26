package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Station;

public class StationQueryResponse extends QueryResponse<Station> {
	
	private static final long serialVersionUID = -2689753333997419445L;

	public StationQueryResponse(String serviceUrl, Station[] results) {
        super(serviceUrl, results);
    }

    public StationQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

    StationQueryResponse() {
		// for serialization
	}

}
