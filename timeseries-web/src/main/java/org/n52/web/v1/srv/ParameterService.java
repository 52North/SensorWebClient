package org.n52.web.v1.srv;

import org.n52.web.v1.ctrl.QueryMap;


public interface ParameterService<T> {
    
	T[] getExpandedParameters(QueryMap query);

    T[] getCondensedParameters(QueryMap query);
    
	T getParameter(String item);

}
