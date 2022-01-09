package com.ice.net.services;

import com.eventsystem.components.EventBus;
import com.eventsystem.events.Event;
import com.ice.events.net.SendRequestEvent;
import com.ice.events.services.NetServiceStartEvent;
import com.ice.models.DataAccess;
import com.ice.net.messages.requests.Request;
import com.ice.net.utils.ServiceDebugLogger;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sends submitted requests received by other
 * services to the server.
 */
public class Sender extends AbstractRunnableService {

    public static final String SENDER_TOPIC = "Sender";

    // Queue of requests to be sent
    private final BlockingQueue<Request> requests;
    // The request currently being handled
    private Request request;

    // Connected to the socket's output stream
    private DataOutputStream outStream;

    public Sender(EventBus eventBus, DataAccess dataAccess) {
        super(eventBus, dataAccess);

        eventBus.createTopic(SENDER_TOPIC);
        eventBus.subscribe(this, SENDER_TOPIC);

        requests = new LinkedBlockingQueue<>();
        request = null;
    }

    protected void setup(@NotNull NetServiceStartEvent event) throws IOException {
        outStream = new DataOutputStream(event.getSocket().getOutputStream());
    }

    protected void teardown() {
        requests.clear();
        clean(outStream);
    }

    /**
     * Each spin polls a request from the requests queue
     * to be handled and sent to the server.
     *
     * @throws IOException - if stream problems occurred.
     */
    private synchronized void send() throws IOException {
        try {
            request = requests.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        ServiceDebugLogger.log(this.getClass(), "Sending Message...");

        outStream.write(request.getTextBytes());
        dataAccess.getSessionData().addBytesSent(request.getTextBytes().length);

        ServiceDebugLogger.log(this.getClass(), "Message Sent!\n\n" + request.getTextContent());
    }

    @Override
    public void handle(Event event) {
        if (event instanceof SendRequestEvent) {
            if (!thread.isAlive())
                return;
            requests.offer(((SendRequestEvent) event).getRequest());
        } else {
            super.handle(event);
        }
    }

    @Override
    public void run() {
        ServiceDebugLogger.log(this.getClass(), "Started!");

        while (!terminated) {
            try {
                send();
            } catch (Exception e) {
                e.printStackTrace();
                initiateUnexpectedTermination(e);
            }
        }
        ServiceDebugLogger.log(this.getClass(), "Terminated!");
    }
}
