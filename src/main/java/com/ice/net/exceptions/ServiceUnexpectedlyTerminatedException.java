package com.ice.net.exceptions;

public class ServiceUnexpectedlyTerminatedException extends ServiceFailureException {

    public ServiceUnexpectedlyTerminatedException(Throwable cause) {
        super(cause);
    }
}
