package com.rchat.net.components;

import com.rchat.net.entities.client.Client;
import com.rchat.net.entities.messages.requests.AuthenticationRequest;
import com.rchat.net.entities.messages.requests.DeliverRequest;
import com.rchat.net.entities.messages.requests.Request;
import com.rchat.net.entities.messages.requests.RequestAcronym;
import com.rchat.net.entities.messages.responses.AlignResponse;
import com.rchat.net.entities.messages.responses.AuthenticationResponse;
import com.rchat.net.entities.messages.responses.BadRequestResponse;
import com.rchat.net.entities.messages.responses.MessageResponse;
import com.rchat.net.utils.AuthenticationResult;
import com.rchat.utils.RunnableImpl;
import com.rchat.utils.ServiceDebugLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles received requests that was parsed by some receiver.
 *
 * @see Receiver
 *
 * @author Ron
 * @since 1.0
 */
public class RequestHandler extends RunnableImpl {

    private final ClientHandler clientHandler;
    private final Distributor distributor;

    /**
     * Queue requests of clients to be handled pushed by their receiver.
     *
     * @see Receiver
     */
    private final Queue<Request> requests;

    public RequestHandler(ClientHandler clientHandler, Distributor distributor) {
        this.clientHandler = clientHandler;
        this.distributor = distributor;

        requests = new ConcurrentLinkedQueue<>();
    }

    /**
     * Push request received by the {@link Receiver}
     * to be handled into the queue.
     *
     * @param request   request to be pushed to the queue and handled.
     */
    public void push(Request request){
        if (request == null)
            throw new NullPointerException("Request can't be NULL!");
        requests.add(request);

        /*
            The Receiver's thread notifies the RequestHandler's
            thread that the requests queue is not empty anymore.
        */
        synchronized (this) { notify(); }
    }

    /**
     * Handle clients requests. One request at a time will
     * be polled out of the queue, and will be handled according
     * to its request-type.
     *
     * Clients may send requests that are invalid or not supported
     * by the server. Such requests, will cause to {@link }
     * to be thrown and response for a bad request to be sent back to the sender.
     */
    private synchronized void handle() {
        while (requests.isEmpty()) {
            ServiceDebugLogger.log(this.getClass(), "Queue is empty...");

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ServiceDebugLogger.log(this.getClass(), "Request detected in the queue. Polling...");
        Request request = requests.poll();

        if (!request.getSender().isAuthenticated()) {
            if (request.getType().equals(RequestAcronym.AUTHENTICATION_REQUEST)) {
                AuthenticationRequest authenticationRequest = (AuthenticationRequest) request;
                Client sender = authenticationRequest.getSender();

                String nickname = authenticationRequest.getNickname();
                AuthenticationResult result = sender.setNickname(nickname);

                if (result.equals(AuthenticationResult.SUCCESS)) {
                    if (!sender.authenticate()) {
                        distributor.push(new AuthenticationResponse(new Client[] { sender }, result));
                        clientHandler.kickUnverifiedClient(sender);
                    } else {
                        distributor.push(new AuthenticationResponse(new Client[] { sender }, result));
                        distributor.push(new AlignResponse(clientHandler.getVerifiedClientsArray(), clientHandler.getVerifiedClientsArray()));
                        ServiceDebugLogger.log(this.getClass(), nickname + " successfully authenticated");
                    }
                } else {
                    ServiceDebugLogger.log(this.getClass(), "Authentication Error! Code: " + result.name());
                    distributor.push(new AuthenticationResponse(new Client[] { sender }, result));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clientHandler.kickUnverifiedClient(sender);
                }
            } else {
                ServiceDebugLogger.log(this.getClass(), "Lack of privilege for an unauthenticated client.");
                handleBadRequest(request);
            }
        } else {
            switch (request.getType()) {
                case RequestAcronym.BAD_REQUEST:
                    handleBadRequest(request);
                    return;
                case RequestAcronym.ALIGN_REQUEST:
                    distributor.push(new AlignResponse(new Client[] { request.getSender() },
                            clientHandler.getVerifiedClientsArray()));
                    break;
                case RequestAcronym.DELIVER_REQUEST:
                    DeliverRequest deliverRequest = (DeliverRequest) request;

                    String from = deliverRequest.getSender().getNickname();
                    String message = deliverRequest.getMessage();

                    ArrayList<Client> clients = new ArrayList<>(Arrays.asList(clientHandler.getVerifiedClientsArray()));
                    clients.remove(request.getSender());
                    distributor.push(new MessageResponse(clients.toArray(new Client[0]), from, message));
                    break;
                default:
                    ServiceDebugLogger.log(this.getClass(), "Unsupported request cannot be handled!");
                    handleBadRequest(request);
            }
        }
        ServiceDebugLogger.log(this.getClass(), "Request successfully handled!");
    }

    private void handleBadRequest(Request request) {
        ServiceDebugLogger.log(this.getClass(), "Bad request cannot be handled!");

        distributor.push(new BadRequestResponse(request.getSender()));

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (request.getSender().isAuthenticated())
            clientHandler.kick(request.getSender().getNickname());
        else
            clientHandler.kickUnverifiedClient(request.getSender());
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
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        doAfter();
    }
}
