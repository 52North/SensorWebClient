/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.series.api.proxy.v0.out;

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
