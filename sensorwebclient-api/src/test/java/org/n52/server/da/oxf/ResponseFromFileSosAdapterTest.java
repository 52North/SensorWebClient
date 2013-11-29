package org.n52.server.da.oxf;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.ows.ExceptionReport;

public class ResponseFromFileSosAdapterTest {
    
    private static final String FAKE_RESPONSE = "/files/fake-response.xml";

    @Test public void
    shouldSuccessfullyCreateInstanceWithVersionParameter() {
        new ResponseFromFileSosAdapter("NA");
    }
    
    @Test public void
    shouldLoadFakeResponse() throws ExceptionReport, OXFException {
        ResponseFromFileSosAdapter adapter = new ResponseFromFileSosAdapter(FAKE_RESPONSE);
        OperationResult result = adapter.doOperation(null, null);
        assertNotNull(result);
    }
    
    
}
