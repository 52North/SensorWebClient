package org.n52.shared.requests.query.queries;

import org.n52.shared.requests.query.QueryParameters;

public class PhenomenonQuery extends QueryRequest {

    private static final long serialVersionUID = 6168793169245980895L;
    
    PhenomenonQuery() {
        // for serialization
    }
    
    public PhenomenonQuery(String serviceUrl, QueryParameters parameters) {
        super(serviceUrl, parameters);
    }

}
