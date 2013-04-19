package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;

public class FeatureQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private FeatureOfInterest[] features = new FeatureOfInterest[0];
	
	public FeatureQueryResponse() {
		// for serialization
	}

	public FeatureOfInterest[] getFeatures() {
		return features;
	}

	public void setFeatures(FeatureOfInterest[] features) {
		this.features = features;
	}

}
