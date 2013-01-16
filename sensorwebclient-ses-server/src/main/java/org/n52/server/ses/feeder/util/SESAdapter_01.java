package org.n52.server.ses.feeder.util;

import java.io.IOException;
import java.io.InputStream;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.ISESRequestBuilder;
import org.n52.oxf.ses.adapter.SESAdapter;

/**
 * The Class SESAdapter_01.
 *
 * @author Jan Schulte
 */
public class SESAdapter_01 extends SESAdapter {

    /**
     * Do operation.
     *
     * @param operation the operation
     * @param parameterContainer the parameter container
     * @return the operation result
     * @throws OXFException the oXF exception
     * @see org.n52.oxf.serviceAdapters.ses.SESAdapter#doOperation(org.n52.oxf.owsCommon.capabilities.Operation, org.n52.oxf.serviceAdapters.ParameterContainer)
     */
    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameterContainer) throws OXFException {
        String request = null;
        ISESRequestBuilder requestBuilder = new SESRequestBuilder_01();
        OperationResult result = null;
        if(operation!=null){
                
        // SUBSCRIBE
        if (operation.getName().equals(SESAdapter.SUBSCRIBE)) {
            request = requestBuilder.buildSubscribeRequest(parameterContainer);
            
            // GET_CAPABILITIES
        } else if(operation.getName().equals(SESAdapter.GET_CAPABILITIES)){
            request = requestBuilder.buildGetCapabilitiesRequest(parameterContainer);

            // NOTIFY
        } else if(operation.getName().equals(SESAdapter.NOTIFY)){
            request = requestBuilder.buildNotifyRequest(parameterContainer);

            // REIGSER_PUBLISHER
        } else if(operation.getName().equals(SESAdapter.REGISTER_PUBLISHER)){
            request = requestBuilder.buildRegisterPublisherRequest(parameterContainer);

            // DESCRIBE_SENSOR
        } else if(operation.getName().equals(SESAdapter.DESCRIBE_SENSOR)){
            request = requestBuilder.buildDescribeSensorRequest(parameterContainer);

            // Operation not supported
        } else {
            throw new OXFException("The operation '" + operation.getName() 
                    + "' is not supported."); 
        }
        try {
            InputStream is = IOHelper.sendPostMessage(operation.getDcps()[0]
                                                                          .getHTTPPostRequestMethods().get(0).getOnlineResource()
                                                                          .getHref(), request);

            result = new OperationResult(is, parameterContainer, request);

        } catch (IOException e) {
            throw new OXFException(e);
        }
        }

        return result;
    }
}
