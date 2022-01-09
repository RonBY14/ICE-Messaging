package com.ice.net.exceptions;

public class UnableToStartServiceException extends ServiceFailureException {

    public UnableToStartServiceException(Throwable cause) {
        super(cause);
    }

    public UnableToStartServiceException(String cause) {
        super(cause);
    }
}
