package org.n52.api.v1.io;

import org.n52.io.v1.data.FeatureOutput;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class FeatureConverter extends OutputConverter<Feature, FeatureOutput> {

    public FeatureConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public FeatureOutput convertExpanded(Feature feature) {
        FeatureOutput convertedFeature = convertCondensed(feature);
        convertedFeature.setService(convertCondensedService());
        return convertedFeature;
    }

    @Override
    public FeatureOutput convertCondensed(Feature feature) {
        FeatureOutput convertedFeature = new FeatureOutput();
        convertedFeature.setId(feature.getFeatureId());
        convertedFeature.setLabel(feature.getLabel());
        return convertedFeature;
    }

}
