package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.Feature;

public interface FeaturesParameterService {

	Feature[] getFeatures(int offset, int size);

	Feature getFeature(String item);

}
