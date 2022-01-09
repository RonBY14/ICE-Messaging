package com.rchat.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

/**
 * Really Simple and common application's implementation for runnable.
 */
public abstract class RunnableImpl implements ThreadControl, Runnable {

    protected Thread thread;
    protected boolean running;

    public RunnableImpl() {
        thread = null;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(String name) {
        if (running)
            return;

        if (name != null)
            thread = new Thread(this, name);
        else
            thread = new Thread(this, this.getClass().getSimpleName());
        thread.start();
    }

    @Override
    public void stop(boolean interrupt) {
        if (!running)
            return;
        running = false;

        if (interrupt)
            thread.interrupt();
    }

    public void doBefore() {}

    public void doAfter() {}

    @Override
    public void clean(@NotNull Closeable @NotNull... closeables) {
        try {
            for (Closeable closeable : closeables) closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
