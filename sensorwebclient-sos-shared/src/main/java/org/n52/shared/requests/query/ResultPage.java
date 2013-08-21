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
