
package org.n52.api.v1.srv;

import org.n52.shared.requests.query.QueryParameters;
import org.n52.web.v1.ctrl.QueryMap;

public class QueryParameterAdapter {

    public static QueryParameters createQueryParameters(QueryMap map) {
        return QueryParameters.createEmptyFilterQuery()
                .setService(map.getService())
                .setFeature(map.getFeature())
                .setOffering(map.getOffering())
                .setProcedure(map.getProcedure())
                .setPhenomenon(map.getPhenomenon());
    }
}
