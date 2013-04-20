package org.n52.shared.requests.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.n52.shared.requests.query.PageResult;

public class PageResultTest {
    
    private static final int PAGE_SIZE = 5;
    
    private String[] allResults; 
    
    @Before
    public void setUp() {
        allResults = new String[] { "one", "two", "three", "four", "five", "six" }; 
    }
    
    @Test(expected=NullPointerException.class) public void 
    shouldThrowNullPointerWhenSettingNullResultsInConstructor()
    {
        new PageResult<String>(0, 0, null);
    }
    
    @Test public void
    shouldIndicateThatMorePagesAreAvailable() 
    {
        PageResult<String> firstPage = getFirstPage();
        assertThat(firstPage.isLastPage(), is(false));
    }
    
    @Test public void
    shouldIndicateLastPageWithOverlappingSize() 
    {
        PageResult<String> lastPage = getLastPageOverlapping();
        assertThat(lastPage.isLastPage(), is(true));
    }
    
    @Test public void
    shouldIndicateLastPageWhenMatchingSize() 
    {
        PageResult<String> lastPage = getLastPageWithMatchingSize();
        assertThat(lastPage.isLastPage(), is(true));
    }
    
    private PageResult<String> getFirstPage() {
        int from = 0;
        int to = from + PAGE_SIZE;
        String[] resultsOnFirstPage = Arrays.copyOfRange(allResults, from, to);
        return new PageResult<String>(from, allResults.length, resultsOnFirstPage);
    }
    
    private PageResult<String> getLastPageOverlapping() {
        int overlap = 2;
        int from = allResults.length - PAGE_SIZE;
        return getLastPage(from + overlap);
    }
    
    private PageResult<String> getLastPageWithMatchingSize() {
        int from = allResults.length - PAGE_SIZE;
        return getLastPage(from);
    }

    private PageResult<String> getLastPage(int from) {
        int to = from + PAGE_SIZE;
        String[] resultsOnLastPage = Arrays.copyOfRange(allResults, from, to);
        return new PageResult<String>(from, allResults.length, resultsOnLastPage);
    }

}
