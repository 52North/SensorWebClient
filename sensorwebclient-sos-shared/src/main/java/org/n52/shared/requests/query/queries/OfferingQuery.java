package org.n52.shared.requests.query.queries;

import org.n52.shared.requests.query.QueryParameters;

public class OfferingQuery extends QueryRequest {

    private static final long serialVersionUID = 6168793169245980895L;
    
    OfferingQuery() {
        // for serialization
    }
    
    public OfferingQuery(String serviceUrl, QueryParameters parameters) {
        super(serviceUrl, parameters);
    }

}
