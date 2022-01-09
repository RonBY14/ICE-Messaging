package com.rchat.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

/**
 * Application's common interface of runnable.
 */
public interface ThreadControl {

    void start();

    void start(String name);

    /**
     * A thread must be stopped naturally, so,
     * avoid thread-interruption when not needed.
     *
     * @param interrupt - should be interrupted or not.
     */
    void stop(boolean interrupt);

    /**
     * Do before thread starts (Called internally automatically when starting).
     */
    void doBefore();

    /**
     * Do after thread ends (Called internally automatically when stopping).
     */
    void doAfter();

    /**
     * Used for cleaning/closing resources. This helps to avoid memory leaks.
     *
     * @param closeables - instances that using memory and must be closed.
     */
    void clean(@NotNull Closeable @NotNull... closeables);
}
