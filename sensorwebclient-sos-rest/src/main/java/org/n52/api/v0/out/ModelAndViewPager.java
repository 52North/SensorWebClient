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

package org.n52.api.v0.out;

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
