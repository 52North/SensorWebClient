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


import java.io.IOException;
import java.util.Collection;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadSoapBodyFromXmlFileViaClassloader;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.n52.shared.serializable.pojos.sos.SosService;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class HydroMetadataHandlerTest {

    private static final String GET_GDA_RESPONSE = "/files/gda_response.xml";

    private static final String FAKE_URL = "http://points.nowhere";

    private static final String VERSION_200 = "2.0.0";

    private HydroMetadataHandlerSeam seam;

    private SOSMetadata metadata;


    @Before public void
    setUp() {
        seam = new HydroMetadataHandlerSeam();
    }

    @Test public void
    shouldParseGetDataAvailabilityResponse() throws Exception {
        Collection<SosTimeseries> timeseries = seam.getAvailableTimeseries();
        assertThat(timeseries.size(), is(140));
    }


    static class HydroMetadataHandlerSeam extends HydroMetadataHandler {

        public HydroMetadataHandlerSeam() {
            super(createMetadata());
        }

        public Collection<SosTimeseries> getAvailableTimeseries() throws XmlException, IOException {
            SosTimeseries template = new SosTimeseries();
            template.setOffering(new Offering("some_offering", FAKE_URL));
            template.setSosService(new SosService(FAKE_URL, VERSION_200));
            XmlObject response = loadSoapBodyFromXmlFileViaClassloader(GET_GDA_RESPONSE, "GetDataAvailabilityResponse", getClass());
            return super.getAvailableTimeseries(response, template, createMetadata());
        }
    }

    private static SOSMetadata createMetadata() {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        builder
            .addServiceVersion(VERSION_200)
            .addServiceURL(FAKE_URL);
        return new SOSMetadata(builder);
    }

}
