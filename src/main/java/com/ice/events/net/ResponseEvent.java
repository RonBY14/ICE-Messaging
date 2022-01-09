package com.ice.events.net;

import com.eventsystem.events.Event;
import com.ice.net.messages.responses.Response;
import org.jetbrains.annotations.NotNull;

public class ResponseEvent extends Event {

    private final Response response;

    public ResponseEvent(@NotNull Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
