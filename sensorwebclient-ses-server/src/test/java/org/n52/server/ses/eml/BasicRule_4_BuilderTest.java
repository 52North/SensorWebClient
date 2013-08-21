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
package org.n52.server.ses.eml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

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
                            .setEntryValue("500")
                            .setExitValue("400")
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
