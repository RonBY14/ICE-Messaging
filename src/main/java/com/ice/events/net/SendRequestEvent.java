package com.ice.events.net;

import com.eventsystem.events.Event;
import com.ice.net.messages.requests.Request;

public class SendRequestEvent extends Event {

    private Request request;

    public SendRequestEvent(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
