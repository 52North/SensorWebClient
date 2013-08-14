package org.n52.web.v1.srv;


public interface ParameterService<T> {
    
	T[] getExpandedParameters(int offset, int size);

    T[] getCondensedParameters(int offset, int size);
    
	T getParameter(String item);

}
