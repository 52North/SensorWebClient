package org.n52.shared.requests.query;


public class PageResult<T> implements Page<T> {
    
    private T[] results;

    private int offset;
    
    private int total;
    
    public PageResult(int offset, int size, T[] results) {
        this.offset = offset;
        this.total = size;
        if (results == null) {
            throw new NullPointerException("Results cannot be null!");
        } else {
            this.results = results;
        }
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
    
    public boolean isLast() {
        return offset + results.length >= total;
    }

}
