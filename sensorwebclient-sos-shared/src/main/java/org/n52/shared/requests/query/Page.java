
package org.n52.shared.requests.query;

import java.io.Serializable;

/**
 * Represents a read-only subset of totally available results.
 * 
 * @param <T>
 *        the type of the results to scroll.
 */
public interface Page<T> extends Serializable {

    /**
     * @return the offset index of the first result available.
     */
    public int getOffset();

    /**
     * The total number of available results should not change. It inidicates a boundary until where the
     * results can be scrolled. The length of each subset is implicit by {@link #getResults().length}.
     * 
     * @return the total number of available results.
     */
    public int getTotal();

    /**
     * @return the results.
     */
    public T[] getResults();

    /**
     * @return <code>true</code> if no more results are available, <code>false</code> otherwise.
     */
    public boolean isLastPage();

}
