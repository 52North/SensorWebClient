package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Feature;

public class FeatureQueryResponse extends QueryResponse<Feature> {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	public FeatureQueryResponse(String serviceUrl, Feature[] results) {
        super(serviceUrl, results);
    }

    public FeatureQueryResponse(String serviceUrl) {
        super(serviceUrl);
    }

    FeatureQueryResponse() {
		// for serialization
	}

}
