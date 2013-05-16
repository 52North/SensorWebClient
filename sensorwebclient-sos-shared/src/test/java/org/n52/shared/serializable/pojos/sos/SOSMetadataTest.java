package org.n52.shared.serializable.pojos.sos;

import org.junit.Before;
import org.junit.Ignore;


@Ignore
public class SOSMetadataTest {
    
    private SOSMetadata testMetadata;
    
    @Before 
    public void setUp() {
        testMetadata = new SOSMetadata("http://url", "test service", "2.0.0");
    }

}
