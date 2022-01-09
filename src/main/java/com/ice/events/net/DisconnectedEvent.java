package com.ice.events.net;

import com.eventsystem.events.Event;
import org.jetbrains.annotations.NotNull;

public class DisconnectedEvent extends Event {

    private final DisconnectionReason reason;

    public DisconnectedEvent(@NotNull DisconnectionReason reason) {
        this.reason = reason;
    }

    public DisconnectionReason getReason() {
        return reason;
    }

    public enum DisconnectionReason { CONNECTION_LOST, REQUESTED }
}
