package org.n52.server.da.oxf;

public class ResponseExceedsSizeLimitException extends RuntimeException {

    private static final long serialVersionUID = 6761493818993026789L;

    public ResponseExceedsSizeLimitException(String message) {
        super(message);
    }

    
}
