package org.n52.server.api.v0.model.geojson;

import java.util.Collection;

public class GeojsonFeatureCollection {

    private GeojsonFeature[] features;
    
    public static GeojsonFeatureCollection create(Collection<? extends GeojsonFeature> features) {
        GeojsonFeatureCollection collection = new GeojsonFeatureCollection();
        collection.setFeatures(features.toArray(new GeojsonFeature[0]));
        return collection;
    }
    
    public static <T extends GeojsonFeature> GeojsonFeatureCollection create(T[] features) {
        GeojsonFeatureCollection collection = new GeojsonFeatureCollection();
        collection.setFeatures(features);
        return collection;
    }
    
    private GeojsonFeatureCollection() {
        // for serialization
    }

    public GeojsonFeature[] getFeatures() {
        return features;
    }

    public void setFeatures(GeojsonFeature[] features) {
        this.features = features;
    }
    
}
