package org.n52.shared.session;

public class LoginSessionException extends Exception {

    private static final long serialVersionUID = 7810404255353216606L;

    public LoginSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginSessionException(String message) {
        super(message);
    }
}
