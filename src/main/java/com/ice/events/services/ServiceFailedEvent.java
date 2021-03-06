package com.ice.events.services;

import com.eventsystem.events.Event;
import com.ice.net.exceptions.ServiceFailureException;

public class ServiceFailedEvent extends Event {

    private final ServiceFailureException exception;

    public ServiceFailedEvent(ServiceFailureException exception) {
        this.exception = exception;
    }

    public ServiceFailureException getException() {
        return exception;
    }
}
