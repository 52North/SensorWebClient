
package org.n52.web.v1;

import java.util.ArrayList;
import java.util.List;

public final class Resources {

    private static List<Resource> resources = new ArrayList<Resource>();
    
    static {
        resources.add(Resource.createResource("services").withLabel("Service Provider").withDescription("A service provider offers timeseries data."));
        resources.add(Resource.createResource("stations").withLabel("Station").withDescription("A station is the place where measurement takes place."));
        resources.add(Resource.createResource("timeseries").withLabel("Timeseries").withDescription("Represents a sequence of data values measured over time."));
        resources.add(Resource.createResource("categories").withLabel("Category").withDescription("A category group available timeseries."));
        resources.add(Resource.createResource("offerings").withLabel("Offering").withDescription("An organizing unit to filter data."));
        resources.add(Resource.createResource("features").withLabel("Feature").withDescription("An organizing unit to filter data."));
        resources.add(Resource.createResource("procedures").withLabel("Procedure").withDescription("An organizing unit to filter data."));
        resources.add(Resource.createResource("phenomena").withLabel("Phenomenon").withDescription("An organizing unit to filter data."));
    }

    public static Resource[] get() {
        return resources.toArray(new Resource[0]);
    }

    static class Resource {

        private String id;
        private String label;
        private String description;

        private Resource(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Resource withLabel(String label) {
            this.label = label;
            return this;
        }

        public Resource withDescription(String description) {
            this.description = description;
            return this;
        }

        public static Resource createResource(String id) {
            return new Resource(id);
        }
    }

}
