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
package org.n52.server.sos.connector.hydro;

import net.opengis.sos.x20.GetObservationResponseDocument;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.Assert;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class SOSwithSoapAdapterTest {

    private static final String GO_HYPROFILE_SOAP_RESPONSE = "/files/SOS_2.0.0_GetObservationResponse_hyprofile_soap.xml";

    @Test
    public void shouldStripSoapEnvelopeFromResponse() throws Exception {
        XmlObject xml = XmlFileLoader.loadXmlFileViaClassloader(GO_HYPROFILE_SOAP_RESPONSE, getClass());
        if (xml instanceof EnvelopeDocument) {
            EnvelopeDocument envelopeDoc = (EnvelopeDocument) xml;
            XmlObject body = SoapUtil.readBodyNodeFrom(envelopeDoc, null);
            Assert.assertThat(body, is(notNullValue()));
            Assert.assertThat(body.schemaType(), is(not(EnvelopeDocument.type)));
            Assert.assertThat(body.schemaType(), is(GetObservationResponseDocument.type));
        }
    }
}
