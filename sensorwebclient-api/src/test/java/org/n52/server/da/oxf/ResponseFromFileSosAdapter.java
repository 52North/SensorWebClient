package org.n52.server.da.oxf;

import static org.junit.Assert.fail;

import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseFromFileSosAdapter extends SOSAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseFromFileSosAdapter.class);
    
    private String file;

    public ResponseFromFileSosAdapter(String fileToRespond) {
        super("2.0.0", (HttpClient) null);
        this.file = fileToRespond;
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
        try {
            XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(file, getClass());
            return new OperationResult(response.newInputStream(), null, null);
        }
        catch (Exception e) {
            LOGGER.error("Could not load response file for testing: {}", file, e);
            fail("Failed to load response file.");
            return null;
        }
    }

    
    
    
}
