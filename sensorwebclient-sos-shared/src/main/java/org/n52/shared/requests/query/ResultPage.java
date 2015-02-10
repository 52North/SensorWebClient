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
package org.n52.shared.requests.query;

import java.io.Serializable;
import java.util.Collections;

/**
 * Represents a read-only subset of totally available results.
 * 
 * @param <T>
 *        the type of the results to scroll.
 */
public class ResultPage<T> implements Serializable {

    private static final long serialVersionUID = 5352687838374339566L;

    private T[] results;

    private int offset;

    private int total;

    @SuppressWarnings("unused")
    private ResultPage() {
        // for serialization
    }

    /**
     * Creates a page of the given results and holds the page's context to the complete result list.
     * 
     * @param results
     *        the results to put on a page.
     * @param offset
     *        the page's offset context (first element's index in the complete result list).
     * @param total
     *        the amount of results in total.
     */
    public ResultPage(T[] results, int offset, int total) {
        this.total = total;
        this.offset = offset;
        this.results = results;
    }

    /**
     * @return the offset index of the first result available.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * The total number of available results should not change. It inidicates a boundary until where the
     * results can be scrolled. The length of each subset is implicit by {@link #getResults().length}.
     * 
     * @return the total number of available results.
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return the results (never <code>null</code> but an empty array when no results were set beforehand).
     */
    public T[] getResults() {
        return results != null ? results : createEmptyArray();
    }

    @SuppressWarnings("unchecked")
    // type safety via generics
    private T[] createEmptyArray() {
        return Collections.emptyList().toArray((T[]) new Object[0]);
    }

    /**
     * @return <code>true</code> if no more results are available, <code>false</code> otherwise.
     */
    public boolean isLastPage() {
        return offset + results.length >= total;
    }

}
