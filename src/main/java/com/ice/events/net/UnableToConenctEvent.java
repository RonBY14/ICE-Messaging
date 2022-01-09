package com.ice.events.net;

import com.eventsystem.events.Event;

public class UnableToConenctEvent extends Event {

    private final Throwable throwable;

    public UnableToConenctEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
