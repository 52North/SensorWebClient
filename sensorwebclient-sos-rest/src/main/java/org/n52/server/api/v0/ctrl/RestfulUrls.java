
package org.n52.server.api.v0.ctrl;

/**
 * The {@link RestfulUrls} serves as markup interface, so that each controller instance uses the same URL
 * subpaths.<br/>
 * <br/>
 * <b>Note:</b> Do not code against this type.
 */
public interface RestfulUrls {

    /**
     * Subpath identifying a collection of configured service instances.
     */
    static final String COLLECTION_INSTANCES = "instances";

    /**
     * Subpath identifying a collection of offerings available.
     */
    static final String COLLECTION_OFFERINGS = "offerings";

    /**
     * Subpath identifying a collection of procedures available.
     */
    static final String COLLECTION_PROCEDURES = "procedures";

    /**
     * Subpath identifying a collection of phenomenons available.
     */
    static final String COLLECTION_PHENOMENONS = "phenomenons";

    /**
     * Subpath identifying a collection of features available.
     */
    static final String COLLECTION_FEATURES = "features";
    
    /**
     * Subpath identifying a collection of stations available.
     */
    static final String COLLECTION_STATIONS = "stations";
    
    /**
     * Subpath identifying a collection of timeseries metadata available.
     */
    static final String COLLECTION_TIMESERIES = "timeseries";

}
