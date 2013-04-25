
package org.n52.shared.requests.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultPage<T> implements Page<T> {

    private static final long serialVersionUID = 5352687838374339566L;

    private T[] results;

    private int offset;

    private int total;

    /**
     * Creates a paged view of the given results.
     * 
     * @param resultsToPage
     *        the results to create a single page from.
     * @param offset
     *        the index of the first page member.
     * @param pageSize
     *        the size of the page.
     * @return a paged subset of the given results.
     */
    public static <T> ResultPage<T> createPageFrom(Collection<T> resultsToPage, int offset, int pageSize) {
        if (offset <= resultsToPage.size()) {
            pageSize = normalizePageSize(offset, resultsToPage.size(), pageSize);
            T[] resultSubset = createResultSubset(getArrayFrom(resultsToPage), offset, pageSize);
            return new ResultPage<T>(resultSubset, offset, resultsToPage.size());
        }
        else {
            return createEmptyPage();
        }
    }
    
    private static int normalizePageSize(int offset, int total, int pageSize) {
        return (offset + pageSize > total) ? total - offset : pageSize;
    }
    
    /**
     * Creates a paged view of the given results.
     * 
     * @param resultsToPage
     *        the results to create a single page from.
     * @param offset
     *        the index of the first page member.
     * @param pageSize
     *        the size of the page.
     * @return a paged subset of the given results.
     */
    public static <T> ResultPage<T> createPageFrom(T[] resultsToPage, int offset, int pageSize) {
        if (resultsToPage == null) {
            return createEmptyPage();
        }
        else {
            pageSize = normalizePageSize(offset, resultsToPage.length, pageSize);
            T[] resultSubset = createResultSubset(resultsToPage, offset, pageSize);
            return new ResultPage<T>(resultSubset, offset, resultsToPage.length);
        }
    }

    private static <T> T[] createResultSubset(T[] results, int offset, int size) {
        return Arrays.copyOfRange(results, offset, offset + size);
    }

    private static <T> ResultPage<T> createEmptyPage() {
        List<T> emptyList = Collections.emptyList();
        return createPageFrom(getArrayFrom(emptyList), 0, 0);
    }

    @SuppressWarnings("unchecked")
    // generics ensure type safety
    private static <T> T[] getArrayFrom(Collection<T> results) {
        return results.toArray((T[]) new Object[0]);
    }

    private ResultPage() {
        // for serialization
    }

    private ResultPage(T[] results, int offset, int total) {
        this.total = total;
        this.offset = offset;
        this.results = results;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getTotal() {
        return total;
    }

    public T[] getResults() {
        return results;
    }

    public boolean isLastPage() {
        return offset + results.length >= total;
    }

}
