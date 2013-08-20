package org.n52.web;

import org.springframework.http.HttpStatus;


public class ExceptionResponse {
    
    // TODO add userMessage
    
    // TODO add developerMessage
    
    // TODO add url for details

    // TODO make stack tracing configurable
    
    private Throwable exception;

    private HttpStatus statusCode;
    
    private String[] hints;
    
    public static ExceptionResponse createExceptionResponse(Throwable e, HttpStatus statusCode) {
        return new ExceptionResponse(e, statusCode);
    }
    
    public static ExceptionResponse createExceptionResponse(WebException e, HttpStatus statusCode) {
        return new ExceptionResponse(e.getThrowable(), statusCode, e.getHints());
    }
    
    private ExceptionResponse(Throwable e, HttpStatus statusCode) {
        this(e, statusCode, null);
    }
    
    private ExceptionResponse(Throwable e, HttpStatus statusCode, String[] hints) {
        this.statusCode = statusCode;
        this.hints = hints;
        this.exception = e;
    }
    
    public int getStatusCode() {
        return statusCode.value();
    }

    public String getReason() {
        return statusCode.getReasonPhrase();
    }

    public String getMessage() {
        return exception.getMessage();
    }
    
    public String causedBy() {
        Throwable causedBy = exception.getCause();
        return causedBy == null ? null : causedBy.getMessage();
    }

    public String[] getHints() {
        return hints;
    }
    
}
