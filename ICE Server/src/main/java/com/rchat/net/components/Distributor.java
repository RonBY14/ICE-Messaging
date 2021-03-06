package com.rchat.net.components;

import com.rchat.net.entities.client.Client;
import com.rchat.net.entities.messages.responses.Response;
import com.rchat.utils.ServiceDebugLogger;
import com.rchat.utils.RunnableImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class Distributor extends RunnableImpl {

    private Queue<Response> responses;

    public Distributor() {
        responses = new ConcurrentLinkedQueue<>();
    }

    /**
     * Push response generated by the {@link RequestHandler}
     * to be sent into the queue.
     *
     * @param response   response pushed in order to be sent.
     */
    public void push(@NotNull Response response){
        if (response == null)
            throw new NullPointerException("Response can't be NULL!");
        responses.add(response);

        /*
            The RequestHandler's thread notifies the Distributor's
            thread that the responses queue is not empty anymore.
         */
        synchronized (this) {
            notify();
        }
    }

    private synchronized void distribute() throws IOException {
        while (responses.isEmpty()) {
            ServiceDebugLogger.log(this.getClass(), "Queue is empty...");

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        ServiceDebugLogger.log(this.getClass(), "Response detected in the queue. Polling...");
        Response response = responses.poll();
        Client[] recipients = response.getRecipients();

        for (int i = 0; i < recipients.length; i++) {
            Client client = recipients[i];

            if (client.getSocket().isConnected() && !client.getSocket().isClosed()) {
                client.getSocket().getOutputStream().write(response.getTextBytes());
                System.out.println("Response sent to " + client.getNickname());
            }
        }
    }

    @Override
    public void doBefore(){
        ServiceDebugLogger.log(this.getClass(), "Started!");
        running = true;
    }

    @Override
    public void doAfter() {
        ServiceDebugLogger.log(this.getClass(), "Terminated!");
    }

    @Override
    public void run() {
        doBefore();

        while (running) {
            try {
                distribute();
            } catch (Exception e) {
                e.printStackTrace();

                ServiceDebugLogger.log(this.getClass(), "Problem occurred causing termination of the Service!");
                running = false;
            }
        }
        doAfter();
    }
}
