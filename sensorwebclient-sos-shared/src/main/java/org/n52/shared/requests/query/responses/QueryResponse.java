package org.n52.shared.requests.query.responses;

import java.io.Serializable;

import org.n52.shared.requests.query.Page;

public  class QueryResponse<T> implements Serializable {

	private static final long serialVersionUID = 8964914430932650368L;
	
	private String serviceUrl;
	
	private Page<T> resultSubset;
	
	public QueryResponse() {
		// for serialization
	}
	
	public QueryResponse(Page<T> resultSubset) {
	    this.resultSubset = resultSubset;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

    public Page<T> getResultSubset() {
        return resultSubset;
    }

    public void setResultSubset(Page<T> resultSubset) {
        this.resultSubset = resultSubset;
    }
    
}
