package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;

public class FeatureQueryResponse extends QueryResponse<FeatureOfInterest> {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	public FeatureQueryResponse(String serviceUrl, FeatureOfInterest[] results) {
        super(serviceUrl, results);
    }

    public FeatureQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

    FeatureQueryResponse() {
		// for serialization
	}

}
