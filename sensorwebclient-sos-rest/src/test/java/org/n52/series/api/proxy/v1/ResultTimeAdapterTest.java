/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.n52.series.api.proxy.v1;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.n52.series.api.proxy.v1.srv.ResultTimeAdapter;

/**
 *
 * @author jansch
 */
public class ResultTimeAdapterTest {
    
    private static final String GDA_RESPONSE = "/files/GetDataAvailability_response.xml";
    private ResultTimeAdapter adapter;

    @Before
    public void setUp() throws OXFException {
        adapter = new ResultTimeAdapter();
    }
    
    @Test
    public void shouldStripSoapEnvelopeFromResponse() throws Exception {
        XmlObject xml = XmlFileLoader.loadXmlFileViaClassloader(GDA_RESPONSE, getClass());
        ArrayList<String> resultTimes = adapter.getResultTimes(xml);
        Assert.assertThat(resultTimes, is(notNullValue()));
        Assert.assertTrue(resultTimes.size() == 4);
    }
    
}
