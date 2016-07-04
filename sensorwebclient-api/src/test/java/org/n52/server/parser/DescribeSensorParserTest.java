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
package org.n52.server.parser;

import java.io.IOException;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorml.x20.PhysicalComponentDocument;
import net.opengis.sensorml.x20.PhysicalSystemDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

public class DescribeSensorParserTest {

    private static final String SENSOR_ML_101 = "/files/test-sensorml-101.xml";

    private static final String soapResponse_SML_101 = "/files/describeSensorResponse_soap.xml";

    private static final String poxResponse_SML_101 = "/files/describeSensorResponse_pox.xml";

    private static final String smlResponse_SML_101 = "/files/describeSensorResponse_sml.xml";
    
    private static final String SENSOR_ML_20 = "/files/test-sensorml-20.xml";

    private static final String soapResponse_SML_20 = "/files/describeSensorResponse_soap_sml_20.xml";

    private static final String poxResponse_SML_20  = "/files/describeSensorResponse_pox_sml_20.xml";
    
    private static final String soapResponse_physSystem_SML_20 = "/files/describeSensorResponse_soap_physSystem_sml_20.xml";

    private static final String poxResponse_physSystem_SML_20  = "/files/describeSensorResponse_pox_physSystem_sml_20.xml";
    
    private static final String describeSensorResponse_unitInOutput = "/files/describeSensorResponse_unitInOutput.xml";

    private DescribeSensorParser parser;

    private DescribeSensorParser parser_sml_20;
    
    @Before
    public void setUp() throws Exception {
        SOSMetadata metadata = new SOSMetadataBuilder().build();
        XmlObject file = XmlFileLoader.loadXmlFileViaClassloader(SENSOR_ML_101, getClass());
        parser = new DescribeSensorParser(file.newInputStream(), metadata);
        
        SOSMetadata metadata2 = new SOSMetadataBuilder().build();
        XmlObject file2 = XmlFileLoader.loadXmlFileViaClassloader(SENSOR_ML_20, getClass());
        parser_sml_20 = new DescribeSensorParser(file2.newInputStream(), metadata2);
    }

    @Test public void
    shouldParseReferenceValuesFromCapabilitiesSection() {
        assertThat(parser.parseReferenceValues().size(), is(5));
    }
    
    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponseAndSoapEnvelope() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(soapResponse_SML_101, getClass());
        response = DescribeSensorParser.unwrapSensorMLFrom(response);
        SensorMLDocument.class.cast(response);
    }

    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponse() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(poxResponse_SML_101, getClass());
        response = DescribeSensorParser.unwrapSensorMLFrom(response);
        SensorMLDocument.class.cast(response);
    }

    @Test
    public void shouldUnwrapSensorMLFromPlainSensorMLResponse() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(smlResponse_SML_101, getClass());
        response = DescribeSensorParser.unwrapSensorMLFrom(response);
        SensorMLDocument.class.cast(response);
    }
    
    @Test public void
    shouldParseReferenceValuesFromCapabilitiesSection_SensorML20() {
        assertThat(parser_sml_20.parseReferenceValues().size(), is(5));
    }
    
    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponseAndSoapEnvelope_SensorML20() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(soapResponse_SML_20, getClass());
        response = DescribeSensorParser.unwrapSensorDescriptionFrom(response);
        PhysicalComponentDocument.class.cast(response);
    }

    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponse_SensorML20() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(poxResponse_SML_20, getClass());
        response = DescribeSensorParser.unwrapSensorDescriptionFrom(response);
        PhysicalComponentDocument.class.cast(response);
    }
    
    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponseAndSoapEnvelope_SensorML20_PhysSystem() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(soapResponse_physSystem_SML_20, getClass());
        response = DescribeSensorParser.unwrapSensorDescriptionFrom(response);
        PhysicalSystemDocument.class.cast(response);
    }

    @Test
    public void shouldUnwrapSensorMLFromDescribeSensorResponse_SensorML20_PhysSystem() throws XmlException, IOException, XMLHandlingException {
        XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(poxResponse_physSystem_SML_20, getClass());
        response = DescribeSensorParser.unwrapSensorDescriptionFrom(response);
        PhysicalSystemDocument.class.cast(response);
    }
    
    @Test
    public void shouldParseUnitOfMeasurementFromOutput() throws Exception {
        SOSMetadata metadata = new SOSMetadataBuilder().build();
        XmlObject file = XmlFileLoader.loadXmlFileViaClassloader(describeSensorResponse_unitInOutput, getClass());
        DescribeSensorParser p = new DescribeSensorParser(file.newInputStream(), metadata);
        assertThat(p.buildUpSensorMetadataUom("Wasserstand"), is("m+NN"));
    }

}
