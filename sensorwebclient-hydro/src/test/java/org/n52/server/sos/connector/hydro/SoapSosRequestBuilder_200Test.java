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

package org.n52.server.sos.connector.hydro;

import java.io.IOException;
import net.opengis.sos.x20.GetObservationDocument;
import org.apache.xmlbeans.XmlException;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.*;
import static org.n52.server.sos.connector.hydro.SoapUtil.readBodyNodeFrom;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class SoapSosRequestBuilder_200Test {

    private SoapSOSRequestBuilder_200 builder;

    @Before
    public void setUp() {
        builder = new SoapSOSRequestBuilder_200();
    }

    @Test public void
    shouldReplaceDefaultOM20ResponseFormatWithWaterML20() throws OXFException, XmlException, IOException {
        ParameterContainer parameters = new ParameterContainer();
        parameters.addParameterShell(GET_OBSERVATION_SERVICE_PARAMETER, "SOS");
        parameters.addParameterShell(GET_OBSERVATION_VERSION_PARAMETER, "2.0.0");
        parameters.addParameterShell(GET_OBSERVATION_OFFERING_PARAMETER, "offering");
        parameters.addParameterShell(GET_OBSERVATION_PROCEDURE_PARAMETER, "procedure");
        parameters.addParameterShell(GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER, "feature");
        parameters.addParameterShell(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER, "http://www.opengis.net/om/2.0");
        String request = builder.buildGetObservationRequest(parameters);

        EnvelopeDocument envelope = EnvelopeDocument.Factory.parse(request);
        GetObservationDocument goDoc = (GetObservationDocument) readBodyNodeFrom(envelope, null);
        String actual = goDoc.getGetObservation().getResponseFormat();
        Assert.assertThat(actual, is("http://www.opengis.net/waterml/2.0"));
    }
}
