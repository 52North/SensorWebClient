package org.n52.shared.requests.query.queries;

import org.n52.shared.requests.query.QueryParameters;

public class StationQuery extends QueryRequest {

    private static final long serialVersionUID = 6168793169245980895L;
    
    StationQuery() {
        // for serialization
    }
    
    public StationQuery(String serviceUrl, QueryParameters parameters) {
        super(serviceUrl, parameters);
    }

}
