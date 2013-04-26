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
        return ResultPage.createPageFrom(allResults, from, to);
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
        return ResultPage.createPageFrom(allResults, from, PAGE_SIZE);
    }

}
