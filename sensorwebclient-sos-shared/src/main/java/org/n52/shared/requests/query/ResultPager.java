/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.requests.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultPager {
    
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
    public <T> ResultPage<T> createPageFrom(Collection<T> resultsToPage, int offset, int pageSize) {
        if (resultsToPage == null || isIllegalOffset(offset, resultsToPage.size())) {
            return createEmptyPage();
        }
        pageSize = normalizePageSize(offset, resultsToPage.size(), pageSize);
        T[] resultSubset = createResultSubset(getArrayFrom(resultsToPage), offset, pageSize);
        return new ResultPage<T>(resultSubset, offset, resultsToPage.size());
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
    public <T> ResultPage<T> createPageFrom(T[] resultsToPage, int offset, int pageSize) {
        if (resultsToPage == null || isIllegalOffset(offset, resultsToPage.length)) {
          return createEmptyPage();
        }
        pageSize = normalizePageSize(offset, resultsToPage.length, pageSize);
        T[] resultSubset = createResultSubset(resultsToPage, offset, pageSize);
        return new ResultPage<T>(resultSubset, offset, resultsToPage.length);
    }

    private boolean isIllegalOffset(int offset, int max) {
        return offset > max;
    }

    private int normalizePageSize(int offset, int total, int pageSize) {
        return (offset + pageSize > total) ? total - offset : pageSize;
    }

    private <T> T[] createResultSubset(T[] results, int offset, int size) {
        return Arrays.copyOfRange(results, offset, offset + size);
    }

    public <T> ResultPage<T> createEmptyPage() {
        List<T> emptyList = Collections.emptyList();
        return createPageFrom(getArrayFrom(emptyList), 0, 0);
    }

    @SuppressWarnings("unchecked") // type safety via generics
    private <T> T[] getArrayFrom(Collection<T> results) {
        return results.toArray((T[]) new Object[0]);
    }

}
