package org.n52.client.ses.ui.subscribe;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class EventSubscriptionControllerTest {
    
    private EventSubscriptionController controllerUnderTest;

    @Before public void 
    setUp()
    throws Exception {
        controllerUnderTest = new EventSubscriptionController();
    }
    
    @Test public void 
    shouldReplaceAllNonAlphaNumericsWithUnderscore()
    {
        assertThat(controllerUnderTest.replaceNonAlphaNumerics("#'+`´^°!§$%&/()=?-"), is("__________________"));
    }
    
    @Test public void 
    shouldNotReplaceAlphaNumericsWithUnderscores()
    {
        assertThat(controllerUnderTest.replaceNonAlphaNumerics("ABCNksdfjiu098723049234lkjdsf"), is("ABCNksdfjiu098723049234lkjdsf"));
    }
    
    @Test public void 
    shouldReplaceAllUmlautsWithAlternatives()
    {
        assertThat(controllerUnderTest.replaceAllUmlauts("ÜüÖöÄäß"), is("UeueOEoeAEaess"));
    }

}
