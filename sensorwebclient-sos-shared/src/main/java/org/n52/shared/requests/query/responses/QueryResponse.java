
package org.n52.shared.requests.query.responses;

import java.io.Serializable;

import org.n52.shared.requests.query.Page;
import org.n52.shared.requests.query.QueryRequest;
import org.n52.shared.requests.query.ResultPage;

/**
 * A response object containing the results of a {@link QueryRequest}. The results can be either paged (a
 * sub-collection of all available results) or a complete collection of all results available.
 */
public class QueryResponse<T> implements Serializable {

    private static final long serialVersionUID = 8964914430932650368L;

    private String serviceUrl;

    private Page<T> results;

    private boolean paged;

    protected QueryResponse() {
        // for serialization
    }

    public QueryResponse(String serviceUrl) {
        this(serviceUrl, null);
    }

    public QueryResponse(String serviceUrl, T[] results) {
        this.serviceUrl = serviceUrl;
        setResults(results);
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public T[] getResults() {
        return results.getResults();
    }

    /**
     * Sets the given results as complete collection of all results available.
     * 
     * @param results
     *        the results to set.
     * @see #setResultPage(Page)
     */
    public void setResults(T[] results) {
        int size = results == null ? 0 : results.length;
        this.results = ResultPage.createPageFrom(results, 0, size);
        this.paged = false;
    }

    /**
     * Sets the given page as a sub-collection of all results available.
     * 
     * @param resultPage
     *        the result page to set.
     * @see #setResults(T[])
     */
    public void setResultPage(Page<T> resultPage) {
        this.results = resultPage;
        this.paged = true;
    }

    /**
     * Returns all query results on a page, no matter if it is a sub-collection or all results available.
     * 
     * @return the results on a page.
     */
    public Page<T> getPagedResults() {
        return results;
    }

    /**
     * @return <code>true</code> the query results are paged, <code>false</code> otherwise.
     */
    public boolean isPaged() {
        return paged;
    }

}
