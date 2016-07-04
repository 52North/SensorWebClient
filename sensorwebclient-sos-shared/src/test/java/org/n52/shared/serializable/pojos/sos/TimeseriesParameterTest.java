/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
