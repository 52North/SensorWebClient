/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.n52.server.sos.connector.hydro;

import java.io.IOException;
import net.opengis.sos.x20.GetObservationDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.*;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
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
