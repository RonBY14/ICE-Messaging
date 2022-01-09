package com.ice.events.net;

import com.eventsystem.events.Event;

public class ConnectEvent extends Event {

    private final String hostAddress;

    public ConnectEvent(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }
}
