package org.n52.shared.requests.query.responses;

import java.io.Serializable;

public abstract class QueryResponse implements Serializable {

	private static final long serialVersionUID = 8964914430932650368L;
	
	private String serviceUrl;
	
	private boolean pagingEnd;
	
	private int pagingEndIndex;
	
	public QueryResponse() {
		// for serialization
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public boolean isPagingEnd() {
		return pagingEnd;
	}

	public void setPagingEnd(boolean pagingEnd) {
		this.pagingEnd = pagingEnd;
	}

	public int getPagingEndIndex() {
		return pagingEndIndex;
	}

	public void setPagingEndIndex(int pagingEndIndex) {
		this.pagingEndIndex = pagingEndIndex;
	}

}
