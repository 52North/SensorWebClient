package org.n52.web.v1.srv;

import org.n52.io.v1.data.FeatureOutput;

public interface FeaturesParameterService {

	FeatureOutput[] getFeatures(int offset, int size);

	FeatureOutput getFeature(String item);

}
