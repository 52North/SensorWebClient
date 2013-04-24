
package org.n52.shared.requests.query;

public class PageResult<T> implements Page<T> {

    private static final long serialVersionUID = 5352687838374339566L;

    private T[] results;

    private int offset;

    private int total;

    @SuppressWarnings("unused")
    private PageResult() {
        // for serialization
    }

    public PageResult(int offset, int size, T[] results) {
        if (results == null) {
            throw new NullPointerException("Results cannot be null!");
        }
        this.total = size;
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
