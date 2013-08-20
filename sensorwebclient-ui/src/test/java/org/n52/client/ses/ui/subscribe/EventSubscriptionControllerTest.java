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
