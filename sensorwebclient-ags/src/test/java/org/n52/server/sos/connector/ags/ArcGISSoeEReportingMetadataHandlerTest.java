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
package org.n52.server.sos.connector.ags;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.server.da.oxf.ResponseFromFileSosAdapter;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

public class ArcGISSoeEReportingMetadataHandlerTest {
    
    private static final String FAKE_URL = "http://points.nowhere";
    
    private static final String VERSION_200 = "2.0.0";

    private ArcGISSoeEReportingMetadataHandlerSeam seam;

    private SOSMetadata metadata;

    
    @Before public void
    setUp() {
        seam = new ArcGISSoeEReportingMetadataHandlerSeam();
        metadata = seam.initMetadata(FAKE_URL, VERSION_200);
    }
    
    @Test public void
    shouldInitEReportingCapabilities() {
        assertNotNull(metadata);
    }
    
    @Test public void
    shouldPerformMetadataCompletion() throws Exception {
        
        // TODO
        SOSMetadata metadata = seam.performMetadataCompletion(FAKE_URL, VERSION_200);
        
    }
    
    static class ArcGISSoeEReportingMetadataHandlerSeam extends ArcGISSoeEReportingMetadataHandler {
        
        private static final String CAPABILITIES_EREPORTING = "/files/capabilities-ereporting.xml";
        
        private static final String SENSOR_NETWORK = "/files/describe-sensor-network_complete.xml";

        public ArcGISSoeEReportingMetadataHandlerSeam() {
            super(createAgsSosMetadata());
        }

        @Override
        protected SOSMetadata initMetadata(String sosUrl, String sosVersion) {
            setSosAdapter(new ResponseFromFileSosAdapter(CAPABILITIES_EREPORTING));
            return super.initMetadata(sosUrl, sosVersion);
        }

        @Override
        protected void performDescribeSensor(String procedure) throws OXFException, ExceptionReport {
            setSosAdapter(new ResponseFromFileSosAdapter(SENSOR_NETWORK));
            super.performDescribeSensor(procedure);
        }
        
        

    }
    
    private static SOSMetadata createAgsSosMetadata() {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        builder
            .addServiceVersion(VERSION_200)
            .addServiceURL(FAKE_URL);
        return new SOSMetadata(builder);
    }

}
