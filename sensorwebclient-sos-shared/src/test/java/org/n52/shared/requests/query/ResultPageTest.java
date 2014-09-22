/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ResultPageTest {
    
    private static final int PAGE_SIZE = 5;
    
    private String[] allResults; 
    
    @Before
    public void setUp() {
        allResults = new String[] { "one", "two", "three", "four", "five", "six" }; 
    }
    
    @Test public void
    shouldIndicateThatMorePagesAreAvailable() 
    {
        ResultPage<String> firstPage = getFirstPage();
        assertThat(firstPage.isLastPage(), is(false));
    }
    
    @Test public void
    shouldIndicateLastPageWithOverlappingSize() 
    {
        ResultPage<String> lastPage = getLastPageOverlapping();
        assertThat(lastPage.isLastPage(), is(true));
    }
    
    @Test public void
    shouldIndicateLastPageWhenMatchingSize() 
    {
        ResultPage<String> lastPage = getLastPageWithMatchingSize();
        assertThat(lastPage.isLastPage(), is(true));
    }
    
    private ResultPage<String> getFirstPage() {
        int from = 0;
        int to = from + PAGE_SIZE;
        return new ResultPager().createPageFrom(allResults, from, to);
    }
    
    private ResultPage<String> getLastPageOverlapping() {
        int overlap = 2;
        int from = allResults.length - PAGE_SIZE;
        return getLastPage(from + overlap);
    }
    
    private ResultPage<String> getLastPageWithMatchingSize() {
        int from = allResults.length - PAGE_SIZE;
        return getLastPage(from);
    }

    private ResultPage<String> getLastPage(int from) {
        return new ResultPager().createPageFrom(allResults, from, PAGE_SIZE);
    }

}
