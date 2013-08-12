
package org.n52.web.v1.ctrl;

/**
 * The {@link RestfulUrls} serves as markup interface, so that each controller instance uses the same URL
 * subpaths.<br/>
 * <br/>
 * <b>Note:</b> Do not code against this type.
 */
public interface RestfulUrls {
    
    /**
     * The base URL to be used as RESTful entry point.
     */
    static final String DEFAULT_PATH = "/v1";

    /**
     * Subpath identifying a collection of services availabe.
     */
    static final String COLLECTION_SERVICES = "services";
    
    /**
     * Subpath identifying a collection of categories availabe.
     */
    static final String COLLECTION_CATEGORIES = "categories";
    
    /**
     * Subpath identifying a collection of offerings available.
     */
    static final String COLLECTION_OFFERINGS = "offerings";

    /**
     * Subpath identifying a collection of features available.
     */
    static final String COLLECTION_FEATURES = "features";
    
    /**
     * Subpath identifying a collection of procedures available.
     */
    static final String COLLECTION_PROCEDURES = "procedures";

    /**
     * Subpath identifying a collection of phenomenons available.
     */
    static final String COLLECTION_OBSERVED_PROPERTIES = "observedProperties";

    /**
     * Subpath identifying a collection of stations available.
     */
    static final String COLLECTION_STATIONS = "stations";
    
    /**
     * Subpath identifying a collection of timeseries metadata available.
     */
    static final String COLLECTION_TIMESERIES = "timeseries";

}
