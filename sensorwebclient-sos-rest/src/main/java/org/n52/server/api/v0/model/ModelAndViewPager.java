
package org.n52.server.api.v0.model;

import java.util.Collection;

import org.n52.shared.requests.query.ResultPage;
import org.n52.shared.requests.query.ResultPager;
import org.springframework.web.servlet.ModelAndView;

/**
 * Utility composite using a {@link ResultPager} to create a paged view on a subset of given results.
 */
public class ModelAndViewPager {

    private ModelAndView mav;

    private ResultPager pager;

    public ModelAndViewPager(String viewName) {
        this.mav = new ModelAndView(viewName);
        this.pager = new ResultPager();
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
     * @return a paged results in a model and view instance.
     */
    public <T> ModelAndView createPagedModelAndViewFrom(Collection<T> resultsToPage, int offset, int pageSize) {
        return setPage(pager.createPageFrom(resultsToPage, offset, pageSize));
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
     * @return a paged results in a model and view instance.
     */
    public <T> ModelAndView createPagedModelAndViewFrom(T[] resultsToPage, int offset, int pageSize) {
        return setPage(pager.createPageFrom(resultsToPage, offset, pageSize));
    }

    private <T> ModelAndView setPage(ResultPage<T> page) {
        return mav.addObject(page);
    }
}
