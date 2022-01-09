package com.ice.events.services;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class NetServiceStartEvent extends ServiceStartEvent {

    private final Socket socket;

    public NetServiceStartEvent(@NotNull Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
