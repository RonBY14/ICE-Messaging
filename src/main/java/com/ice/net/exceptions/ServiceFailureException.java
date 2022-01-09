package com.ice.net.exceptions;

public abstract class ServiceFailureException extends RuntimeException {

    public ServiceFailureException() {}

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String cause) {
        super(cause);
    }
}