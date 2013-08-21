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
