/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
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
