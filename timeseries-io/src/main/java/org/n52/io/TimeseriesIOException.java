package org.n52.io;

public class TimeseriesIOException extends Exception {

    private static final long serialVersionUID = -3627963628985404024L;

    public TimeseriesIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeseriesIOException(String message) {
        super(message);
    }
    
}
