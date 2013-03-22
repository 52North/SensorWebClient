package org.n52.shared.requests.query.responses;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;

public class FeatureQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;
	
	private Collection<FeatureOfInterest> features = new ArrayList<FeatureOfInterest>();
	
	public FeatureQueryResponse() {
		// for serialization
	}

	public Collection<FeatureOfInterest> getFeature() {
		return features;
	}

	public void setFeature(Collection<FeatureOfInterest> features) {
		this.features = features;
	}

	public void addFeature(FeatureOfInterest feature) {
		this.features.add(feature);
	}

}
