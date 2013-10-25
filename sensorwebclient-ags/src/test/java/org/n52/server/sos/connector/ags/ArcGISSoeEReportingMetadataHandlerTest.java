package org.n52.server.sos.connector.ags;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public class ArcGISSoeEReportingMetadataHandlerTest {

    static class ArcGISSoeEReportingMetadataHandlerSeam extends ArcGISSoeEReportingMetadataHandler {
        
        private static final String CAPABILITIES_EREPORTING = "/files/capabilities-ereporting.xml";

        @Override
        protected SOSMetadata initMetadata(String sosUrl, String sosVersion) {
            return super.initMetadata(sosUrl, sosVersion);
        }
        
        
    }
}
