
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

    @SuppressWarnings("unchecked") // type safety via generics
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
