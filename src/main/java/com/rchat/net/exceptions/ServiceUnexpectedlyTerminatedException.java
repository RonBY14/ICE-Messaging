package com.rchat.net.exceptions;

public class ServiceUnexpectedlyTerminatedException extends ServiceFailureException {

    public ServiceUnexpectedlyTerminatedException(Throwable cause) {
        super(cause);
    }
}
