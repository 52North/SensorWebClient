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
package org.n52.shared.serializable.pojos.sos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class TimeseriesParameterTest {
    
    @Test public void 
    shouldParseLastUrnFragmentAsLabel() {
        TestParameter testParameter = new TestParameter("urn:123::999:id2134");
        assertThat(testParameter.getLabel(), is("id2134"));
    }
    
    @Test public void 
    shouldParseLastHttpPathAsLabel() {
        TestParameter testParameter = new TestParameter("http:///envuvlxkq/D_GB_Sample.xml");
        assertThat(testParameter.getLabel(), is("D_GB_Sample.xml"));
    }
    
    @Test public void 
    shouldParseHttpFragmentAsLabel() {
        TestParameter testParameter = new TestParameter("http:///envuvlxkq/D_GB_Sample.xml#GB_SamplingFeature_281");
        assertThat(testParameter.getLabel(), is("GB_SamplingFeature_281"));
    }
    
    @Test public void 
    shouldHaveTestSuffixWithinGlobalId() {
        TestParameter testParameter = new TestParameter("someParameterId");
        assertThat(testParameter.getGlobalId(), startsWith("test_"));
    }
    
    private class TestParameter extends TimeseriesParameter {
        
        private static final long serialVersionUID = -7460364788019304476L;

        public TestParameter(String parameterId) {
            super(parameterId, new String[]{parameterId});
        }

        @Override
        protected String getGlobalIdPrefix() {
            return "test_";
        }
        
    }
}
