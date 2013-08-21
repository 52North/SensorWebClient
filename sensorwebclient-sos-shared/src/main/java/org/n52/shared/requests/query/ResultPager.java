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
