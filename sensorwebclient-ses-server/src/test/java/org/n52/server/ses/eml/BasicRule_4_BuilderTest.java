/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.eml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_OR_EQUAL_TO_INT;

import java.io.IOException;
import java.net.MalformedURLException;

import net.opengis.eml.x001.EMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.RuleBuilder;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;


public class BasicRule_4_BuilderTest {

    private static final String EXIT_VALUE = "400.0";

    private static final String ENTRY_VALUE = "500.0";
    
    private BasicRule_4_Builder builder;

    private TimeseriesMetadata metadata;

    private Rule rule;

    @Before public void
    setUp() {
        builder = new TestableBasicRule4Builder();
        metadata = new TimeseriesMetadata();
        metadata.setServiceUrl("http://fake.url");
        metadata.setOffering("offering");
        metadata.setPhenomenon("phenomenon");
        metadata.setProcedure("procedure");
        metadata.setFeatureOfInterest("FOI");
        rule = RuleBuilder.aRule()
                    .setEntryValue(ENTRY_VALUE)
                    .setExitValue(EXIT_VALUE)
                    .setEntryOperatorIndex(GREATER_THAN_INT)
                    .setExitOperatorIndex(LESS_THAN_OR_EQUAL_TO_INT)
                    .setEntryUnit("m")
                    .setExitUnit("m")
                    .setTitle("MyTestRule")
                    .setTimeseriesMetadata(metadata)
                    .build();
    }
    
    @Test public void 
    shouldBlah() 
    throws Exception {
        String eml = builder.create(rule).getEml();
        assertThat(XMLBeansParser.validate(XmlObject.Factory.parse(eml)), is(empty()));
    }
    
    private class TestableBasicRule4Builder extends BasicRule_4_Builder {
        
        @Override
        protected EMLDocument getEmlTemplate() throws MalformedURLException, XmlException, IOException {
            return EMLDocument.Factory.parse(getClass().getResourceAsStream("/files/BR_4.xml"));
        }

        @Override
        protected User getUserFrom(Rule rule) {
            User user = new User();
            user.setId(0);
            return user;
        }
        
    }


}
