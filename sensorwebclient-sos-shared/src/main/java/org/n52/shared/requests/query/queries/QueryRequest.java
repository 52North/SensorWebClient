package org.n52.shared.requests.query.queries;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import java.io.Serializable;

import org.n52.shared.requests.query.QueryParameters;

public abstract class QueryRequest implements Serializable {
	
	private static final long serialVersionUID = -2469479744020996103L;

    private QueryParameters parameters;
    
	private String serviceUrl;
	
	private int offset;
	
	private int pageSize;

	QueryRequest() {
		// for serialization
	}

	protected QueryRequest(String serviceUrl, QueryParameters parameters) {
		this.serviceUrl = serviceUrl;
		this.parameters = parameters;
	}

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

	/**
	 * @return an always non-<code>null</code> parameter set.
	 */
	public QueryParameters getQueryParameters() {
	    return parameters != null ? parameters : createEmptyFilterQuery();
	}
	
	public void setQueryParameters(QueryParameters parameters) {
	    this.parameters = parameters;
	}

	/**
	 * @return an offset which always is >= <code>0</code>;
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Negative values are handled as <code>0</code>.
	 * 
	 * @param offset the offset to set.  
	 */
	public void setOffset(int offset) {
		this.offset = isNegative(offset) ? 0 : offset;
	}

	/**
     * @return a size which always is >= <code>0</code>.
     */
	public int getPageSize() {
		return isNegative(pageSize) ? 0 : pageSize;
	}

	/**
     * Negative values are handled as <code>0</code>.
     * 
     * @param pageSize the page size to set.
     */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
    private boolean isNegative(int value) {
        return value < 0;
    }

}
