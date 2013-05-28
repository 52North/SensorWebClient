package org.n52.shared.requests.query.queries;

import org.n52.shared.requests.query.QueryParameters;

public class ProcedureQuery extends QueryRequest {

    private static final long serialVersionUID = 6168793169245980895L;
    
    ProcedureQuery() {
        // for serialization
    }
    
    public ProcedureQuery(String serviceUrl, QueryParameters parameters) {
        super(serviceUrl, parameters);
    }

}
