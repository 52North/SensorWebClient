/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
