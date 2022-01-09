package com.ice.net.utils;

import org.jetbrains.annotations.NotNull;

public abstract class ServiceDebugLogger {

    public static void log(@NotNull Class<?> service, @NotNull String message) {
        System.out.println("[" + service.getSimpleName() + "] " + message);
    }

    public static void log(@NotNull String service, @NotNull String message) {
        System.out.println("[" + service + "] " + message);
    }
}
