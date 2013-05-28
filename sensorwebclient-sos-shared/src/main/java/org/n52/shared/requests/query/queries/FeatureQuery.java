package org.n52.shared.requests.query.queries;

import org.n52.shared.requests.query.QueryParameters;

public class FeatureQuery extends QueryRequest {

    private static final long serialVersionUID = 6168793169245980895L;
    
    FeatureQuery() {
        // for serialization
    }
    
    public FeatureQuery(String serviceUrl, QueryParameters parameters) {
        super(serviceUrl, parameters);
    }

}
