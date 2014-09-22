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
    
    @Test public void 
    shouldNormalizeUmlautsAndNonAlphaNumerics()
    {
        assertThat(controllerUnderTest.normalize("ÜüÖöÄäß#'+`´^°!§$%&/()=?-ABCNksdfjiu098723049234lkjdsf"), 
                   is("UeueOEoeAEaess__________________ABCNksdfjiu098723049234lkjdsf"));
    }

}
